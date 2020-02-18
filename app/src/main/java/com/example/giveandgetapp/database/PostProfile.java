package com.example.giveandgetapp.database;

public class PostProfile {
    public int postId;
    public int actorId;
    public String title;
    public int status;
    public int imageId;

    public PostProfile(int postId, int actorId, String title, int status, int imageId){
        this.postId = postId;
        this.actorId = actorId;
        this.title = title;
        this.status = status;
        this.imageId = imageId;
    }

}
