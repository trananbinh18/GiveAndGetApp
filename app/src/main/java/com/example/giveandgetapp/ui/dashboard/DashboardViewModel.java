package com.example.giveandgetapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.FeedNotification;

import java.util.ArrayList;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<Integer> maxPostId;
    private MutableLiveData<Integer> minPostId;

    private MutableLiveData<ArrayList<FeedItem>> listFeedItem;

    public DashboardViewModel() {
        listFeedItem = new MutableLiveData<>();
        minPostId = new MutableLiveData<>();
        maxPostId = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<FeedItem>> getListFeedItem() {
        return this.listFeedItem;
    }

    public void setListFeedItem(ArrayList listNotification) {
        this.listFeedItem.setValue(listNotification);
    }



    public MutableLiveData<Integer> getMinPostId() {
        return this.minPostId;
    }

    public void setMinPostId(Integer minPostId) {
        this.minPostId.setValue(minPostId);
    }



    public MutableLiveData<Integer> getMaxPostId() {
        return this.maxPostId;
    }

    public void setMaxPostId(Integer maxPostId) {
        this.maxPostId.setValue(maxPostId);
    }
}