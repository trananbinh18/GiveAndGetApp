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
import com.example.giveandgetapp.database.FeedNotification;
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NotificationsViewModel _notificationsViewModel;
    private DashboardViewModel _dashboardViewModel;
    private ProfileViewModel _profileViewModel;
    private TextView txtNumberNotifyCount;

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
        txtNumberNotifyCount = badge.findViewById(R.id.notifications_badge);


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
                //On Edit
                case RESULT_FIRST_USER:
                    final int postId1 = Integer.parseInt(data.getData().toString());

                    //Delete Post in dashboard view model
                    _dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
                    ArrayList<FeedItem> currentFeedItems1 = _dashboardViewModel.getListFeedItem().getValue();
                    ArrayList<FeedItem> newFeedItems1 = new ArrayList<FeedItem>();

                    for (FeedItem item:currentFeedItems1) {
                        if(item.postId == postId1){
                            newFeedItems1.add(getFeedItemById(postId1));
                        }else{
                            newFeedItems1.add(item);
                        }
                    }
                    _dashboardViewModel.setListFeedItem(newFeedItems1);


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
                    _profileViewModel.setListPostProfileActor(newPostProfiles);

                    //Set for notification
                    ArrayList<FeedNotification> currentNotifications = _notificationsViewModel.getListNotification().getValue();
                    ArrayList<FeedNotification> newNotifications = new ArrayList<FeedNotification>();

                    int countNotRead = 0;
                    for (FeedNotification item: currentNotifications) {
                        if(item.postId == postId && item.type ==2){
                            item.status = 2;
                        }

                        if(item.status == 1){
                            countNotRead++;
                        }

                        newNotifications.add(item);
                    }

                    txtNumberNotifyCount.setText(countNotRead+"");

                    _notificationsViewModel.setListNotification(newNotifications);



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

                    _profileViewModel.setListPostProfileActor(newPostProfiles);


                    //Set for notification
                    ArrayList<FeedNotification> currentNotifications = _notificationsViewModel.getListNotification().getValue();
                    ArrayList<FeedNotification> newNotifications = new ArrayList<FeedNotification>();

                    int countNotRead = 0;
                    for (FeedNotification item: currentNotifications) {
                        if(item.postId == postId && item.type ==3){
                            item.status = 2;
                        }
                        if(item.status == 1){
                            countNotRead++;
                        }

                        newNotifications.add(item);
                    }

                    txtNumberNotifyCount.setText(countNotRead+"");

                    _notificationsViewModel.setListNotification(newNotifications);


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

    private FeedItem getFeedItemById(int postId){
        FeedItem item = null;

        Database database = new Database(this);
        Connection con = database.connectToDatabase();
        SessionManager sessionManager = new SessionManager(this);

        User currentUser = sessionManager.getUserDetail();

        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.CreateDate, p.Image, p.Image2, p.Image3" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = " + currentUser.id +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = " + currentUser.id +
                " WHERE p.Id ="+postId;

        ResultSet rs = database.excuteCommand(con,query);
        try {
            if(rs.next()){
                int actorId = rs.getInt("ActorId");
                String actorName = rs.getString("ActorName");
                String title = rs.getString("Title");
                String content = rs.getString("Contents");
                boolean isLiked = (rs.getInt("IsLiked")==currentUser.id)?true:false;
                boolean isReceived = (rs.getInt("IsReceived")==currentUser.id)?true:false;
                int actorImage = rs.getInt("ActorImage");
                int Image = rs.getInt("Image");
                int Image2 = rs.getInt("Image2");
                int Image3 = rs.getInt("Image3");
                Timestamp tsCreateDate = rs.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }

                item = new FeedItem(postId,actorId,actorImage,actorName,title,content,Image,Image2,Image3,isLiked,isReceived,createDate);

                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;


    }

}

