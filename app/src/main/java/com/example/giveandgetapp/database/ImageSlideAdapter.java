package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ImageSlideAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Bitmap> listImage;
    private LayoutInflater inflater;

    public ImageSlideAdapter(Context context, ArrayList<Bitmap> listImage){
        this.context = context;
        this.listImage = listImage;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listImage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View myImageLayout = inflater.inflate(R.layout.image_slide, container, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.image);
        myImage.setImageBitmap(listImage.get(position));
        container.addView(myImageLayout, 0);
        return myImageLayout;
    }
}
