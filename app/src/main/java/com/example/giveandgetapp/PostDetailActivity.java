package com.example.giveandgetapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.ImageSlideAdapter;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.example.giveandgetapp.ui.dashboard.DashboardFragment;
import com.example.giveandgetapp.ui.dashboard.DashboardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class PostDetailActivity extends AppCompatActivity {

    private SessionManager _sessionManager;
    private Database _database;
    private ImageView _imageActor;
    private ImageView _imageMore;
    private TextView _txtActorName;
    private TextView _txtContentPost;
    private TextView _txtTitlePost;
    private CircleIndicator _indicatorDetail;
    private ViewPager _imageDetailPost;
    private LinearLayout _dialogLayout;
    private FeedItem item;
    private int postId;
    private ImageButton _imgReceive;
    private ImageButton _imgLike;
    private TextView _txtLikeCount;
    private TextView _txtTimelogin;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        this.activity = this;
        this.postId = getIntent().getIntExtra("Post_Id",0);

        _sessionManager = new SessionManager(this);
        final User user = _sessionManager.getUserDetail();
        _txtActorName = findViewById(R.id.timelogin);
        _txtContentPost = findViewById(R.id.txtcontenpost);
        _txtTitlePost = findViewById(R.id.txttitlepost);
        _imageDetailPost = findViewById(R.id.imagedetailpost);
        _indicatorDetail = findViewById(R.id.indicatordetail);
        _imageMore = findViewById(R.id.iconmoredetail);
        _imageActor = findViewById(R.id.avatarActor);
        _dialogLayout = findViewById(R.id.dialog);
        _imgLike = findViewById(R.id.imageButton2);
        _imgReceive = findViewById(R.id.imageButton3);
        _txtLikeCount = findViewById(R.id.txtLikeCountPostdetail);
        _txtTimelogin = findViewById(R.id.actornamedetail);

        _database = new Database(this);
        final User currentUser = _sessionManager.getUserDetail();
        Connection con = _database.connectToDatabase();
        String query = "SELECT p.CreateDate, p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.Image, p.Image2, p.Image3,p.ReceiverCount ,p.LikeCount" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = " + currentUser.id +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = " + currentUser.id +
                " WHERE p.id = "+ postId;

        final ResultSet result = _database.excuteCommand(con, query);

        try
        {
            if(result.next()){
                int postId = result.getInt("Id");
                int actorId = result.getInt("ActorId");
                String actorName = result.getString("ActorName");
                final String title = result.getString("Title");
                String contents = result.getString("Contents");
                boolean isLiked = (result.getInt("IsLiked")==currentUser.id)?true:false;
                boolean isReceived = (result.getInt("IsReceived")==currentUser.id)?true:false;
                Bitmap actorImage = _database.getImageInDatabase(con,result.getInt("ActorImage"));
                Bitmap Image = _database.getImageInDatabase(con,result.getInt("Image"));
                Bitmap Image2 = _database.getImageInDatabase(con,result.getInt("Image2"));
                Bitmap Image3 = _database.getImageInDatabase(con,result.getInt("Image3"));
                int likeCount = result.getInt("LikeCount");
                int receiverCount = result.getInt("ReceiverCount");
                Date createDate = result.getDate("CreateDate");

                item = new FeedItem(postId,actorId,actorImage,actorName,title,contents,Image,Image2,Image3,isLiked,isReceived);
//                item1 = new FeedItem(postId, actorId, actorImageId, actorName, title, contents, imageId, image2Id, image3Id, isLiked, isReceived, createDate, likeCount, receiverCount);

                ArrayList<Bitmap> listImage = new ArrayList<Bitmap>();
                if(item.image != null) {
                    listImage.add(item.image);
                }

                if(item.image2 != null) {
                    listImage.add(item.image2);
                }

                if(item.image3 != null) {
                    listImage.add(item.image3);
                }



                ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(this,listImage,null);
                _imageDetailPost.setAdapter(imageSlideAdapter);
                _indicatorDetail.setViewPager(_imageDetailPost);
                _txtActorName.setText(item.actorName);
                _imageActor.setImageBitmap(item.actorImage);
                _txtTitlePost.setText(item.title);
                _txtContentPost.setText(item.contents);

                //Set Time post
                String timeStr = "";
                if(createDate != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createDate);
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    java.util.Date currentDate = new java.util.Date(System.currentTimeMillis() - (1 * DAY_IN_MS));
                    if(currentDate.before(createDate)){
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(new java.util.Date());
                        int hourBefore = calendar1.get(Calendar.HOUR_OF_DAY) - calendar.get(Calendar.HOUR_OF_DAY);
                        if(hourBefore != 0){
                            timeStr = hourBefore+" giờ trước";
                        }else{
                            timeStr = "gần đây";
                        }

                    }else{
                        int month = calendar.get(Calendar.MONTH)+1;
                        timeStr = "Ngày "+ calendar.get(Calendar.DAY_OF_MONTH)+" Tháng "+ month;
                    }
                }

                _txtTimelogin.setText(timeStr);

                //Set text for like and receiver
                String strLikeCount = (likeCount>0)?likeCount+" lượt thích":"";
                String strReceiverCount = (receiverCount>0)?receiverCount+" lượt đăng ký":"";
                if(strLikeCount =="" || strReceiverCount ==""){
                    _txtLikeCount.setText(strLikeCount+strReceiverCount);
                }else{
                    _txtLikeCount.setText(strLikeCount+" | "+strReceiverCount);
                }

                if(item.isLiked){
                    _imgLike.setImageResource(R.drawable.ic_heart_fill_foreground);
                }else{
                    _imgLike.setImageResource(R.drawable.ic_heart_foreground);
                }

                if(item.isReceiver){
                    _imgReceive.setImageResource(R.drawable.ic_hand_fill_foreground);
                }else{
                    _imgReceive.setImageResource(R.drawable.ic_hand_foreground);
                }
                con.close();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        _imageMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder _dialogPost = new AlertDialog.Builder(PostDetailActivity.this);
                View abc = getLayoutInflater().inflate(R.layout.postdetail_dialog,null);

                _dialogPost.setView(abc);
                final AlertDialog dialog = _dialogPost.create();

                final Button _chodo = abc.findViewById(R.id.btnChoDo);
                final Button _baocao = (Button) abc.findViewById(R.id.btnBaocao);
//                  _baocao.setVisibility(View.INVISIBLE);
                final Button _tuychon = (Button) abc.findViewById(R.id.btntuychon);
                final Button _luunoidungbaocao = (Button) abc.findViewById(R.id.luunoidungbaocao);
                final EditText _noidungbaocao = (EditText) abc.findViewById(R.id.txtnoidungbaocao);
                Button _huyDialog = (Button) abc.findViewById(R.id.tatdialog);

                if(item.actorId == user.id)
                {
                    //User là actor

                    _chodo.setVisibility(View.VISIBLE);
//                    _baocao.setVisibility(View.GONE);
                    _baocao.setText("Xóa bài viết");
                    _tuychon.setText("Chỉnh sửa bài viết");

                    _chodo.setOnClickListener(getButtonGiveClickListener(item.postId));

                    //Button xoa bai viet
                    _baocao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Connection con  = _database.connectToDatabase();
                                String query = "DELETE FROM [Post] " +
                                        "      WHERE Id ="+item.postId;
                                _database.excuteCommand(con,query);
                                con.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            Intent data = new Intent();
                            data.setData(Uri.parse(item.postId+""));
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    });

                    //Button chinh sua bai viet
                    _tuychon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EditPostActivity.class);
                            intent.putExtra("Post_Id",postId);
                            startActivityForResult(intent,13);
                        }
                    });



                }else {
                    //User không phải là actor
                    _baocao.setText("Báo cáo");
                    _tuychon.setVisibility(View.GONE);
                    //Button báo cáo
                    _baocao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _baocao.setVisibility(View.INVISIBLE);
                            _noidungbaocao.setVisibility(View.VISIBLE);
                            _luunoidungbaocao.setVisibility(View.VISIBLE);
                            _luunoidungbaocao.setText("Lưu");
                            _luunoidungbaocao.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Connection con  = _database.connectToDatabase();
                                        String queryCheckExist = "SELECT *" +
                                                "  FROM [Report]" +
                                                "  WHERE PostId = " +item.postId +
                                                "  AND UserId =" + currentUser.id;

                                        ResultSet rs = _database.excuteCommand(con,queryCheckExist);

                                        if(rs.next()){
                                            String query = "UPDATE [Report]" +
                                                    "   SET Contents = '" +_noidungbaocao.getText().toString()+"'"+
                                                    "       WHERE PostId = " +item.postId +
                                                    "       AND UserId =" + currentUser.id;

                                            _database.excuteCommand(con,query);


                                        }else{
                                            String query = "INSERT INTO [Report]" +
                                                    "   (PostId ,UserId ,Contents)" +
                                                    "    VALUES" +
                                                    "           ("+item.postId +
                                                    "           ,"+currentUser.id+
                                                    "           ,'"+_noidungbaocao.getText().toString()+"')";

                                            _database.excuteCommand(con,query);

                                            String queryGetReportCount = "SELECT ReportCount FROM [User] WHERE Id = "+ item.actorId;

                                            ResultSet resultSetReportCount = _database.excuteCommand(con,queryGetReportCount);
                                            if(resultSetReportCount.next()){
                                                int reportCount = resultSetReportCount.getInt("ReportCount");
                                                reportCount++;
                                                String queryUpdateReportCount = "UPDATE [User] " +
                                                        "   SET ReportCount = " +reportCount+
                                                        "       WHERE Id = " + item.actorId;

                                                _database.excuteCommand(con,queryUpdateReportCount);
                                            }
                                        }


                                        con.close();
                                        dialog.cancel();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }


                //Button Hủy
                _huyDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(item.actorImage != null){
            item.actorImage.recycle();
        }
        if(item.image != null){
            item.image.recycle();
        }
        if(item.image2 != null){
            item.image2.recycle();
        }
        if(item.image3 != null){
            item.image3.recycle();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Edit post
        if(requestCode == 13){
            switch (resultCode) {
                //On edit post
                case RESULT_OK:
                    data.setData(Uri.parse(item.postId+""));
                    setResult(RESULT_FIRST_USER, data);
                    finish();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener getButtonGiveClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GiveActivity.class);
                intent.putExtra("Post_Id",postId);
                activity.startActivityForResult(intent,11);
            }
        };

        return listener;
    }
}
