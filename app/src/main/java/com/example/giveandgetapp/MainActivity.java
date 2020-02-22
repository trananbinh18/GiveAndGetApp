package com.example.giveandgetapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.PostProfile;
import com.example.giveandgetapp.database.RunableGetNotification;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.example.giveandgetapp.ui.dashboard.DashboardViewModel;
import com.example.giveandgetapp.ui.notifications.NotificationsViewModel;
import com.example.giveandgetapp.ui.profile.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NotificationsViewModel _notificationsViewModel;
    private DashboardViewModel _dashboardViewModel;
    private ProfileViewModel _profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_add, R.id.navigation_profile, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //get notification interval in background
        _notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);

        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(this)
                .inflate(R.layout.item_notification_tab, itemView, true);
        TextView txtNumberNotifyCount = badge.findViewById(R.id.notifications_badge);


        getNotificationsInterval(_notificationsViewModel, txtNumberNotifyCount);
    }

    private void getNotificationsInterval(NotificationsViewModel modelNotification, TextView txtNumberNotifyCount){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new RunableGetNotification(modelNotification ,txtNumberNotifyCount ,getApplicationContext(),this),
                0, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10){
            switch (resultCode){
                //On delete
                case RESULT_OK:
                    final int postId = Integer.parseInt(data.getData().toString());

                    //Delete Post in dashboard view model
                    _dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
                    ArrayList<FeedItem> currentFeedItems = _dashboardViewModel.getListFeedItem().getValue();
                    ArrayList<FeedItem> newFeedItems = new ArrayList<FeedItem>();

                    for (FeedItem item:currentFeedItems) {
                        if(item.postId != postId){
                            newFeedItems.add(item);
                        }
                    }
                    _dashboardViewModel.setListFeedItem(newFeedItems);

                    //Delete Post in profile view model
                    _profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

                    if(_profileViewModel.getListPostProfileActor().getValue() == null){
                        _profileViewModel.setListPostProfileActor(getInitListPostProfileActor(_profileViewModel));
                    }
                    ArrayList<PostProfile> currentPostProfiles = _profileViewModel.getListPostProfileActor().getValue();
                    ArrayList<PostProfile> newPostProfiles = new ArrayList<PostProfile>();

                    for (PostProfile item: currentPostProfiles) {
                        if(item.postId != postId){
                            newPostProfiles.add(item);
                        }
                    }

                    _profileViewModel.setListPostProfileActor(newPostProfiles);

                    break;


            }

        }else if (requestCode == 11){
            //On give
            switch (resultCode){
                //On given
                case RESULT_OK:
                    final int postId = Integer.parseInt(data.getData().toString());
                    //edit Post in profile view model
                    _profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

                    if(_profileViewModel.getListPostProfileActor().getValue() == null){
                        _profileViewModel.setListPostProfileActor(getInitListPostProfileActor(_profileViewModel));
                    }
                    ArrayList<PostProfile> currentPostProfiles = _profileViewModel.getListPostProfileActor().getValue();
                    ArrayList<PostProfile> newPostProfiles = new ArrayList<PostProfile>();

                    for (PostProfile item: currentPostProfiles) {
                        if(item.postId == postId){
                            item.status = 3;
                        }
                        newPostProfiles.add(item);
                    }


                    break;

            }
        }else if (requestCode == 12){
            //On rating
            switch (resultCode){
                //On rating
                case RESULT_OK:
                    final int postId = Integer.parseInt(data.getData().toString());
                    //edit Post in profile view model
                    _profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

                    if(_profileViewModel.getListPostProfileActor().getValue() == null){
                        _profileViewModel.setListPostProfileActor(getInitListPostProfileActor(_profileViewModel));
                    }
                    ArrayList<PostProfile> currentPostProfiles = _profileViewModel.getListPostProfileActor().getValue();
                    ArrayList<PostProfile> newPostProfiles = new ArrayList<PostProfile>();

                    for (PostProfile item: currentPostProfiles) {
                        if(item.postId == postId){
                            item.status = 4;
                        }
                        newPostProfiles.add(item);
                    }


                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private ArrayList<PostProfile> getInitListPostProfileActor(ProfileViewModel profileViewModel) {
        ArrayList<PostProfile> listPostProfile = new ArrayList<PostProfile>();

        try{
            Database database = new Database(this);
            SessionManager sessionManager = new SessionManager(this);

            Connection con = database.connectToDatabase();
            User currentUser = sessionManager.getUserDetail();

            String query = "SELECT Id,Image,Title,Status" +
                    "  FROM [Post] Where Actor = "+currentUser.id;
            ResultSet result = database.excuteCommand(con, query);
            int maxIdPostProfileActor = 0;

            while(result.next()){
                int postId = result.getInt("Id");
                String title = result.getString("Title");
                int status = result.getInt("Status");
                int imageId = result.getInt("Image");

                PostProfile postProfile = new PostProfile(postId, currentUser.id, title, status, imageId,0);
                listPostProfile.add(postProfile);
                if(postId > maxIdPostProfileActor){
                    maxIdPostProfileActor = postId;
                }
            }

            profileViewModel.setmMaxPostProfileActor(maxIdPostProfileActor);

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return listPostProfile;
    }
}

