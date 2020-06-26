package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

import net.sourceforge.jtds.jdbc.DateTime;


public class ResultListUser {
    public int postId;
    public int userId;
    public String userName;
    public Bitmap userImage;
    public DateTime timeLiked;
    public DateTime timeRegistered;

    public ResultListUser(int postId, int userId, Bitmap userImage, DateTime timeLiked, DateTime timeRegistered, String userName) {
        this.postId = postId;
        this.userId = userId;
        this.userImage = userImage;
        this.timeLiked = timeLiked;
        this.timeRegistered = timeRegistered;
        this.userName = userName;
    }
}
