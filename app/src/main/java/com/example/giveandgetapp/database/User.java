package com.example.giveandgetapp.database;

public class User {
    public int id;
    public String studentId;
    public String avatar;
    public String email;
    public String name;
    public String clazz;
    public String phone;
    public int gender;

    public User(int id, String studentId, String avatar, String email, String name, String clazz, String phone, int gender){
        this.id = id;
        this.studentId = studentId;
        this.avatar = avatar;
        this.email = email;
        this.name = name;
        this.clazz = clazz;
        this.phone = phone;
        this.gender = gender;
    }

}
