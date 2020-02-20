package com.example.giveandgetapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.giveandgetapp.database.PostProfile;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<ArrayList<PostProfile>> mListPostProfileActor;
    private MutableLiveData<ArrayList<PostProfile>> mListPostProfileReceive;
    private MutableLiveData<Integer> mMaxPostProfileActor;
    private MutableLiveData<Integer> mMaxPostProfileReceive;

    public ProfileViewModel() {
        mListPostProfileActor = new MutableLiveData<>();
        mListPostProfileReceive = new MutableLiveData<>();
        mMaxPostProfileActor = new MutableLiveData<>();
        mMaxPostProfileReceive = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<PostProfile>> getListPostProfileActor() {
        return mListPostProfileActor;
    }

    public void setListPostProfileActor(ArrayList<PostProfile> listPostProfile){
        this.mListPostProfileActor.setValue(listPostProfile);
    }

    public MutableLiveData<ArrayList<PostProfile>> getListPostProfileReceive() {
        return mListPostProfileReceive;
    }

    public void setListPostProfileReceive(ArrayList<PostProfile> listPostProfile){
        this.mListPostProfileReceive.setValue(listPostProfile);
    }


    public MutableLiveData<Integer> getMaxPostProfileActor() {
        return mMaxPostProfileActor;
    }

    public void setmMaxPostProfileActor(int maxPostProfileActor){
        this.mMaxPostProfileActor.setValue(maxPostProfileActor);
    }
}