package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.giveandgetapp.GiveActivity;
import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.RatingActivity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<FeedNotification> feedNotifications;
    private Database database;

    public NotificationAdapter(Activity activity, List<FeedNotification> feedNotifications){
        this.activity = activity;
        this.feedNotifications = feedNotifications;
        this.database = new Database(activity.getApplicationContext());
    }

    public void setFeedNotifications(List<FeedNotification> feedNotifications) {
        this.feedNotifications = feedNotifications;
    }

    @Override
    public int getCount() {
        return this.feedNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return this.feedNotifications.get(position);
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
            convertView = inflater.inflate(R.layout.feed_notification, null);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtContent = convertView.findViewById(R.id.txtContent);
        TextView txtTime = convertView.findViewById(R.id.txtTime);
        LinearLayout parentLayout = convertView.findViewById(R.id.parentLayout);

        final FeedNotification item = this.feedNotifications.get(position);
        txtTitle.setText(item.title);
        txtContent.setText(item.contents);

        //Set time to notification
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

        //Set background by status
        if(item.status == 1){
            parentLayout.setBackgroundColor(Color.parseColor("#EDA600"));
        }else{
            parentLayout.setBackgroundColor(Color.WHITE);
        }

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (item.type){
                    case 1:
                        setNotificationAlreadyRead(item.id);
                        Intent intent = new Intent(activity.getApplicationContext(), PostDetailActivity.class);
                        intent.putExtra("Post_Id",item.postId);
                        activity.startActivityForResult(intent,10);
                        break;
                    case 2:
                        if(item.status == 1){
                            Intent intent1 = new Intent(activity.getApplicationContext(), GiveActivity.class);
                            intent1.putExtra("Post_Id",item.postId);
                            activity.startActivityForResult(intent1,11);
                        }
                        break;
                    case 3:
                        if(item.status == 1){
                            Intent intent2 = new Intent(activity.getApplicationContext(), RatingActivity.class);
                            intent2.putExtra("Post_Id",item.postId);
                            activity.startActivityForResult(intent2,12);
                        }
                        break;

                    default:
                        setNotificationAlreadyRead(item.id);
                }
            }
        });


        txtTime.setText(timeStr);

        return convertView;
    }

    private void setNotificationAlreadyRead(int notificationId) {
        try {
            Connection con = database.connectToDatabase();
            String query = "UPDATE [Notification] " +
                    " SET Status = 2" +
                    " WHERE Id = "+ notificationId;
            database.excuteCommand(con,query);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }




}
