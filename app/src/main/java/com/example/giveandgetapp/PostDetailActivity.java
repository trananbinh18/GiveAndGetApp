package com.example.giveandgetapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.ImageSlideAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity {

    private Database _database;
    private ImageView _imageActor;
    private TextView _txtTimePost;
    private TextView _txtActorName;
    private TextView _txtCatalogiPost;
    private TextView _txtTitlePost;
    private CircleIndicator _indicatorDetail;
    private ViewPager _imageDetailPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        _txtTimePost = findViewById(R.id.timelogin);
        _txtActorName = findViewById(R.id.actornamedetail);
        _txtCatalogiPost = findViewById(R.id.txtcatalogipost);
        _txtTitlePost = findViewById(R.id.txttitlepost);
        _imageDetailPost = findViewById(R.id.imagedetailpost);
        _indicatorDetail = findViewById(R.id.indicatordetail);

        _database = new Database(this);
        Connection con = _database.connectToDatabase();
        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.Image, p.Image2, p.Image3" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = 1" +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = 1" +
                " WHERE p.id = 1";

        ResultSet result = _database.excuteCommand(con, query);

        try
        {
            if(result.next()){
                int postId = result.getInt("Id");
                int actorId = result.getInt("ActorId");
                String actorName = result.getString("ActorName");
                String title = result.getString("Title");
                String content = result.getString("Contents");
                boolean isLiked = (result.getInt("IsLiked")==1)?true:false;
                boolean isReceived = (result.getInt("IsReceived")==1)?true:false;
                Bitmap actorImage = _database.getImageInDatabase(con,result.getInt("ActorImage"));
                Bitmap Image = _database.getImageInDatabase(con,result.getInt("Image"));
                Bitmap Image2 = _database.getImageInDatabase(con,result.getInt("Image2"));
                Bitmap Image3 = _database.getImageInDatabase(con,result.getInt("Image3"));
                FeedItem item = new FeedItem(postId,actorId,actorImage,actorName,title,content,Image,Image2,Image3,isLiked,isReceived);

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

                ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(this,listImage);
                _imageDetailPost.setAdapter(imageSlideAdapter);
                _indicatorDetail.setViewPager(_imageDetailPost);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

            }
        }
