package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private SessionManager sessionManager;
    private User currentUser;
    private Database database;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems){
        this.activity = activity;
        this.feedItems = feedItems;
        this.sessionManager = new SessionManager(activity.getApplicationContext());
        this.currentUser = this.sessionManager.getUserDetail();
        this.database = new Database(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return this.feedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        ImageButton actorImage  = (ImageButton) convertView.findViewById(R.id.actorImage);
        TextView actorName = (TextView) convertView.findViewById(R.id.actorName);
        TextView createPostTime = (TextView) convertView.findViewById(R.id.createPostTime);
        TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView content = (TextView) convertView.findViewById(R.id.txtContent);
        ViewPager pageImage = (ViewPager) convertView.findViewById(R.id.pageImage);
        CircleIndicator indicator = (CircleIndicator) convertView.findViewById(R.id.indicator);
        ImageButton imgLike = (ImageButton) convertView.findViewById(R.id.imgLike);
        ImageButton imgReceive = (ImageButton) convertView.findViewById(R.id.imgReceive);


        final FeedItem item = feedItems.get(position);
        //Image Paging
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

        ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(convertView.getContext(),listImage);
        pageImage.setAdapter(imageSlideAdapter);
        indicator.setViewPager(pageImage);


        actorImage.setImageBitmap(item.actorImage);
        actorName.setText(item.actorName);
        title.setText(item.title);
        content.setText(item.contents);

        if(item.isLiked){
            imgLike.setImageResource(R.drawable.ic_heart_fill_foreground);
        }else{
            imgLike.setImageResource(R.drawable.ic_heart_foreground);
        }

        if(item.isReceiver){
            imgReceive.setImageResource(R.drawable.ic_hand_fill_foreground);
        }else{
            imgReceive.setImageResource(R.drawable.ic_hand_foreground);
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isLiked = !item.isLiked;
                ImageButton imgLike = (ImageButton) v;
                if(item.isLiked){
                    imgLike.setImageResource(R.drawable.ic_heart_fill_foreground);
                    likePost(item.postId);
                }else{
                    imgLike.setImageResource(R.drawable.ic_heart_foreground);
                    unLikePost(item.postId);
                }
            }
        });

        imgReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isReceiver = !item.isReceiver;
                ImageButton imgReceive = (ImageButton) v;
                if(item.isReceiver){
                    imgReceive.setImageResource(R.drawable.ic_hand_fill_foreground);
                    receivePost(item.postId);
                }else{
                    imgReceive.setImageResource(R.drawable.ic_hand_foreground);
                    unReceivePost(item.postId);
                }
            }
        });


        return convertView;
    }

    private void likePost(int postId){
        Connection con = database.connectToDatabase();
        String query = "INSERT INTO [Like](UserId,PostId)VALUES("+currentUser.id+", "+postId+")";
        database.excuteCommand(con, query);
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void unLikePost(int postId){
        Connection con = database.connectToDatabase();
        String query = "DELETE FROM [Like] WHERE UserId="+currentUser.id+" AND PostId="+postId;
        database.excuteCommand(con, query);
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void receivePost(int postId){
        Connection con = database.connectToDatabase();
        String query = "INSERT INTO [Receive](UserId,PostId)VALUES("+currentUser.id+", "+postId+")";
        database.excuteCommand(con, query);
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void unReceivePost(int postId){
        Connection con = database.connectToDatabase();
        String query = "DELETE FROM [Receive] WHERE UserId="+currentUser.id+" AND PostId="+postId;
        database.excuteCommand(con, query);
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






}
