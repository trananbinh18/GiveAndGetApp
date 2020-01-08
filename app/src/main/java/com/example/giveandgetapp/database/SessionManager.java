package com.example.giveandgetapp.database;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.example.giveandgetapp.LoginActivity;
import com.example.giveandgetapp.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "LOGIN";
    public static final String LOGIN = "IS";
    public static final String ID_USER = "ID";
    public static final String STUDENT_ID = "STUDENTID";
    public static final String AVATAR = "AVATAR";
    public static final String EMAIL = "EMAIL";
    public static final String NAME = "NAME";
    public static final String CLASS = "CLASS";
    public static final String PHONE = "PHONE";
    public static final String GENDER = "GENDER";


    public SessionManager (Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


    public boolean createSession (int id, Bitmap avatar, String studentId, String email, String name, String clazz, String phone, int gender) {
        String avatarPath = "";

        try {
            avatarPath = storeImageInInternalStorage(avatar);
        } catch (Exception e){
            return false;
        }

        editor.putBoolean(LOGIN, true);
        editor.putInt(ID_USER, id);
        editor.putString(AVATAR, avatarPath);
        editor.putString(STUDENT_ID, studentId);
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putString(CLASS,clazz);
        editor.putString(PHONE, phone);
        editor.putInt(GENDER, gender);

        editor.apply();

        return true;
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }


    public String storeImageInInternalStorage(Bitmap image) {
        ContextWrapper cw = new ContextWrapper(this.context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "useravatar.jpg");

        if(file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getPath().toString();
    }

    public User getUserDetail () {
        int id = sharedPreferences.getInt(ID_USER, 0);
        String avatar = sharedPreferences.getString(AVATAR, null);
        String studentId = sharedPreferences.getString(STUDENT_ID, null);
        String email = sharedPreferences.getString(EMAIL, null);
        String name = sharedPreferences.getString(NAME, null);
        String clazz = sharedPreferences.getString(CLASS,null);
        String phone = sharedPreferences.getString(PHONE, null);
        int gender = sharedPreferences.getInt(GENDER, 0);

        User user = new User(id, studentId, avatar, email, name, clazz, phone, gender);

        return user;
    }
}
