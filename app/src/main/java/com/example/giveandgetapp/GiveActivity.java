package com.example.giveandgetapp;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.FeedListAdapter;
import com.example.giveandgetapp.database.UserGiven;
import com.example.giveandgetapp.database.UserGivenAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiveActivity extends AppCompatActivity {

    private ListView _listviewUserGiveActivity;
    private ImageView _postImageGiveActivity;
    private TextView _postTitleGiveActivity;
    private FeedListAdapter _adapterGiveActivity;
    private ArrayList<UserGiven> _listFeedItemGiveActivity;
    private Database _database;
    private UserGivenAdapter _adapter;
    private Bitmap _postImage;
    private String _postTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);
        _listviewUserGiveActivity = findViewById(R.id.listviewusergiveactivity);
        _postImageGiveActivity = findViewById(R.id.postImagegiveactivity);
        _postTitleGiveActivity = findViewById(R.id.posttitlegiveactivity);
        _database = new Database(this);
        Connection con = _database.connectToDatabase();
        String queryToGetPost = "select Title, Image from [Post] where Id = 1 ";
        String query = "select [User].Id , [User].Name , [User].Avatar from Receive inner join [User] on [User].Id = Receive.UserId where PostId = 1";
        ResultSet result = _database.excuteCommand(con, query);
        ResultSet resultPost = _database.excuteCommand(con, queryToGetPost);
        _listFeedItemGiveActivity = new ArrayList<UserGiven>();

        //Get Post
        try{
            if (resultPost.next()){
                _postTitle = resultPost.getString("Title");
                _postImage = _database.getImageInDatabase(con,resultPost.getInt("Image"));
            }

        }catch (SQLException e){
         e.printStackTrace();
        }

        //Get USerGiven
        try{
            while (result.next()){
                int _userid = result.getInt("Id");
                String _userName = result.getString("Name");
                Bitmap _userimage = _database.getImageInDatabase(con,result.getInt("Avatar"));
                UserGiven item = new UserGiven(_userid,_userName,_userimage);
                _listFeedItemGiveActivity.add(item);
            }
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        _adapter = new UserGivenAdapter(this,_listFeedItemGiveActivity);
        _listviewUserGiveActivity.setAdapter(_adapter);
        _postImageGiveActivity.setImageBitmap(_postImage);
        _postTitleGiveActivity.setText(_postTitle);


    }

    @Override
    protected void onDestroy() {
        for (UserGiven user: _listFeedItemGiveActivity) {
            if(user.userImage != null){
                user.userImage.recycle();
            }
        }

        if(_postImage != null){
            _postImage.recycle();
        }
        super.onDestroy();
    }
}
