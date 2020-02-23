package com.example.giveandgetapp;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity {

    private SessionManager _sessionManager;
    private Database _database;
    private ImageView _imageActor;
    private ImageView _imageMore;
    private TextView _txtTimePost;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        this.postId = getIntent().getIntExtra("Post_Id",0);

        _sessionManager = new SessionManager(this);
        final User user = _sessionManager.getUserDetail();
        _txtTimePost = findViewById(R.id.timelogin);
        _txtActorName = findViewById(R.id.actornamedetail);
        _txtContentPost = findViewById(R.id.txtcontenpost);
        _txtTitlePost = findViewById(R.id.txttitlepost);
        _imageDetailPost = findViewById(R.id.imagedetailpost);
        _indicatorDetail = findViewById(R.id.indicatordetail);
        _imageMore = findViewById(R.id.iconmoredetail);
        _imageActor = findViewById(R.id.avatarActor);
        _dialogLayout = findViewById(R.id.dialog);
        _imgLike = findViewById(R.id.imageButton2);
        _imgReceive = findViewById(R.id.imageButton3);
        _database = new Database(this);
        final User currentUser = _sessionManager.getUserDetail();
        Connection con = _database.connectToDatabase();
        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.Image, p.Image2, p.Image3" +
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
                boolean isLiked = (result.getInt("IsLiked")==1)?true:false;
                boolean isReceived = (result.getInt("IsReceived")==1)?true:false;
                Bitmap actorImage = _database.getImageInDatabase(con,result.getInt("ActorImage"));
                Bitmap Image = _database.getImageInDatabase(con,result.getInt("Image"));
                Bitmap Image2 = _database.getImageInDatabase(con,result.getInt("Image2"));
                Bitmap Image3 = _database.getImageInDatabase(con,result.getInt("Image3"));
                item = new FeedItem(postId,actorId,actorImage,actorName,title,contents,Image,Image2,Image3,isLiked,isReceived);

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

                final Button _baocao = (Button) abc.findViewById(R.id.btnBaocao);
//                  _baocao.setVisibility(View.INVISIBLE);
                final Button _tuychon = (Button) abc.findViewById(R.id.btntuychon);
                final Button _luunoidungbaocao = (Button) abc.findViewById(R.id.luunoidungbaocao);
                final EditText _noidungbaocao = (EditText) abc.findViewById(R.id.txtnoidungbaocao);
                Button _huyDialog = (Button) abc.findViewById(R.id.tatdialog);

                if(item.actorId == user.id)
                {
                    //User là actor

//                    _baocao.setVisibility(View.GONE);
                    _baocao.setText("Xóa bài viết");
                    _tuychon.setText("Chỉnh sửa bài viết");

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
                                        String query = "INSERT INTO [Report]" +
                                                "   (PostId ,UserId ,Contents)" +
                                                "    VALUES" +
                                                "           ("+item.postId +
                                                "           ,"+currentUser.id+
                                                "           ,"+_noidungbaocao.getText().toString()+")";
                                        _database.excuteCommand(con,query);
                                        con.close();
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
}
