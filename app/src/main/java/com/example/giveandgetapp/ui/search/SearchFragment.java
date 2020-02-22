package com.example.giveandgetapp.ui.search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Catalog;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.ResultSearch;
import com.example.giveandgetapp.database.ResultSearchAdapter;
import com.example.giveandgetapp.database.UserGiven;
import com.example.giveandgetapp.database.UserGivenAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    //Component
    private Database _database;
    private Spinner _spinnerSelectCatalogi;
    private ImageButton _imgbtnSearch;
    private EditText _txtTypeTitleSearch;
    private ListView _listviewSearch;
    private SearchViewModel searchViewModel;
    private ResultSearchAdapter _adapter;
    private ArrayList<ResultSearch> _listResultSearchFragment;
    private Bitmap _postImage;
    private String _postTitle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        _spinnerSelectCatalogi = root.findViewById(R.id.searchtypecatalogi);
        _txtTypeTitleSearch = root.findViewById(R.id.searchtypetitle);
        _imgbtnSearch = root.findViewById(R.id.imgbtnsearch);
        _listviewSearch = root.findViewById(R.id.listviewResultSearch);
        _database = new Database(root.getContext());

        //Get text from Spinner and EditText

        Connection con = _database.connectToDatabase();

        //Load catalogi in spinner
        String query = "SELECT * FROM [Catalog]";
        ResultSet rs = _database.excuteCommand(con, query);

        //Load value to Spinner
        try {
            ArrayList<Catalog> arrayData = new ArrayList<Catalog>();
            int idHint = 0;
            String nameHint = "Tất cả";
            Catalog hint = new Catalog(idHint, nameHint);
            arrayData.add(hint);
            while (rs.next()){
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                Catalog element = new Catalog(id, name);
                arrayData.add(element);
            }
            ArrayAdapter<Catalog> adapterSpiner = new ArrayAdapter<Catalog>(root.getContext(), android.R.layout.simple_spinner_dropdown_item, arrayData);
            adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _spinnerSelectCatalogi.setAdapter(adapterSpiner);

        }catch (SQLException e) {
            e.printStackTrace();
        }

        String editText = _txtTypeTitleSearch.getText().toString();
        _listResultSearchFragment = new ArrayList<ResultSearch>();
        //Action Search
       _imgbtnSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(_spinnerSelectCatalogi.getSelectedItem().toString().contains("Tất cả")){
                   String queryAllPost = "SELECT * FROM [Post] WHERE [Post].Title LIKE '%"+editText+"%'";
                   ResultSet rsAllPost = _database.excuteCommand(con, queryAllPost);
                   try{
                       while (rsAllPost.next()){
                           int _postid = rsAllPost.getInt("Id");
                           String _posttitle = rsAllPost.getString("Title");
                           Bitmap _postimage = _database.getImageInDatabase(con,rsAllPost.getInt("Image"));
                           ResultSearch item = new ResultSearch(_postid,_posttitle,_postimage);
                           _listResultSearchFragment.add(item);
                       }

                   }catch (SQLException e){
                       e.printStackTrace();
                   }
               }

               else {
                   String catalogId = ((Catalog)_spinnerSelectCatalogi.getSelectedItem()).id+"";
                   String querySearch = "SELECT * FROM [Post] WHERE CatalogId = "+catalogId+" and Title LIKE '%"+editText+"%' ";
                   ResultSet rsSearch = _database.excuteCommand(con, querySearch);
                   //Get Post
                   try{
                       while (rsSearch.next()){
                           int _postid = rsSearch.getInt("Id");
                           String _posttitle = rsSearch.getString("Title");
                           Bitmap _postimage = _database.getImageInDatabase(con,rsSearch.getInt("Image"));
                           ResultSearch item = new ResultSearch(_postid,_posttitle,_postimage);
                           _listResultSearchFragment.add(item);
                       }
                       con.close();
                   }catch (SQLException e){
                       e.printStackTrace();
                   }
               }
               _adapter = new ResultSearchAdapter(root.getContext(), _listResultSearchFragment);
               _listviewSearch.setAdapter(_adapter);
           }
       });
        return root;
    }
    @Override
    public void onDestroy() {
        for (ResultSearch resultSearch: _listResultSearchFragment) {
            if(resultSearch.postImage != null){
                resultSearch.postImage.recycle();
            }
        }
        super.onDestroy();
    }
}