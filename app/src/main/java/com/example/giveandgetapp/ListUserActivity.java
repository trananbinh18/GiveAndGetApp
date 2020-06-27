package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity {
    private Database _database;
    private ImageView _userImage;
    private TextView _txtUserName;
    private TextView _txtTimeLiked;
    private TextView _txtTimeRegistered;
    private ListView _listUserLikedRegistered;
    private int postId;
    private Activity activity;
//    private SearchViewModel searchViewModel;
    private ResultListUserAdapter _adapter;
    private ArrayList<ResultListUser> _listResultUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        this.activity = this;
        this.postId = getIntent().getIntExtra("Post_Id",0);
        _database = new Database(this);
        _userImage = findViewById(R.id.imageUserListUser);
        _txtUserName = findViewById(R.id.nameUserListUser);
        _txtTimeLiked = findViewById(R.id.timeLikedListUser);
        _txtTimeRegistered = findViewById(R.id.timeRegisterListUser);
        _listUserLikedRegistered = findViewById(R.id.listUser);
        Connection con = _database.connectToDatabase();
        _listResultUser = new ArrayList<ResultListUser>();
        String query = "SELECT u.Id, l.DateTimeLiked, r.DateTimeRegistered, u.Name, u.Avatar, r.PostId AS isHaveRegistered , l.PostId AS isHaveLiked\n" +
                        "FROM [User] u \n" +
                        "LEFT JOIN [Like] l ON  l.UserId= u.Id AND l.PostId = "+postId+"\n" +
                        "LEFT JOIN [Receive] r ON r.UserId = l.UserId AND r.PostId = "+postId+"\t\t";
        ResultSet rs = _database.excuteCommand(con, query);
        try{
            while (rs != null && rs.next()){
                int _userid = rs.getInt("Id");
                Bitmap _userImage = _database.getImageInDatabase(con,rs.getInt("Avatar"));
                String _userName = rs.getString("Name");
//                Date _liked = rs.getDate("DateTimeLiked");
//                Date _registered = rs.getDate("DateTimeRegistered");
                int _isHaveLiked = rs.getInt("isHaveLiked");
                int isHaveRegistered = rs.getInt("isHaveRegistered");
                Timestamp LikedCreateDate = rs.getTimestamp("DateTimeLiked");
                Date likedDate = null;
                if(LikedCreateDate != null){
                    likedDate = new Date(LikedCreateDate.getTime());
                }
                Timestamp RegisteredCreateDate = rs.getTimestamp("DateTimeRegistered");
                Date registeredDate = null;
                if(RegisteredCreateDate != null){
                    registeredDate = new Date(RegisteredCreateDate.getTime());
                }

                ResultListUser item = new ResultListUser(postId,_userid,_userImage,likedDate,registeredDate,_userName);
                if(_isHaveLiked != 0 || isHaveRegistered != 0)
                {
                    _listResultUser.add(item);
                }
            }
            con.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
        _adapter = new ResultListUserAdapter(activity,this, _listResultUser);
        _listUserLikedRegistered.setAdapter(_adapter);
    }
}