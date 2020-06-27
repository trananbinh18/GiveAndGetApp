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

import com.example.giveandgetapp.ActorInforActivity;
import com.example.giveandgetapp.ListUserActivity;
import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.ui.profile.ProfileFragment;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<Integer,  ImageStoreEachPost> imageMap;


    public FeedListAdapter(Activity activity, List<FeedItem> feedItems){
        this.activity = activity;
        this.feedItems = feedItems;
        this.sessionManager = new SessionManager(activity.getApplicationContext());
        this.currentUser = this.sessionManager.getUserDetail();
        this.database = new Database(activity.getApplicationContext());
        this.listImagesInAllItems = new ArrayList<Bitmap>();

        //Get all image in database
        this.imageMap = new HashMap<Integer, ImageStoreEachPost>();


        Connection con = database.connectToDatabase();
        for (FeedItem item: feedItems) {

            ArrayList<Bitmap> imagesPost = new ArrayList<Bitmap>();
            Bitmap imageActor = null;

            if(item.actorImageId != 0){
                Bitmap img = database.getImageInDatabase(con, item.actorImageId);
                this.listImagesInAllItems.add(img);
                imageActor  = img;
            }

            if(item.imageId != 0) {
                Bitmap img = database.getImageInDatabaseInRec(con, item.imageId);
                this.listImagesInAllItems.add(img);
                imagesPost.add(img);
            }

            if(item.image2Id != 0) {
                Bitmap img = database.getImageInDatabaseInRec(con, item.image2Id);
                this.listImagesInAllItems.add(img);
                imagesPost.add(img);
            }

            if(item.image3Id != 0) {
                Bitmap img = database.getImageInDatabaseInRec(con, item.image3Id);
                this.listImagesInAllItems.add(img);
                imagesPost.add(img);
            }

            ImageStoreEachPost imgsPost = new ImageStoreEachPost(imagesPost, imageActor);

            imageMap.put(item.postId, imgsPost);

        }

        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void setFeedItems(List<FeedItem> feedItems) {
        this.feedItems = feedItems;

        Connection con = database.connectToDatabase();
        for (FeedItem item: feedItems) {
            if(!imageMap.containsKey(item.postId)){
                ArrayList<Bitmap> imagesPost = new ArrayList<Bitmap>();
                Bitmap imageActor = null;

                if(item.actorImageId != 0){
                    Bitmap img = database.getImageInDatabase(con, item.actorImageId);
                    this.listImagesInAllItems.add(img);
                    imageActor  = img;
                }

                if(item.imageId != 0) {
                    Bitmap img = database.getImageInDatabaseInRec(con, item.imageId);
                    this.listImagesInAllItems.add(img);
                    imagesPost.add(img);
                }

                if(item.image2Id != 0) {
                    Bitmap img = database.getImageInDatabaseInRec(con, item.image2Id);
                    this.listImagesInAllItems.add(img);
                    imagesPost.add(img);
                }

                if(item.image3Id != 0) {
                    Bitmap img = database.getImageInDatabaseInRec(con, item.image3Id);
                    this.listImagesInAllItems.add(img);
                    imagesPost.add(img);
                }

                ImageStoreEachPost imgsPost = new ImageStoreEachPost(imagesPost, imageActor);

                imageMap.put(item.postId, imgsPost);
            }
        }

        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        ImageButton actorImage  = convertView.findViewById(R.id.actorImage);
        TextView actorName = convertView.findViewById(R.id.actorName);
        TextView createPostTime = convertView.findViewById(R.id.createPostTime);
        TextView title = convertView.findViewById(R.id.txtTitle);
        TextView content = convertView.findViewById(R.id.txtContent);
        ViewPager pageImage = convertView.findViewById(R.id.pageImage);
        CircleIndicator indicator = convertView.findViewById(R.id.indicator);
        ImageButton imgLike = convertView.findViewById(R.id.imgLike);
        ImageButton imgReceive = convertView.findViewById(R.id.imgReceive);
        ImageView imgMore  = convertView.findViewById(R.id.iconMoreDashboard);
        TextView txtLikeCount = convertView.findViewById(R.id.txtLikeCount);
        TextView ratingCount = convertView.findViewById(R.id.ratingcount);
        imgReceive.setVisibility(View.VISIBLE);


        final FeedItem item = feedItems.get(position);

        ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(convertView.getContext(),imageMap.get(item.postId).imagesPost,imageMap.get(item.postId).imageActor);
        pageImage.setAdapter(imageSlideAdapter);
        indicator.setViewPager(pageImage);

        actorImage.setImageBitmap(imageMap.get(item.postId).imageActor);
        actorName.setText(item.actorName);
        title.setText(item.title);
        content.setText(item.contents);
        ratingCount.setText("Đã được đánh giá: "+ ProfileFragment.roundHalf(item.ratingCount));

        //Set text for like and receiver
        String strLikeCount = (item.likeCount>0)?item.likeCount+" lượt thích":"";
        String strReceiverCount = (item.receiverCount>0)?item.receiverCount+" lượt đăng ký":"";
        if(strLikeCount =="" || strReceiverCount ==""){
            txtLikeCount.setText(strLikeCount+strReceiverCount);
        }else{
            txtLikeCount.setText(strLikeCount+" | "+strReceiverCount);
        }


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

        if(currentUser.id == item.actorId){
            imgReceive.setVisibility(View.INVISIBLE);
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                item.isLiked = !item.isLiked;
                ImageButton imgLike = (ImageButton) v;
                if(item.isLiked){
                    imgLike.setImageResource(R.drawable.ic_heart_fill_foreground);
                    likePost(item, txtLikeCount);
                }else{
                    imgLike.setImageResource(R.drawable.ic_heart_foreground);
                    unLikePost(item, txtLikeCount);
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
                    receivePost(item, txtLikeCount);
                }else{
                    imgReceive.setImageResource(R.drawable.ic_hand_foreground);
                    unReceivePost(item, txtLikeCount);
                }
            }
        });

        actorImage.setOnClickListener(getListenerForActorInfoActivity(item.actorId));
        title.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        content.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        imgMore.setOnClickListener(getListenerForPostDetailActivity(item.postId));
        txtLikeCount.setOnClickListener(getListenerListUserLikedRegistered(item.postId));


        return convertView;
    }

    private void likePost(FeedItem item, TextView txtLikeCount){
        item.likeCount++;
        String strLikeCount = item.likeCount+" lượt thích";
        String strReceiverCount = (item.receiverCount >0)?item.receiverCount+" lượt đăng ký":"";
        if(strReceiverCount != ""){
            txtLikeCount.setText(strLikeCount + " | "+strReceiverCount);
        }else{
            txtLikeCount.setText(strLikeCount);
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Connection con = database.connectToDatabase();
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String create_date = formater.format(date);
                String query = "INSERT INTO [Like](UserId,PostId,DateTimeLiked)VALUES("+currentUser.id+", "+item.postId+", "+"CONVERT(datetime,'" +create_date+"',120)"+" )";
                String queryNotification = "INSERT INTO [Notification]" +
                        "           (UserId,PostId,Status,CreateDate,Title,Contents,Type)" +
                        "     VALUES" +
                        "           ("+item.actorId +
                        "           ,"+item.postId +
                        "           ,1" +
                        "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                        "           ,N'"+currentUser.name+" Đã thích bài'" +
                        "           ,N'Đã có thêm một người thích bài "+item.title+"'" +
                        "           ,1)";

                String queryUpdateLikeCount = "UPDATE [Post]" +
                        "   SET LikeCount = LikeCount+1" +
                        "   WHERE Id = "+ item.postId;


                database.excuteCommand(con, query);
                if(currentUser.id != item.actorId){
                    database.excuteCommand(con, queryNotification);
                }
                database.excuteCommand(con,queryUpdateLikeCount);

                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void unLikePost(FeedItem item, TextView txtLikeCount){
        item.likeCount = (item.likeCount>0)?item.likeCount-1:0;
        String strLikeCount = (item.likeCount>0)?item.likeCount+" lượt thích":"";
        String strReceiverCount = (item.receiverCount>0)?item.receiverCount+" lượt đăng ký":"";
        if(strLikeCount =="" || strReceiverCount ==""){
            txtLikeCount.setText(strLikeCount+strReceiverCount);
        }else{
            txtLikeCount.setText(strLikeCount+" | "+strReceiverCount);
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Connection con = database.connectToDatabase();
                String query = "DELETE FROM [Like] WHERE UserId="+currentUser.id+" AND PostId="+item.postId;
                String queryUpdateLikeCount = "UPDATE [Post]" +
                        "   SET LikeCount = LikeCount-1" +
                        "   WHERE LikeCount > 0 AND Id = "+item.postId;
                database.excuteCommand(con, query);
                database.excuteCommand(con,queryUpdateLikeCount);

                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void receivePost(FeedItem item, TextView txtLikeCount){
        item.receiverCount++;
        String strReceiverCount = item.receiverCount+" lượt đăng ký";
        String strLikeCount = (item.likeCount>0)?item.likeCount+" lượt thích":"";
        if(strLikeCount != ""){
            txtLikeCount.setText(strLikeCount+" | "+strReceiverCount);
        }else{
            txtLikeCount.setText(strReceiverCount);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Connection con = database.connectToDatabase();
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String create_date = formater.format(date);
                String query = "INSERT INTO [Receive](UserId,PostId,DateTimeRegistered)VALUES("+currentUser.id+", "+item.postId+", "+"CONVERT(datetime,'" +create_date+"',120)"+" )";;
                String queryNotification = "INSERT INTO [Notification]" +
                        "           (UserId,PostId,Status,CreateDate,Title,Contents,Type)" +
                        "     VALUES" +
                        "           ("+item.actorId +
                        "           ,"+item.postId +
                        "           ,1" +
                        "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                        "           ,N'"+currentUser.name+" Đã đăng ký nhận'" +
                        "           ,N'Đã có thêm một người đăng ký nhận bài "+item.title+"'" +
                        "           ,1)";

                String queryUpdateReceiverCount = "UPDATE [Post]" +
                        "   SET ReceiverCount = ReceiverCount+1" +
                        "   WHERE Id = "+ item.postId;

                database.excuteCommand(con, query);
                database.excuteCommand(con, queryNotification);
                database.excuteCommand(con, queryUpdateReceiverCount);

                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void unReceivePost(FeedItem item, TextView txtLikeCount){
        item.receiverCount = (item.receiverCount>0)?item.receiverCount-1:0;
        String strLikeCount = (item.likeCount>0)?item.likeCount+" lượt thích":"";
        String strReceiverCount = (item.receiverCount>0)?item.receiverCount+" lượt đăng ký":"";
        if(strLikeCount =="" || strReceiverCount ==""){
            txtLikeCount.setText(strLikeCount+strReceiverCount);
        }else{
            txtLikeCount.setText(strLikeCount+" | "+strReceiverCount);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Connection con = database.connectToDatabase();
                String query = "DELETE FROM [Receive] WHERE UserId="+currentUser.id+" AND PostId="+item.postId;
                String queryUpdateReceiverCount = "UPDATE [Post]" +
                        "   SET ReceiverCount = ReceiverCount-1" +
                        "   WHERE ReceiverCount > 0 AND Id = "+item.postId;
                database.excuteCommand(con, query);
                database.excuteCommand(con,queryUpdateReceiverCount);
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
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

    //LikeCount Click
    private  View.OnClickListener getListenerListUserLikedRegistered (final int postId){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), ListUserActivity.class);
                intent.putExtra("Post_Id",postId);
                activity.startActivityForResult(intent,10);
            }
        };
        return listener;
    }

    private View.OnClickListener getListenerForActorInfoActivity(final int actorId){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ActorInforActivity.class);
                intent.putExtra("Actor_Id",actorId);
                activity.startActivity(intent);
            }
        };
        return listener;
    }

    public class ImageStoreEachPost{
        public ArrayList<Bitmap> imagesPost;
        public Bitmap imageActor;

        public ImageStoreEachPost(ArrayList<Bitmap> imagesPost, Bitmap imageActor) {
            this.imagesPost = imagesPost;
            this.imageActor = imageActor;
        }
    }


}

