package com.example.giveandgetapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.FeedListAdapter;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.example.giveandgetapp.database.UserGiven;
import com.example.giveandgetapp.database.UserGivenAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GiveActivity extends AppCompatActivity {

    private ListView _listviewUserGiveActivity;
    private ImageView _postImageGiveActivity;
    private TextView _postTitleGiveActivity;
    private ImageButton _btnRandom;
    private Button _btnApprove;

    private FeedListAdapter _adapterGiveActivity;
    private ArrayList<UserGiven> _listFeedItemGiveActivity;
    private Database _database;
    private UserGivenAdapter _adapter;
    private Bitmap _postImage;
    private String _postTitle;
    private int _postId;
    private int _giveType;
    private User _currentUser;
    private SessionManager _sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);
        _listviewUserGiveActivity = findViewById(R.id.listviewusergiveactivity);
        _postImageGiveActivity = findViewById(R.id.postImagegiveactivity);
        _postTitleGiveActivity = findViewById(R.id.posttitlegiveactivity);
        _btnRandom = findViewById(R.id.btnRandom);
        _btnApprove = findViewById(R.id.btnApprove);

        _sessionManager = new SessionManager(this);
        _currentUser = _sessionManager.getUserDetail();

        this._postId = getIntent().getIntExtra("Post_Id",0);

        _database = new Database(this);
        Connection con = _database.connectToDatabase();
        String queryToGetPost = "select Title, Image, GiveType from [Post] where Id = "+this._postId;
        String query = "select [User].Id , [User].Name , [User].Avatar from Receive inner join [User] on [User].Id = Receive.UserId where PostId = "+this._postId;
        ResultSet result = _database.excuteCommand(con, query);
        ResultSet resultPost = _database.excuteCommand(con, queryToGetPost);
        _listFeedItemGiveActivity = new ArrayList<UserGiven>();

        //Get Post
        try{
            if (resultPost.next()){
                _postTitle = resultPost.getString("Title");
                _giveType = resultPost.getInt("GiveType");
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

        //Pick
        if(this._giveType == 1){
            _btnRandom.setVisibility(View.GONE);
        }


        _adapter = new UserGivenAdapter(this,_listFeedItemGiveActivity, _giveType);

        _listviewUserGiveActivity.setAdapter(_adapter);
        _postImageGiveActivity.setImageBitmap(_postImage);
        _postTitleGiveActivity.setText(_postTitle);

        //Set listener for 2 button
        _btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<_listviewUserGiveActivity.getChildCount();i++){
                    ImageButton imgBtn = _listviewUserGiveActivity.getChildAt(i).findViewById(R.id.iconGiven);
                    imgBtn.setVisibility(View.GONE);
                }

                int max = _adapter.getCount()-1;
                int min = 0;

                int randomPosition = (int)Math.floor(Math.random() * ((max - min) + 1) + min);
                UserGiven item = (UserGiven)_adapter.getItem(randomPosition);
                _adapter._idUserChoosed = item.userId;
                ImageButton imgBtn = _listviewUserGiveActivity.getChildAt(randomPosition).findViewById(R.id.iconGiven);
                imgBtn.setImageResource(R.drawable.ic_hand_fill_foreground);
                imgBtn.setVisibility(View.VISIBLE);

            }
        });


        _btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idUserChoosed = _adapter._idUserChoosed;
                if(idUserChoosed != 0){
                    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String create_date = formater.format(date);

                    String query = "UPDATE [Post]" +
                            " SET Receiver = " + idUserChoosed +
                            ", Status = 3"+
                            " WHERE Id = "+_postId;

                    String queryAddNotification = "INSERT INTO [Notification]" +
                            "           (UserId,PostId,Status,CreateDate,Title,Contents,Type)" +
                            "     VALUES" +
                            "           ("+idUserChoosed +
                            "           ,"+_postId +
                            "           ,1" +
                            "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                            "           ,N'Xác bạn đã nhận được món đồ'" +
                            "           ,N'Bạn nhận được một món đồ của từ bài post "+_postTitle+"'" +
                            "           ,3)";

                    Connection con = _database.connectToDatabase();

                    try {
                    _database.excuteCommand(con,query);
                    _database.excuteCommand(con,queryAddNotification);
                    String queryEditNotification = "UPDATE [Notification]" +
                                "   SET Status = 2"+
                                "   WHERE UserId="+_currentUser.id+
                                "       AND PostId="+_postId+
                                "       AND Type = 2";

                    _database.excuteCommand(con,queryEditNotification);

                    con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Intent data = new Intent();
                    data.setData(Uri.parse(_postId+""));
                    setResult(RESULT_OK, data);

                    finish();


                }else{
                    Toast.makeText(getApplicationContext(),"Hãy chọn người nhận trước khi xác nhận",Toast.LENGTH_LONG).show();
                }


            }
        });



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
