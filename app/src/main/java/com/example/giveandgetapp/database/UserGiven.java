package com.example.giveandgetapp.database;

import android.graphics.Bitmap;

public class UserGiven {

    public int userId;
    public String userName;
    public Bitmap userImage;
    public boolean isChoosed;

    public UserGiven (int userId, String userName, Bitmap userImage){
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.isChoosed = false;
    }
}

