package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

public class ResultSearch {
    public int postId;
    public String postTitle;
    public Bitmap postImage;
    public int postStatus;

    public ResultSearch (int postId, String postTitle, Bitmap Image, int postStatus){
        this.postId = postId;
        this.postTitle = postTitle;
        this.postImage = Image;
        this.postStatus = postStatus;
    }
}
