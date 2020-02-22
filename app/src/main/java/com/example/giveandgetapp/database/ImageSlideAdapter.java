package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ImageSlideAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Bitmap> listImage;
    private LayoutInflater inflater;
    private Bitmap actorImage;

    public ImageSlideAdapter(Context context, ArrayList<Bitmap> listImage, Bitmap actorImage){
        this.context = context;
        this.listImage = listImage;
        this.actorImage = actorImage;
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        boolean isShowed = container.isShown();

        if(!isShowed) {
            if(listImage.get(position) != null){
                listImage.get(position).recycle();
            }

            if(actorImage !=null){
                actorImage.recycle();
            }
        }


        ((ViewPager) container).removeView((View) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View myImageLayout = inflater.inflate(R.layout.image_slide, container, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.image);
        myImage.setImageBitmap(listImage.get(position));
        container.addView(myImageLayout, position);
        return myImageLayout;
    }


}
