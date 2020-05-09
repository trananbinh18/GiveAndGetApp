package com.example.giveandgetapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedNotification;
import com.example.giveandgetapp.database.NotificationAdapter;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel _notificationsViewModel;
    private NotificationAdapter _adapter;
    private Database _database;
    private SessionManager _sessionManager;
    private ArrayList<FeedNotification> _listFeedNotification;

    private ListView _listViewNotification;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _notificationsViewModel =
                ViewModelProviders.of(getActivity()).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        _database = new Database(getContext());
        _sessionManager = new SessionManager(getContext());

        if(_notificationsViewModel.getListNotification().getValue() == null) {
            _notificationsViewModel.setListNotification(getInitNotificationList());
        }

        _listFeedNotification = _notificationsViewModel.getListNotification().getValue();
        _adapter = new NotificationAdapter(getActivity(), _listFeedNotification);

        _listViewNotification = root.findViewById(R.id.listViewNotification);
        _listViewNotification.setAdapter(_adapter);

        _notificationsViewModel.getListNotification().observe(this, new Observer<ArrayList<FeedNotification>>() {
            @Override
            public void onChanged(ArrayList<FeedNotification> feedNotifications) {
                try{
                    _adapter.setFeedNotifications(feedNotifications);
                    _adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        return root;
    }

    private ArrayList<FeedNotification> getInitNotificationList() {
        ArrayList<FeedNotification> result = new ArrayList<FeedNotification>();
        Connection con = _database.connectToDatabase();
        User currentUser = _sessionManager.getUserDetail();
        String query = "SELECT TOP(10) " +
                " n.Id" +
                ",n.PostId" +
                ",n.Status" +
                ",n.CreateDate" +
                ",n.Title" +
                ",n.Contents" +
                ",n.Type" +
                ",p.Image" +
                "  FROM [Notification] n LEFT JOIN [Post] p ON n.PostId = p.Id " +
                "  WHERE UserId = " + currentUser.id +
                "  ORDER BY Id DESC";

        ResultSet rs = _database.excuteCommand(con, query);

        try {

            while (rs.next()){
                int id = rs.getInt("Id");
                int postId = rs.getInt("PostId");
                int status = rs.getInt("Status");
                Timestamp tsCreateDate = rs.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }
                String title = rs.getString("Title");
                String content = rs.getString("Contents");
                int type = rs.getInt("Type");
                int idImage = rs.getInt("Image");

                FeedNotification item = new FeedNotification(id,postId,status,createDate,title,content,type,idImage);
                result.add(item);
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }
}