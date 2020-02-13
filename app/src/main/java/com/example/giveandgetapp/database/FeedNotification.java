package com.example.giveandgetapp.database;

import net.sourceforge.jtds.jdbc.DateTime;

import java.util.Date;

public class FeedNotification {
    public int postId;
    public int status;
    public Date createDate;
    public String title;
    public String contents;
    public int type;

    public FeedNotification(int postId, int status, Date creaDateTime, String title, String contents, int type){
        this.postId = postId;
        this.status = status;
        this.createDate = creaDateTime;
        this.title = title;
        this.contents = contents;
        this.type = type;
    }

}
