package com.example.giveandgetapp.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    //Component
    private Database _database;
    private Spinner _spinnerSelectCatalogi;
    private ImageButton _imgbtnSearch;
    private EditText _txtTypeTitle;

    private SearchViewModel searchViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_search, container, false);
        _spinnerSelectCatalogi = root.findViewById(R.id.searchtypecatalogi);
        _txtTypeTitle = root.findViewById(R.id.searchtypetitle);
        _imgbtnSearch = root.findViewById(R.id.imgbtnsearch);
        _database = new Database(root.getContext());

        //Get text from Spinner and EditText
        //String spinnerText = _spinnerSelectCatalogi.getSelectedItem().toString();
        String editText = _txtTypeTitle.getText().toString();

        final Connection con = _database.connectToDatabase();

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

        String catalogId = ((Catalog)_spinnerSelectCatalogi.getSelectedItem()).id+"";
        String queryAllPost = "SELECT * FROM [Post] LIKE '%"+editText+"%'";
        String querySearch = "SELECT * FROM [Post] WHERE CatalogId = "+catalogId+" and Title LIKE '%"+editText+"%' ";
        final ResultSet rsAllPost = _database.excuteCommand(con, queryAllPost);
        ResultSet rsSearch = _database.excuteCommand(con, querySearch);
        //Action Search
        _imgbtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    while (rsAllPost.next()){
                        Toast.makeText(getActivity(), "Your Text Here!", Toast.LENGTH_SHORT).show();
                    }
                    con.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }

            }
        });

        searchViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}