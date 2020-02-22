package com.example.giveandgetapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.CaseMap;
import android.os.Bundle;

import com.example.giveandgetapp.database.Catalog;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditPostActivity extends AppCompatActivity {

    private Database _database;
    private SessionManager _sessionManager;
    private ImageButton _imgbtnPic1EditPost;
    private ImageButton _imgbtnPic2EditPost;
    private ImageButton _imgbtnPic3EditPost;
    private Spinner _cbxCatalogyEditPost;
    private EditText _txtTitleEditPost;
    private EditText _txtContentEditPost;
    private RadioButton _rbtnTuchonEditPost;
    private RadioButton _rbtnNgaunhienEditPost;
    private RadioButton _rbtn3ngayEditPost;
    private RadioButton _rbtn5ngayEditPost;
    private RadioButton _rbtn7ngayEditPost;
    private Button _btnLuuEditPost;
    private Button _btnHuyEditPost;
    private ArrayAdapter<Catalog> adapterSpiner;
    private ArrayList<Catalog> arrayData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        _sessionManager = new SessionManager(this);
        User user = _sessionManager.getUserDetail();
        _imgbtnPic1EditPost = findViewById(R.id.imgPicker1editpost);
        _imgbtnPic2EditPost = findViewById(R.id.imgPicker2editpost);
        _imgbtnPic3EditPost = findViewById(R.id.imgPicker3editpost);
        _cbxCatalogyEditPost = findViewById(R.id.cbxCatalogyeditpost);
        _txtTitleEditPost = findViewById(R.id.txtTitleeditpost);
        _txtContentEditPost = findViewById(R.id.txtContenteditpost);
        _rbtnTuchonEditPost = findViewById(R.id.rdoPickeditpost);
        _rbtnNgaunhienEditPost = findViewById(R.id.rdoRandomeditpost);
        _rbtn3ngayEditPost = findViewById(R.id.rdo3Dayeditpost);
        _rbtn5ngayEditPost = findViewById(R.id.rdo5Dayeditpost);
        _rbtn7ngayEditPost = findViewById(R.id.rdo7Dayeditpost);
        _btnLuuEditPost = findViewById(R.id.btnLuueditpost);
        _btnHuyEditPost = findViewById(R.id.btnHuyeditpost);

        //Load Data
        _database = new Database(this);
        Connection con = _database.connectToDatabase();
        String query = "SELECT * FROM Post WHERE Id = 1";

        //Load data to spinner
        String queryCatalogy = "SELECT * FROM Catalog";
        ResultSet resultSet1 = _database.excuteCommand(con, queryCatalogy);
        try {
            arrayData = new ArrayList<Catalog>();
            while (resultSet1.next()){
                int id = resultSet1.getInt("Id");
                String name = resultSet1.getString("Name");
                Catalog element = new Catalog(id, name);
                arrayData.add(element);
            }
            adapterSpiner = new ArrayAdapter<Catalog>(this, android.R.layout.simple_spinner_dropdown_item, arrayData);
            adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _cbxCatalogyEditPost.setAdapter(adapterSpiner);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //Load Data Post
        ResultSet resultSet = _database.excuteCommand(con, query);
        try {
            if(resultSet.next()){
                _imgbtnPic1EditPost.setImageBitmap(_database.getImageInDatabase(con,resultSet.getInt("Image")));
                _imgbtnPic2EditPost.setImageBitmap(_database.getImageInDatabase(con,resultSet.getInt("Image2")));
                _imgbtnPic3EditPost.setImageBitmap(_database.getImageInDatabase(con,resultSet.getInt("Image3")));
                int catalogId = resultSet.getInt("CatalogId");
                for(int i=0;i<arrayData.size();i++)
                {
                    if(catalogId == arrayData.get(i).id){
                        _cbxCatalogyEditPost.setSelection(i);
                        break;
                    }
                }

                _txtTitleEditPost.setText(resultSet.getString("Title"));
                _txtContentEditPost.setText(resultSet.getString("Contents"));
                if(resultSet.getInt("GiveType") == 1)
                {
                    _rbtnTuchonEditPost.setChecked(true);
                }else if(resultSet.getInt("GiveType") == 2)
                {
                    _rbtnNgaunhienEditPost.setChecked(true);
                }
                if(resultSet.getInt("ExpireType")== 3)
                {
                    _rbtn3ngayEditPost.setChecked(true);
                } else if(resultSet.getInt("ExpireType")== 5)
                {
                    _rbtn5ngayEditPost.setChecked(true);
                }else if(resultSet.getInt("ExpireType")== 7)
                {
                    _rbtn7ngayEditPost.setChecked(true);
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Nút Hủy
        _btnHuyEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}

