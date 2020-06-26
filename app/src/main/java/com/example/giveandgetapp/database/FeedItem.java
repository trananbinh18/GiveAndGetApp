package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

import java.util.Date;

public class FeedItem {
    public FeedItem(int postId, int actorId, Bitmap actorImage, String actorName, String title, String contents, Bitmap image, Bitmap image2, Bitmap image3, boolean isLiked, boolean isReceiver, Date createDate){
        this.postId = postId;
        this.actorId = actorId;
        this.actorImage = actorImage;
        this.actorName = actorName;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.image2 = image2;
        this.image3 = image3;
        this.isLiked = isLiked;
        this.isReceiver = isReceiver;
        this.createDate = createDate;
    }

    public FeedItem(int postId, int actorId, Bitmap actorImage, String actorName, String title, String contents, Bitmap image, Bitmap image2, Bitmap image3, boolean isLiked, boolean isReceiver){
        this.postId = postId;
        this.actorId = actorId;
        this.actorImage = actorImage;
        this.actorName = actorName;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.image2 = image2;
        this.image3 = image3;
        this.isLiked = isLiked;
        this.isReceiver = isReceiver;
    }


    public FeedItem(int postId, int actorId, Bitmap actorImage, String actorName, String title, String contents, Bitmap image, Bitmap image2, Bitmap image3, boolean isLiked, boolean isReceiver, int likeCount, int receiverCount){
        this.postId = postId;
        this.actorId = actorId;
        this.actorImage = actorImage;
        this.actorName = actorName;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.image2 = image2;
        this.image3 = image3;
        this.isLiked = isLiked;
        this.isReceiver = isReceiver;
        this.likeCount = likeCount;
        this.receiverCount = receiverCount;
    }

    public FeedItem(int postId, int actorId, int actorImageId, String actorName, String title, String contents, int imageId, int image2Id, int image3Id, boolean isLiked, boolean isReceiver, Date createDate, int likeCount, int receiverCount, float ratingCount){
        this.postId = postId;
        this.actorId = actorId;
        this.actorImageId = actorImageId;
        this.actorName = actorName;
        this.title = title;
        this.contents = contents;
        this.imageId = imageId;
        this.image2Id = image2Id;
        this.image3Id = image3Id;
        this.isLiked = isLiked;
        this.isReceiver = isReceiver;
        this.createDate = createDate;
        this.likeCount = likeCount;
        this.receiverCount = receiverCount;
        this.ratingCount = ratingCount;
    }

    public int postId;
    public int actorId;
    public int actorImageId;
    public int imageId;
    public int image2Id;
    public int image3Id;
    public Bitmap actorImage;
    public String actorName;
    public String title;
    public String contents;
    public Bitmap image;
    public Bitmap image2;
    public Bitmap image3;
    public boolean isLiked;
    public boolean isReceiver;
    public int likeCount;
    public int receiverCount;
    public Date createDate;
    public float ratingCount;

}
