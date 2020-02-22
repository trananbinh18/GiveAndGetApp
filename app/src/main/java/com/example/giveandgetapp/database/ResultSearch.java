package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

public class ResultSearch {
    public int postId;
    public String postTitle;
    public Bitmap postImage;

    public ResultSearch (int postId, String postTitle, Bitmap Image){
        this.postId = postId;
        this.postTitle = postTitle;
        this.postImage = Image;
    }
}
