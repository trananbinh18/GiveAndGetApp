package com.example.giveandgetapp;

import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.sql.Connection;

public class RatingActivity extends AppCompatActivity {

    private Database _database;
    private SessionManager _sessionManager;
    private ImageView _actorImageRating;
    private TextView _txtTitlePostRating;
    private TextView _txtContentPostRating;
    private RatingBar _ratingBar;
    private Button _btnLuuRating;
    private Button _btnHuyRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        _actorImageRating = findViewById(R.id.avatarActorRating);
        _txtTitlePostRating = findViewById(R.id.titlePostRating);
        _txtContentPostRating = findViewById(R.id.contentPostRating);
        _ratingBar = findViewById(R.id.ratingBar);
        _btnLuuRating = findViewById(R.id.btnLuuRating);
        _btnHuyRating = findViewById(R.id.btnHuyRating);
        _sessionManager = new SessionManager(this);
        final User user = _sessionManager.getUserDetail();
        _database = new Database(this);
        final User currentUser = _sessionManager.getUserDetail();
        Connection con = _database.connectToDatabase();
        String queryuser = "SELECT * FROM Post WHERE Id = 1";
        String queryrating = "";



        //Button Luu
        _btnLuuRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //Button Huy
        _btnLuuRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Ratingbar

    }
}
