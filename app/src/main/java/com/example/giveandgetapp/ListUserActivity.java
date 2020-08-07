package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.ResultListUser;
import com.example.giveandgetapp.database.ResultListUserLikeAdapter;
import com.example.giveandgetapp.database.ResultListUserRegisterAdapter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity {
    private TabHost _tabHost;
    private Database _database;
    private ImageView _userImage;
    private TextView _txtUserName;
    private TextView _txtTimeLiked;
    private TextView _txtTimeRegistered;
    private ListView _listUserLikedRegistered;
    private ListView _listViewUserLiked;
    private ListView _listViewUserRegister;
    private int postId;
    private Activity activity;
//    private SearchViewModel searchViewModel;
    private ResultListUserLikeAdapter _adapterLike;
    private ResultListUserRegisterAdapter _adapterRegister;
    private ArrayList<ResultListUser> _listResultUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        this.activity = this;
        this.postId = getIntent().getIntExtra("Post_Id",0);
        _database = new Database(this);
//        _listUserLikedRegistered = findViewById(R.id.listUser);
        _listViewUserLiked = findViewById(R.id.lstUserLike);
        _listViewUserRegister = findViewById(R.id.lstUserRegister);
        _tabHost = findViewById(R.id.tabhost);
        //setup tabhost
        _tabHost.setup();

        //tab1
        TabHost.TabSpec spec = _tabHost.newTabSpec("Tab một");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Những người dùng đã thích");
        _tabHost.addTab(spec);

        //tab2
        spec = _tabHost.newTabSpec("Tab hai");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Những người dùng đã đăng kí");
        _tabHost.addTab(spec);


        Connection con = _database.connectToDatabase();
        _listResultUser = new ArrayList<ResultListUser>();
        String query = "SELECT u.Id, l.DateTimeLiked, r.DateTimeRegistered, u.Name, u.Avatar, r.PostId AS isHaveRegistered , l.PostId AS isHaveLiked\n" +
                        "FROM [User] u \n" +
                        "LEFT JOIN [Like] l ON  l.UserId= u.Id AND l.PostId = "+postId+"\n" +
                        "LEFT JOIN [Receive] r ON r.UserId = u.Id AND r.PostId = "+postId+"\t\t";
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

        ArrayList<ResultListUser> listLike = new ArrayList<>();
        ArrayList<ResultListUser> listRegister = new ArrayList<>();
        for (ResultListUser item :_listResultUser){
            if(item.timeLiked != null){
                listLike.add(item);
            }

            if(item.timeRegistered != null){
                listRegister.add(item);
            }
        }
        _adapterLike = new ResultListUserLikeAdapter(activity,this, listLike);
        _listViewUserLiked.setAdapter(_adapterLike);

        _adapterRegister = new ResultListUserRegisterAdapter(activity,this, listRegister);
        _listViewUserRegister.setAdapter(_adapterRegister);


    }
}