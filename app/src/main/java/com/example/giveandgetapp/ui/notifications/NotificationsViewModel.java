package com.example.giveandgetapp.ui.notifications;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedNotification;
import com.example.giveandgetapp.database.NotificationAdapter;

import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<FeedNotification>> mlistNotification;

    public NotificationsViewModel() {
        mlistNotification = new MutableLiveData<ArrayList<FeedNotification>>();
    }


    public MutableLiveData<ArrayList<FeedNotification>> getListNotification() {
        return mlistNotification;
    }

    public void setListNotification(ArrayList listNotification) {
        this.mlistNotification.setValue(listNotification);
    }
}