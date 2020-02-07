package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<FeedNotification> feedNotifications;

    public NotificationAdapter(Activity activity, List<FeedNotification> feedNotifications){
        this.activity = activity;
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

        return convertView;
    }
}
