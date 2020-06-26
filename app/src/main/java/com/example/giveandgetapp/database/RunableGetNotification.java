package com.example.giveandgetapp.database;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.giveandgetapp.GiveActivity;
import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.RatingActivity;
import com.example.giveandgetapp.ui.notifications.NotificationsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

public class RunableGetNotification implements Runnable{
    NotificationsViewModel _modelNotification;
    Database _database;
    SessionManager _sessionManager;
    Context _context;
    BottomNavigationItemView _badgeView;
    Activity _activity;
    TextView _txtNumberNotifyCount;
    int _numberOfNotRead;

    public RunableGetNotification(NotificationsViewModel modelNotification, TextView txtNumberNotifyCount , Context context, Activity activity){
        this._modelNotification = modelNotification;
        this._database = new Database(context);
        this._sessionManager = new SessionManager(context);
        this._context = context;
        this._activity = activity;
        this._numberOfNotRead = Integer.parseInt(txtNumberNotifyCount.getText().toString());
        this._txtNumberNotifyCount = txtNumberNotifyCount;
    }

    @Override
    public void run() {
        User currentUser = _sessionManager.getUserDetail();
        int numberOfNotRead = 0;

        Connection con = this._database.connectToDatabase();
        String query = "SELECT TOP(10) " +
                " n.Id" +
                ",n.PostId" +
                ",n.Status" +
                ",n.CreateDate" +
                ",n.Title" +
                ",n.Contents" +
                ",n.Type" +
                ",p.Image" +
                "  FROM [Notification] n LEFT JOIN [Post] p ON n.PostId = p.Id " +
                "  WHERE UserId = " + currentUser.id +
                "  ORDER BY Id DESC";

        ResultSet rs = _database.excuteCommand(con, query);
        ArrayList<FeedNotification> result = new ArrayList<FeedNotification>();

        try {
            while (rs.next()){
                int id = rs.getInt("Id");
                int postId = rs.getInt("PostId");
                int status = rs.getInt("Status");
                int idImage = rs.getInt("Image");
                Timestamp tsCreateDate = rs.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }

                String title = rs.getString("Title");
                String content = rs.getString("Contents");
                int type = rs.getInt("Type");


                FeedNotification item = new FeedNotification(id,postId,status,createDate,title,content,type,idImage);
                result.add(item);

                if(status == 1){
                    numberOfNotRead++;
                    addNotification(_context, item);
                }
            }

            _numberOfNotRead = Integer.parseInt(_txtNumberNotifyCount.getText().toString());
            if(_numberOfNotRead != numberOfNotRead){
                final int finalNumberOfNotRead = numberOfNotRead;
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalNumberOfNotRead != 0){
                            _txtNumberNotifyCount.setVisibility(View.VISIBLE);
                            _txtNumberNotifyCount.setText(finalNumberOfNotRead+"");
                        }else{
                            _txtNumberNotifyCount.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            _modelNotification.getListNotification().postValue(result);


            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void addNotification( Context context, FeedNotification feedNotification){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] activeNotifications = manager.getActiveNotifications();
        boolean isHadNotify = false;
        for (StatusBarNotification notify: activeNotifications) {
            if(notify.getId() == feedNotification.id){
                isHadNotify= true;
                break;
            }
        }

        if(!isHadNotify){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(feedNotification.title)
                    .setContentText(feedNotification.contents)
                    .setAutoCancel(true)
                    .setChannelId("giveandget");


            Intent intent;
            intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("Post_Id", feedNotification.postId);
            intent.putExtra("Is_From_Notification",true);
            intent.putExtra("Notification_Id",feedNotification.id);

            switch (feedNotification.type) {
                case 2:
                    if (feedNotification.status == 1) {
                        intent = new Intent(context, GiveActivity.class);
                        intent.putExtra("Post_Id", feedNotification.postId);
                    }
                    break;
                case 3:
                    if (feedNotification.status == 1) {
                        intent = new Intent(context, RatingActivity.class);
                        intent.putExtra("Post_Id", feedNotification.postId);
                    }
                    break;

            }

            Intent[] intents = {intent};
            PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentIntent(pendingIntent);

            manager.notify(feedNotification.id, builder.build());
        }

    }
}
