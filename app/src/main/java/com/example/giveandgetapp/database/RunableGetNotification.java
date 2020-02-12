package com.example.giveandgetapp.database;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giveandgetapp.R;
import com.example.giveandgetapp.ui.notifications.NotificationsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

public class RunableGetNotification implements Runnable{
    NotificationsViewModel _modelNotification;
    Database _database;
    SessionManager _sessionManager;
    Context _context;
    BottomNavigationItemView _badgeView;
    Activity _activity;
    TextView _txtNumberNotifyCount;
    int _numberOfNotRead;

    public RunableGetNotification(NotificationsViewModel modelNotification, TextView txtNumberNotifyCount , Context context, Activity activity){
        this._modelNotification = modelNotification;
        this._database = new Database(context);
        this._sessionManager = new SessionManager(context);
        this._context = context;
        this._activity = activity;
        this._numberOfNotRead = 0;
        this._txtNumberNotifyCount = txtNumberNotifyCount;

    }

    @Override
    public void run() {
        User currentUser = _sessionManager.getUserDetail();
        int numberOfNotRead = 0;

        Connection con = this._database.connectToDatabase();
        String query = "SELECT TOP(5) " +
                " Id" +
                ",PostId" +
                ",Status" +
                ",CreateDate" +
                ",Title" +
                ",Contents" +
                ",Type" +
                "  FROM [Notification]" +
                "  WHERE UserId = " + currentUser.id +
                "  ORDER BY CreateDate DESC";

        ResultSet rs = _database.excuteCommand(con, query);
        ArrayList<FeedNotification> result = new ArrayList<FeedNotification>();

        try {
            while (rs.next()){
                int id = rs.getInt("Id");
                int postId = rs.getInt("PostId");
                int status = rs.getInt("Status");
                if(status == 1){
                    numberOfNotRead++;
                }
                Timestamp tsCreateDate = rs.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }

                String title = rs.getString("Title");
                String content = rs.getString("Contents");
                int type = rs.getInt("Type");

                FeedNotification item = new FeedNotification(id,postId,status,createDate,title,content,type);
                result.add(item);
            }

            if(_numberOfNotRead != numberOfNotRead){
                final int finalNumberOfNotRead = numberOfNotRead;
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _txtNumberNotifyCount.setText(""+finalNumberOfNotRead);

                    }
                });
            }
            _modelNotification.getListNotification().postValue(result);


            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
