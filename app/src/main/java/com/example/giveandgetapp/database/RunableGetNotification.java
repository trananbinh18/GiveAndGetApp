package com.example.giveandgetapp.database;

import android.content.Context;
import android.widget.Toast;

import com.example.giveandgetapp.ui.notifications.NotificationsViewModel;

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

    public RunableGetNotification(NotificationsViewModel modelNotification, Context context){
        this._modelNotification = modelNotification;
        this._database = new Database(context);
        this._sessionManager = new SessionManager(context);
        this._context = context;

    }

    @Override
    public void run() {
        User currentUser = _sessionManager.getUserDetail();

        Connection con = this._database.connectToDatabase();
        String query = "SELECT TOP(5) " +
                " PostId" +
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

                FeedNotification item = new FeedNotification(postId,status,createDate,title,content,type);
                result.add(item);
            }

            _modelNotification.getListNotification().postValue(result);

//            Toast.makeText(_context,"Load notication", Toast.LENGTH_LONG);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
