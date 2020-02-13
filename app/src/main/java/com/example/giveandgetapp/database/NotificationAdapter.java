package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<FeedNotification> feedNotifications;

    public NotificationAdapter(Activity activity, List<FeedNotification> feedNotifications){
        this.activity = activity;
        this.feedNotifications = feedNotifications;
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

        FeedNotification item = this.feedNotifications.get(position);
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

        txtTime.setText(timeStr);

        return convertView;
    }
}
