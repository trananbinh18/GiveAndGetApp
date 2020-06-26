package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.ResultListUser;
import com.example.giveandgetapp.database.ResultListUserAdapter;
import com.example.giveandgetapp.database.ResultSearch;
import com.example.giveandgetapp.database.ResultSearchAdapter;
import com.example.giveandgetapp.ui.search.SearchViewModel;

import java.sql.Connection;
import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity {
    private Database _database;
    private ImageView _userImage;
    private TextView _txtUserName;
    private TextView _txtTimeLiked;
    private TextView _txtTimeRegistered;
    private ListView _listUserLikedRegistered;
//    private SearchViewModel searchViewModel;
    private ResultListUserAdapter _adapter;
    private ArrayList<ResultListUser> _listResultUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        _database = new Database(this);
        _userImage = findViewById(R.id.imageUserListUser);
        _txtUserName = findViewById(R.id.nameUserListUser);
        _txtTimeLiked = findViewById(R.id.timeLikedListUser);
        _txtTimeRegistered = findViewById(R.id.timeRegisterListUser);
        _listUserLikedRegistered = findViewById(R.id.listUser);
        Connection con = _database.connectToDatabase();

        _listResultUser = new ArrayList<ResultListUser>();

    }
}