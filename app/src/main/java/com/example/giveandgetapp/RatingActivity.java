package com.example.giveandgetapp;

import android.content.Intent;
import android.net.Uri;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RatingActivity extends AppCompatActivity {

    private Database _database;
    private SessionManager _sessionManager;
    private ImageView _actorImageRating;
    private TextView _txtTitlePostRating;
    private TextView _txtContentPostRating;
    private RatingBar _ratingBar;
    private Button _btnLuuRating;
    private Button _btnHuyRating;
    private TextView _txtMain;
    private Button _btnApprove;
    private Button _btnDenied;
    private int _postId;
    private User _currentUser;
    private int _actorId;
    private String _postTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        this._postId = getIntent().getIntExtra("Post_Id",0);

        _actorImageRating = findViewById(R.id.avatarActorRating);
        _txtTitlePostRating = findViewById(R.id.titlePostRating);
        _txtContentPostRating = findViewById(R.id.contentPostRating);
        _ratingBar = findViewById(R.id.ratingBar);
        _btnLuuRating = findViewById(R.id.btnLuuRating);
        _btnHuyRating = findViewById(R.id.btnHuyRating);
        _txtMain = findViewById(R.id.txtMain);
        _btnApprove = findViewById(R.id.btnApprove);
        _btnDenied = findViewById(R.id.btnDenied);
        _sessionManager = new SessionManager(this);
        _database = new Database(this);
        _currentUser = _sessionManager.getUserDetail();
        Connection con = _database.connectToDatabase();
        String queryPost = "SELECT * FROM Post WHERE Id = "+_postId;
        ResultSet rs = _database.excuteCommand(con,queryPost);

        try {
            if(rs.next()){
                _txtTitlePostRating.setText(rs.getString("Title"));
                _txtContentPostRating.setText(rs.getString("Contents"));
                _actorId = rs.getInt("Actor");
                _postTitle = rs.getString("Title");
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Button Approve
        _btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _txtMain.setText("Đánh giá bài viết này");
                _ratingBar.setVisibility(View.VISIBLE);
                _btnLuuRating.setVisibility(View.VISIBLE);
                _btnApprove.setVisibility(View.INVISIBLE);
                _btnDenied.setVisibility(View.INVISIBLE);
            }
        });

        //Button Denied
        _btnDenied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Connection con = _database.connectToDatabase();

                String queryEditNotification = "UPDATE [Notification]" +
                        "   SET Status = 2"+
                        "   WHERE UserId="+_currentUser.id+
                        "       AND PostId="+_postId+
                        "       AND Type = 3";

                _database.excuteCommand(con,queryEditNotification);

                String queryEditPost = "UPDATE [Post]" +
                        "   SET Status = 2" +
                        "   WHERE Id = " + _postId;

                _database.excuteCommand(con,queryEditPost);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String create_date = formater.format(date);
                String queryNotification = "INSERT INTO [Notification]" +
                        "           (UserId,PostId,Status,CreateDate,Title,Contents,Type)" +
                        "     VALUES" +
                        "           ("+_actorId +
                        "           ,"+_postId +
                        "           ,1" +
                        "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                        "           ,N'"+_currentUser.name+" Đã từ trối xác nhận món đồ này'" +
                        "           ,N'Hãy chọn lại người nhận'" +
                        "           ,2)";

                _database.excuteCommand(con,queryNotification);


                finish();
            }
        });


        //Button Luu
        _btnLuuRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float star = _ratingBar.getRating();
                Connection con = _database.connectToDatabase();
                String queryGet = "SELECT RatingCount, NumberPostHadRated FROM [User] WHERE Id = "+_actorId;
                ResultSet resultSet = _database.excuteCommand(con,queryGet);
                try {
                    if(resultSet.next()){
                        float ratingCount = resultSet.getFloat("RatingCount");
                        int numberPostHadRated = resultSet.getInt("NumberPostHadRated");

                        float newRatingCount = ((ratingCount*numberPostHadRated)+star)/(numberPostHadRated+1);
                        int newNumberPostHadRated = numberPostHadRated+1;

                        String queryEdit = "UPDATE [User]" +
                                "   SET RatingCount = "+newRatingCount +
                                "      ,NumberPostHadRated = "+ newNumberPostHadRated +
                                "   WHERE Id = " + _actorId;

                        _database.excuteCommand(con,queryEdit);

                        String queryEditPost = "UPDATE [Post]" +
                                "   SET Status = 4" +
                                "   WHERE Id = " + _postId;

                        _database.excuteCommand(con,queryEditPost);

                        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        String create_date = formater.format(date);
                        String queryNotification = "INSERT INTO [Notification]" +
                                "           (UserId,PostId,Status,CreateDate,Title,Contents,Type)" +
                                "     VALUES" +
                                "           ("+_actorId +
                                "           ,"+_postId +
                                "           ,1" +
                                "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                                "           ,N'Bạn đã được đánh giá'" +
                                "           ,N'Bạn đã được đánh giá "+star+" sao bài: "+_postTitle+"'" +
                                "           ,0)";

                        _database.excuteCommand(con,queryNotification);

                        String queryEditNotification = "UPDATE [Notification]" +
                                "   SET Status = 2"+
                                "   WHERE UserId="+_currentUser.id+
                                "       AND PostId="+_postId+
                                "       AND Type = 3";

                        _database.excuteCommand(con,queryEditNotification);

                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Intent data = new Intent();
                data.setData(Uri.parse(_postId+""));
                setResult(RESULT_OK, data);

                finish();

            }
        });


        //Button Huy
        _btnHuyRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Ratingbar

    }
}
