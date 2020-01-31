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

import java.util.ArrayList;
import java.util.List;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems){
        this.activity = activity;
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
//        ImageButton feedImage = (ImageButton) convertView.findViewById(R.id.feedImage1);
        ViewPager pageImage = (ViewPager) convertView.findViewById(R.id.pageImage);

        FeedItem item = feedItems.get(position);
        //Image Paging
        ArrayList<Bitmap> listImage = new ArrayList<Bitmap>();
        if(item.image != null) {
            listImage.add(item.image);
        }

        if(item.image2 != null) {
            listImage.add(item.image);
        }

        if(item.image3 != null) {
            listImage.add(item.image);
        }

        ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(convertView.getContext(),listImage);
        pageImage.setAdapter(imageSlideAdapter);


        actorImage.setImageBitmap(item.actorImage);
        actorName.setText(item.actorName);
        title.setText(item.title);
        content.setText(item.contents);
//        feedImage.setImageBitmap(item.image);

        return convertView;
    }
}
