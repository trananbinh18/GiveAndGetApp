package com.example.giveandgetapp.database;

import net.sourceforge.jtds.jdbc.DateTime;

public class Post {
    public int id;
    public int actorId;
    public int catalogId;
    public int receiverId;
    public int imageId;
    public int image2Id;
    public int image3Id;
    public String title;
    public String content;
    public DateTime createDate;
    public DateTime expireDate;
    public int giveType;
    public int status;
    public int receiverCount;
    public int expireType;  

}
