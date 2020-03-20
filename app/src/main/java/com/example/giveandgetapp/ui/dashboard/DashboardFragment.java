package com.example.giveandgetapp.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.FeedItem;
import com.example.giveandgetapp.database.FeedListAdapter;
import com.example.giveandgetapp.database.FeedNotification;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {

    private DashboardViewModel _dashboardViewModel;
    private Database _database;
    private SessionManager _sessionManager;
    private int _countCurrent = 1;
    private FeedListAdapter _adapter;
    private final int _numberPostLoad = 5;
    private ProgressBar _progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _dashboardViewModel =
                ViewModelProviders.of(this.getActivity()).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        _progressBar = root.findViewById(R.id.progressBar);
        _sessionManager = new SessionManager(root.getContext());

        if(_dashboardViewModel.getListFeedItem().getValue() == null){
            initListPost();
        }

        _dashboardViewModel.getListFeedItem().observe(this, new Observer<ArrayList<FeedItem>>() {
            @Override
            public void onChanged(ArrayList<FeedItem> feedItems) {
                try{
                    _adapter.setFeedItems(feedItems);
                    _adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        ListView listViewMain = root.findViewById(R.id.listViewMain);
        _adapter = new FeedListAdapter(this.getActivity(), _dashboardViewModel.getListFeedItem().getValue());
        listViewMain.setAdapter(_adapter);

        listViewMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                View view1 = view.getChildAt(view.getChildCount() - 1);
                int diff = (view1.getBottom() - (view.getHeight() + view.getScrollY()));
                if (diff == 0) {
                    if(_countCurrent == 0){
                        _progressBar.setVisibility(View.VISIBLE);
                        final Timer timer = new Timer();
                        _countCurrent = -1;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                _countCurrent = loadMorePost();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }, 3000);


                    }else if(_countCurrent != -1){
                        _progressBar.setVisibility(View.VISIBLE);

                        _countCurrent = loadMorePost();
                        _progressBar.setVisibility(View.INVISIBLE);

                    }else {
                        return;
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        return root;
    }

    private int loadMorePost(){
        if(_database == null){
            _database = new Database(getContext());
        }
        Connection con = _database.connectToDatabase();
        User currentUser = _sessionManager.getUserDetail();

        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.Image, p.Image2, p.Image3, p.CreateDate" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = " + currentUser.id +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = " + currentUser.id +
                " WHERE" +
                " (p.Id > "+_dashboardViewModel.getMaxPostId().getValue().intValue()+
                " OR" +
                " p.Id < "+_dashboardViewModel.getMinPostId().getValue().intValue()+
                " ) AND p.Status = 1 " +
                " ORDER BY p.Id DESC" +
                " OFFSET 0 ROWS " +
                " FETCH NEXT 5 ROWS ONLY";

        ResultSet result = _database.excuteCommand(con, query);

        int countRow = 0;
        try {
            while (result.next()){
                int postId = result.getInt("Id");

                if(postId > _dashboardViewModel.getMaxPostId().getValue().intValue()){
                    _dashboardViewModel.getMaxPostId().setValue(postId);
                }

                if(postId < _dashboardViewModel.getMinPostId().getValue().intValue()){
                    _dashboardViewModel.getMinPostId().setValue(postId);
                }

                int actorId = result.getInt("ActorId");
                String actorName = result.getString("ActorName");
                String title = result.getString("Title");
                String content = result.getString("Contents");
                boolean isLiked = (result.getInt("IsLiked")==currentUser.id)?true:false;
                boolean isReceived = (result.getInt("IsReceived")==currentUser.id)?true:false;
                int actorImage = result.getInt("ActorImage");
                int Image = result.getInt("Image");
                int Image2 = result.getInt("Image2");
                int Image3 = result.getInt("Image3");
                Timestamp tsCreateDate = result.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }

                FeedItem item = new FeedItem(postId,actorId,actorImage,actorName,title,content,Image,Image2,Image3,isLiked,isReceived,createDate);
                _dashboardViewModel.getListFeedItem().getValue().add(item);
                countRow++;

            }
            if(countRow == 0){
                return countRow;
            }

            _adapter.notifyDataSetChanged();

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e2){
            e2.printStackTrace();
        }
        return countRow;
    }


    private void initListPost(){
        User currentUser = _sessionManager.getUserDetail();

        _database = new Database(getContext());
        Connection con = _database.connectToDatabase();

        String query = "SELECT p.Id, a.Id as ActorId, a.Name as ActorName, p.Title, p.Contents, l.UserId as IsLiked, r.UserId as IsReceived, a.Avatar as ActorImage, p.CreateDate, p.Image, p.Image2, p.Image3" +
                " FROM [Post] p" +
                " INNER JOIN [User] a" +
                " ON p.Actor = a.Id" +
                " LEFT JOIN [Like] l" +
                " ON p.Id = l.PostId  AND l.UserId = " + currentUser.id +
                " LEFT JOIN [Receive] r" +
                " ON p.Id = r.PostId  AND r.UserId = " + currentUser.id +
                " WHERE p.Status = 1 " +
                " ORDER BY p.Id DESC" +
                " OFFSET 0 ROWS " +
                " FETCH NEXT 5 ROWS ONLY";

        ResultSet result = _database.excuteCommand(con, query);
        ArrayList<FeedItem> listFeedItem = new ArrayList<FeedItem>();
        int maxPostId = 0;
        int minPostId = 0;

        try {
            while (result.next()){
                int postId = result.getInt("Id");

                //Set for max min post id
                if(result.isFirst()){
                    maxPostId = postId;
                    minPostId = postId;
                }

                if(postId > maxPostId){
                    maxPostId = postId;
                }

                if(postId < minPostId){
                    minPostId = postId;
                }

                int actorId = result.getInt("ActorId");
                String actorName = result.getString("ActorName");
                String title = result.getString("Title");
                String content = result.getString("Contents");
                boolean isLiked = (result.getInt("IsLiked")==currentUser.id)?true:false;
                boolean isReceived = (result.getInt("IsReceived")==currentUser.id)?true:false;
                int actorImage = result.getInt("ActorImage");
                int Image = result.getInt("Image");
                int Image2 = result.getInt("Image2");
                int Image3 = result.getInt("Image3");
                Timestamp tsCreateDate = result.getTimestamp("CreateDate");
                Date createDate = null;
                if(tsCreateDate != null){
                    createDate = new Date(tsCreateDate.getTime());
                }

                FeedItem item = new FeedItem(postId,actorId,actorImage,actorName,title,content,Image,Image2,Image3,isLiked,isReceived,createDate);
                listFeedItem.add(item);

            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        _dashboardViewModel.setListFeedItem(listFeedItem);
        _dashboardViewModel.setMaxPostId(maxPostId);
        _dashboardViewModel.setMinPostId(minPostId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}