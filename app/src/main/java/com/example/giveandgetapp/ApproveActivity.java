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

public class ApproveActivity extends AppCompatActivity {

    private Database _database;
    private SessionManager _sessionManager;
    private ImageView _actorImageRating;
    private TextView _txtTitlePostRating;
    private TextView _txtContentPostRating;
    private Button _btnHuyRating;
    private TextView _txtMain;
    private Button _btnApprove;
    private Button _btnDenied;
    private Button _btnConfirmDenied;
    private Button _btnBack;
    private int _postId;
    private User _currentUser;
    private int _actorId;
    private String _postTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);
        this._postId = getIntent().getIntExtra("Post_Id",0);
        _actorImageRating = findViewById(R.id.avatarActorRating);
        _txtTitlePostRating = findViewById(R.id.titlePostRating);
        _txtContentPostRating = findViewById(R.id.contentPostRating);
        _btnHuyRating = findViewById(R.id.btnHuyRating);
        _txtMain = findViewById(R.id.txtMain);
        _btnApprove = findViewById(R.id.btnApprove);
        _btnDenied = findViewById(R.id.btnDenied);
        _btnBack = findViewById(R.id.btnBack);
        _btnConfirmDenied = findViewById(R.id.btnConfirmDenied);
        _sessionManager = new SessionManager(this);
        _database = new Database(this);
        _currentUser = _sessionManager.getUserDetail();
        Connection con = _database.connectToDatabase();
        String queryPost = "SELECT *, Avatar FROM [Post] INNER JOIN [User] u ON Post.Actor = u.Id WHERE Post.Id = "+_postId;
        ResultSet rs = _database.excuteCommand(con,queryPost);

        try {
            if(rs.next()){
                _txtTitlePostRating.setText(rs.getString("Title"));
                _txtContentPostRating.setText(rs.getString("Contents"));
                _actorImageRating.setImageBitmap(_database.getImageInDatabase(con,rs.getInt("Avatar")));
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
                Connection con = _database.connectToDatabase();
                String queryEditPost = "UPDATE [Post]" +
                        "   SET Status = 4" +
                        "   WHERE Id = " + _postId;


                String queryEditNotification = "UPDATE [Notification]" +
                        "   SET Status = 2"+
                        "   WHERE UserId="+_currentUser.id+
                        "       AND PostId="+_postId+
                        "       AND Type = 4";

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
                        "           ,N'"+_currentUser.name+" đã xác nhận món đồ này'" +
                        "           ,N'Người được chọn đã xác nhận nhận món đồ này.'" +
                        "           ,1)";


                _database.excuteCommand(con,queryNotification);
                _database.excuteCommand(con,queryEditNotification);
                _database.excuteCommand(con, queryEditPost);

                Intent data = new Intent();
                data.setData(Uri.parse(_postId+","+_actorId));
                setResult(RESULT_OK, data);
                finish();


            }
        });

        //Button Confirm Deny
        _btnConfirmDenied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnDenied.setVisibility(View.VISIBLE);
                _btnBack.setVisibility(View.VISIBLE);
                _btnApprove.setVisibility(View.INVISIBLE);
                _btnConfirmDenied.setVisibility(View.INVISIBLE);
                _txtMain.setText("Bạn có thật sự không muốn nhận món đồ này ?");
            }
        });

        //Button Back
        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnDenied.setVisibility(View.INVISIBLE);
                _btnBack.setVisibility(View.INVISIBLE);
                _btnApprove.setVisibility(View.VISIBLE);
                _btnConfirmDenied.setVisibility(View.VISIBLE);
                _txtMain.setText("Bạn đã được cho món đồ này bạn có muốn xác nhận đã nhận món đồ này?");
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
                        "       AND Type = 4";

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
                        "           ,N'"+_currentUser.name+" đã từ chối xác nhận món đồ này'" +
                        "           ,N'Hãy chọn lại người nhận'" +
                        "           ,2)";

                _database.excuteCommand(con,queryNotification);

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
    }

}
