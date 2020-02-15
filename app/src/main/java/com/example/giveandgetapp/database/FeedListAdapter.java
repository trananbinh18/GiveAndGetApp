package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

import static android.app.Activity.RESULT_OK;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private SessionManager sessionManager;
    private User currentUser;
    private Database database;
    public ArrayList<Bitmap> listImagesInAllItems;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems){
        this.activity = activity;
        this.feedItems = feedItems;
        this.sessionManager = new SessionManager(activity.getApplicationContext());
        this.currentUser = this.sessionManager.getUserDetail();
        this.database = new Database(activity.getApplicationContext());
        this.listImagesInAllItems = new ArrayList<Bitmap>();
    }

    public void setFeedItems(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
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
        ImageView imgMore  = convertView.findViewById(R.id.iconMoreDashboard);


        final FeedItem item = feedItems.get(position);
        //Image Paging
        ArrayList<Bitmap> listImage = new ArrayList<Bitmap>();
        Connection con = database.connectToDatabase();
        Bitmap imageActor = null;

        try{
            if(item.actorImageId != 0){
                Bitmap img = database.getImageInDatabase(con, item.actorImageId);
                this.listImagesInAllItems.add(img);
                imageActor = img;
            }

            if(item.imageId != 0) {
                Bitmap img = database.getImageInDatabase(con, item.imageId);
                this.listImagesInAllItems.add(img);
                listImage.add(img);
            }

            if(item.image2Id != 0) {
                Bitmap img = database.getImageInDatabase(con, item.image2Id);
                this.listImagesInAllItems.add(img);
                listImage.add(img);
            }

            if(item.image3Id != 0) {
                Bitmap img = database.getImageInDatabase(con, item.image3Id);
                this.listImagesInAllItems.add(img);
                listImage.add(img);
            }
            con.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }


        ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(convertView.getContext(),listImage,imageActor);
        pageImage.setAdapter(imageSlideAdapter);
        indicator.setViewPager(pageImage);

        actorImage.setImageBitmap(imageActor);
        actorName.setText(item.actorName);
        title.setText(item.title);
        content.setText(item.contents);

        //Set Time post
        String timeStr = "";
        if(item.createDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(item.createDate);
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            Date currentDate = new Date(System.currentTimeMillis() - (1 * DAY_IN_MS));
            if(currentDate.before(item.createDate)){
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(new Date());
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

        createPostTime.setText(timeStr);


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

        pageImage.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        title.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        content.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        imgMore.setOnClickListener(getListenerForPostDetailActivity(item.postId));


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

    private View.OnClickListener getListenerForPostDetailActivity(final int postId){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("Post_Id",postId);
                activity.startActivityForResult(intent,10);
            }
        };
        return listener;
    }


}
