package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

import net.sourceforge.jtds.jdbc.DateTime;

import java.util.Date;


public class ResultListUser {
    public int postId;
    public int userId;
    public String userName;
    public Bitmap userImage;
    public Date timeLiked;
    public Date timeRegistered;

    public ResultListUser(int postId, int userId, Bitmap userImage, Date timeLiked, Date timeRegistered, String userName) {
        this.postId = postId;
        this.userId = userId;
        this.userImage = userImage;
        this.timeLiked = timeLiked;
        this.timeRegistered = timeRegistered;
        this.userName = userName;
    }
}
