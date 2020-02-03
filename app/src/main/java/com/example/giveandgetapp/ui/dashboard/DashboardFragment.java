package com.example.giveandgetapp.ui.dashboard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.FeedListAdapter;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private Database _database;
    private SessionManager _sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        _sessionManager = new SessionManager(root.getContext());
        User currentUser = _sessionManager.getUserDetail();

        _database = new Database(root.getContext());
        Connection con = _database.connectToDatabase();

        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.Image, p.Image2, p.Image3" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = " + currentUser.id +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = " + currentUser.id;

        ResultSet result = _database.excuteCommand(con, query);
        List<FeedItem> listFeedItem = new ArrayList<FeedItem>();

        try {
            while (result.next()){
             int postId = result.getInt("Id");
             int actorId = result.getInt("ActorId");
             String actorName = result.getString("ActorName");
             String title = result.getString("Title");
             String content = result.getString("Contents");
             boolean isLiked = (result.getInt("IsLiked")==currentUser.id)?true:false;
             boolean isReceived = (result.getInt("IsReceived")==currentUser.id)?true:false;
             Bitmap actorImage = _database.getImageInDatabase(con,result.getInt("ActorImage"));
             Bitmap Image = _database.getImageInDatabase(con,result.getInt("Image"));
             Bitmap Image2 = _database.getImageInDatabase(con,result.getInt("Image2"));
             Bitmap Image3 = _database.getImageInDatabase(con,result.getInt("Image3"));

             FeedItem item = new FeedItem(postId,actorId,actorImage,actorName,title,content,Image,Image2,Image3,isLiked,isReceived);
             listFeedItem.add(item);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView listViewMain = root.findViewById(R.id.listViewMain);
        FeedListAdapter adapter = new FeedListAdapter(this.getActivity(),listFeedItem);
        listViewMain.setAdapter(adapter);


        return root;
    }
}