package com.example.giveandgetapp.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.ActorInforActivity;
import com.example.giveandgetapp.ApproveActivity;
import com.example.giveandgetapp.EditPostActivity;
import com.example.giveandgetapp.EditProfileActivity;
import com.example.giveandgetapp.GiveActivity;
import com.example.giveandgetapp.LoginActivity;
import com.example.giveandgetapp.MainActivity;
import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.RatingActivity;
import com.example.giveandgetapp.database.ActorPostAdapter;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.PostProfile;
import com.example.giveandgetapp.database.ReceivePostAdapter;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    //Component
    private Database _database;
    private SessionManager _sessionManager;
    private TabHost _tabHost;
    private ImageView _iconBack;
    private ImageView _avatarUser;
    private Button _btnEditProfile;
    private TextView _lblten;
    private TextView _lbllop;
    private TextView _lblmssv;
    private TextView _lblsdt;
    private TextView _sobaidang;
    private TextView _sodanhgia;
    private TextView _sobaocao;
    private Button _btnLogout;
    private TabWidget _tabWidget;
    private LinearLayout _linearLayoutTabs;
    private ProfileViewModel profileViewModel;
    private GridView _gridActorPost;
    private GridView _gridReceivePost;
    private GridView _gridActorPostGived;
    private ArrayList<Bitmap> _listImagePostActor;
    private ArrayList<Bitmap> _listImagePostReceive;
    private ArrayList<Bitmap> _listImagePostActorGive;
    private Boolean _isRedirectToActivity  = false;
    private int _maxIdPostProfileActor = 0;
    private ActorPostAdapter _actorPostAdapter;
    private ReceivePostAdapter _receivePostAdapter;
    private ActorPostAdapter _actorPostGivedAdapter;


    public final int REQUEST_CODE_EDIT_PROFILE = 100;

    public static double roundHalf(double number) {
        double diff = number - (int)number;
        if (diff < 0.25) {
            return (int)number;
        }
        else if (diff < 0.75) {
            return (int)number + 0.5;
        } else {
            return (int)number + 1;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        profileViewModel =
                ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        _database = new Database(root.getContext());
        _sessionManager = new SessionManager(root.getContext());

        if(profileViewModel.getListPostProfileActor().getValue() == null){
            //Init
            profileViewModel.setListPostProfileActor(getInitListPostProfileActor());
            profileViewModel.setmMaxPostProfileActor(_maxIdPostProfileActor);
        }else{
            //Update
            _maxIdPostProfileActor = profileViewModel.getMaxPostProfileActor().getValue();
            profileViewModel.setmMaxPostProfileActor(getMaxPostIdActor());
        }

        profileViewModel.setListPostProfileReceive(getInitListPostProfileReceive());

        User user = _sessionManager.getUserDetail();

        _lblten = root.findViewById(R.id.lbltenuser);
        _lbllop = root.findViewById(R.id.lbllopuser);
        _lblmssv = root.findViewById(R.id.lblmssvuser);
        _lblsdt = root.findViewById(R.id.lblsdtuser);
        _avatarUser = root.findViewById(R.id.avataruser);
        _sobaidang = root.findViewById(R.id.sobaidang);
        _sodanhgia = root.findViewById(R.id.sodanhgia);
        _sobaocao = root.findViewById(R.id.sobaocao);
//        _btnLogout = root.findViewById(R.id.btnlogout);
        _gridActorPost = root.findViewById(R.id.gridActorPost);
        _gridReceivePost = root.findViewById(R.id.gridReceivePost);
        _gridActorPostGived = root.findViewById(R.id.gridActorPostGived);



        //Load bài đăng + báo cáo
        Connection con = _database.connectToDatabase();
        String query = "SELECT ReportCount, RatingCount FROM [User] WHERE Id = "+ user.id;
        String querypost = "SELECT COUNT (*) FROM Post WHERE Actor =  "+user.id;
        ResultSet resultSet = _database.excuteCommand(con, query);
        ResultSet resultquerypost = _database.excuteCommand(con, querypost);
        try {
            if(resultSet.next()){
                int rpc = resultSet.getInt("ReportCount");
                float rtc = resultSet.getFloat("RatingCount");
                double parsertcToDouble = rtc;
                double rating = roundHalf(parsertcToDouble);
                _sodanhgia.setText(rating+"");
                _sobaocao.setText(rpc+"");
            }

            if(resultquerypost.next()){
                int pc = resultquerypost.getInt(1);
                _sobaidang.setText(pc+"");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        //Load thông tin user
        _lblten.setText("Tên: " + user.name);
        if(user.clazz == null || user.clazz.length() == 0)
        {
            _lbllop.setText("Lớp:");
        }else
        {
            _lbllop.setText("Lớp: " + user.clazz);
        }

        if(user.studentId == null || user.studentId.length() == 0)
        {
            _lblmssv.setText("MSSV:");
        }else
        {
            _lblmssv.setText("MSSV: " + user.studentId);
        }

        _lblsdt.setText("SĐT: " + user.phone);
        _avatarUser.setImageBitmap(BitmapFactory.decodeFile(user.avatar));

        _tabHost = root.findViewById(R.id.tabhost);
        _btnEditProfile = root.findViewById(R.id.btneditprofile);
        _btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                _isRedirectToActivity = true;
                startActivityForResult(intent,REQUEST_CODE_EDIT_PROFILE);
            }
        });

        //Button logout
//        _btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                _sessionManager.logout();
//            }
//        });

        _tabHost.setup();

        //tab1
        TabHost.TabSpec spec = _tabHost.newTabSpec("Tab một");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Những bài viết đã đăng");
        _tabHost.addTab(spec);

        //tab2
        spec = _tabHost.newTabSpec("Tab hai");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Những bài viết đã nhận");
        _tabHost.addTab(spec);

        //tab3
        spec = _tabHost.newTabSpec("Tab ba");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Những bài viết đã cho");
        _tabHost.addTab(spec);

        TabWidget tw = (TabWidget)_tabHost.findViewById(android.R.id.tabs);
        View tabView = tw.getChildTabViewAt(0);
        TextView tv = (TextView)tabView.findViewById(android.R.id.title);
        tv.setTextSize(10);


        View tabView1 = tw.getChildTabViewAt(1);
        TextView tv1 = (TextView)tabView1.findViewById(android.R.id.title);
        tv1.setTextSize(10);

        View tabView2 = tw.getChildTabViewAt(2);
        TextView tv2 = (TextView)tabView2.findViewById(android.R.id.title);
        tv2.setTextSize(10);

        //Set Adapter for Gridview receive


        ArrayList<PostProfile> listProfileReceive= profileViewModel.getListPostProfileReceive().getValue();
        _listImagePostReceive = new ArrayList<Bitmap>();
        _receivePostAdapter = new ReceivePostAdapter(root.getContext(),new ArrayList<PostProfile>(),new ArrayList<Bitmap>());
        _gridReceivePost.setAdapter(_receivePostAdapter);

        Runnable runnablePostReceive = new Runnable() {
            @Override
            public void run() {
                Connection connection = _database.connectToDatabase();

                for (PostProfile item:listProfileReceive) {
                    Bitmap img = _database.getImageInDatabaseInSquire(connection, item.imageId);
                    _listImagePostReceive.add(img);
                }

                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _receivePostAdapter.setListPostReceive(listProfileReceive, _listImagePostReceive);
                        _receivePostAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        Thread threadPostReceive = new Thread(runnablePostReceive);
        threadPostReceive.start();



        //Set Adapter for Gridview actor
        profileViewModel.getListPostProfileActor().observe(this, new Observer<ArrayList<PostProfile>>() {
            @Override
            public void onChanged(ArrayList<PostProfile> postProfiles) {
                Connection connection = _database.connectToDatabase();
                _listImagePostActor = new ArrayList<Bitmap>();

                _listImagePostActorGive = new ArrayList<Bitmap>();
                ArrayList<PostProfile> postProfileGived = new ArrayList<PostProfile>();

                for (PostProfile item:postProfiles) {
                    Bitmap img = _database.getImageInDatabaseInSquire(connection, item.imageId);
                    _listImagePostActor.add(img);
                    if(item.status > 2){
                        postProfileGived.add(item);
                        _listImagePostActorGive.add(img);
                    }
                }

                _actorPostGivedAdapter.setListPostActor(postProfileGived,_listImagePostActorGive);
                _actorPostGivedAdapter.notifyDataSetChanged();

                _actorPostAdapter.setListPostActor(postProfiles,_listImagePostActor);
                _actorPostAdapter.notifyDataSetChanged();
            }
        });

        //Post Actor adp
        ArrayList<PostProfile> listProfileActor= profileViewModel.getListPostProfileActor().getValue();
        _listImagePostActor = new ArrayList<Bitmap>();
        _actorPostAdapter = new ActorPostAdapter(root.getContext(),new ArrayList<PostProfile>(),new ArrayList<Bitmap>());
        _gridActorPost.setAdapter(_actorPostAdapter);

        //Post Actor Gived adp
        ArrayList<PostProfile> listProfileActorGive = new ArrayList<PostProfile>();
        _listImagePostActorGive = new ArrayList<Bitmap>();
        _actorPostGivedAdapter = new ActorPostAdapter(root.getContext(),new ArrayList<PostProfile>(),new ArrayList<Bitmap>());
        _gridActorPostGived.setAdapter(_actorPostGivedAdapter);


        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }




        //Set EventClick for grid post actor
        AlertDialog.Builder dialogPostActor = new AlertDialog.Builder(this.getContext());
        View viewPostActor = getLayoutInflater().inflate(R.layout.dialog_post_actor_profile,null);

        Button btnReviewActor = viewPostActor.findViewById(R.id.btnReview);
        Button btnEditActor = viewPostActor.findViewById(R.id.btnEdit);
        Button btnGiveActor = viewPostActor.findViewById(R.id.btnGive);
        Button btnDeleteActor = viewPostActor.findViewById(R.id.btnDelete);
        Button btnCancelActor = viewPostActor.findViewById(R.id.btnCancel);

        dialogPostActor.setView(viewPostActor);
        Dialog dlogPostActor = dialogPostActor.create();
        dlogPostActor.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Reset view
                btnReviewActor.setVisibility(View.GONE);
                btnEditActor.setVisibility(View.GONE);
                btnGiveActor.setVisibility(View.GONE);
                btnDeleteActor.setVisibility(View.GONE);
            }
        });

        btnCancelActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlogPostActor.cancel();
            }
        });

        _gridActorPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostProfile item = listProfileActor.get(position);

                //Are Post is not expire time
                if(item.status == 1){
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnDeleteActor.setVisibility(View.VISIBLE);
                    btnEditActor.setVisibility(View.VISIBLE);
                    btnGiveActor.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));
                    btnDeleteActor.setOnClickListener(getButtonDeleteClickListener(item.postId, position));
                    btnEditActor.setOnClickListener(getButtonEditClickListener(item.postId));
                    btnGiveActor.setOnClickListener(getButtonGiveClickListener(item.postId));
                }
                //Are Post is expire
                else if(item.status == 2){
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));

                    btnGiveActor.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnGiveActor.setOnClickListener(getButtonGiveClickListener(item.postId));
                } else {
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));
                }

                dlogPostActor.show();

            }
        });

        //Actor post gived dialog
        _gridActorPostGived.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostProfile item = _actorPostGivedAdapter._listPostActor.get(position);

                //Are Post is not expire time
                if(item.status == 1){
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnDeleteActor.setVisibility(View.VISIBLE);
                    btnEditActor.setVisibility(View.VISIBLE);
                    btnGiveActor.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));
                    btnDeleteActor.setOnClickListener(getButtonDeleteClickListener(item.postId, position));
                    btnGiveActor.setOnClickListener(getButtonGiveClickListener(item.postId));
                }
                //Are Post is expire
                else if(item.status == 2){
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));
                    btnGiveActor.setVisibility(View.VISIBLE);
                    //Add action listener
                    btnGiveActor.setOnClickListener(getButtonGiveClickListener(item.postId));
                } else {
                    btnReviewActor.setVisibility(View.VISIBLE);
                    btnReviewActor.setOnClickListener(getButtonReviewClickListener(item.postId));
                }
                dlogPostActor.show();

            }
        });


        //Set EventClick for grid post receive
        AlertDialog.Builder dialogPostReceive = new AlertDialog.Builder(this.getContext());
        View viewPostReceive = getLayoutInflater().inflate(R.layout.dialog_post_receive_profile,null);

        Button btnReviewReceive = viewPostReceive.findViewById(R.id.btnReview);
        Button btnRatingReceive = viewPostReceive.findViewById(R.id.btnRating);
        Button btnCancelReceive = viewPostReceive.findViewById(R.id.btnCancel);
        Button btnActorProfileReceive = viewPostReceive.findViewById(R.id.btnActorProfile);

        dialogPostReceive.setView(viewPostReceive);
        Dialog dlogPostReceive = dialogPostReceive.create();
        dlogPostReceive.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Reset view
                btnReviewReceive.setVisibility(View.GONE);
                btnRatingReceive.setVisibility(View.GONE);
            }
        });

        btnCancelReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlogPostReceive.cancel();
            }
        });

        _gridReceivePost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostProfile item = _receivePostAdapter._listPostReceive.get(position);

                btnActorProfileReceive.setOnClickListener(getButtonActorInfoClickListener(item.actorId));

                //Are Post is not expire time
                if(item.status == 1){
                    btnReviewReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewReceive.setOnClickListener(getButtonReviewClickListener(item.postId));
                }

                //Are Post is expire
                else if(item.status == 4 && _sessionManager.getUserDetail().id == item.receiveId){
                    btnRatingReceive.setText("Đánh giá");

                    btnRatingReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnRatingReceive.setOnClickListener(getButtonRatingClickListener(item.postId));

                    btnReviewReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewReceive.setOnClickListener(getButtonReviewClickListener(item.postId));
                }else if(item.status == 3){
                    btnRatingReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnRatingReceive.setText("Xác nhận nhận đồ");
                    btnRatingReceive.setOnClickListener(getButtonApproveClickListener(item.postId));

                    btnReviewReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewReceive.setOnClickListener(getButtonReviewClickListener(item.postId));
                }
                else {
                    btnReviewReceive.setVisibility(View.VISIBLE);

                    //Add action listener
                    btnReviewReceive.setOnClickListener(getButtonReviewClickListener(item.postId));
                }
                dlogPostReceive.show();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.navigation_logout){
            _sessionManager.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Button Approve
    private View.OnClickListener getButtonApproveClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ApproveActivity.class);
                intent.putExtra("Post_Id", postId);
                getActivity().startActivityForResult(intent,120);

                _isRedirectToActivity = true;
            }
        };

        return listener;
    }

    //Button rating action
    private View.OnClickListener getButtonRatingClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RatingActivity.class);
                intent.putExtra("Post_Id",postId);
                getActivity().startActivityForResult(intent,12);

                _isRedirectToActivity = true;
            }
        };

        return listener;
    }

    //Button edit action
    private View.OnClickListener getButtonEditClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPostActivity.class);
                intent.putExtra("Post_Id",postId);
                getActivity().startActivityForResult(intent,14);

                _isRedirectToActivity = true;
            }
        };

        return listener;
    }

    //Button actor info action
    private View.OnClickListener getButtonActorInfoClickListener(int actorId){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to activity actor info
                Intent intent = new Intent(getActivity(), ActorInforActivity.class);
                intent.putExtra("Actor_Id",actorId);
                getActivity().startActivityForResult(intent,20);

                _isRedirectToActivity = true;
            }
        };
    }

    //Button give action
    private View.OnClickListener getButtonGiveClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GiveActivity.class);
                intent.putExtra("Post_Id",postId);
                getActivity().startActivityForResult(intent,11);

                _isRedirectToActivity = true;
            }
        };

        return listener;
    }

    //Button review action
    private View.OnClickListener getButtonReviewClickListener(int postId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("Post_Id",postId);
                getActivity().startActivityForResult(intent,10);

                _isRedirectToActivity = true;
            }
        };

        return listener;
    }

    //Button delete action
    private View.OnClickListener getButtonDeleteClickListener(int postId, int position) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Connection con  = _database.connectToDatabase();
                    String query = "DELETE FROM [Post] " +
                            "      WHERE Id =" + postId;
                    _database.excuteCommand(con,query);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                _actorPostAdapter._listPostActor.remove(position);
                _actorPostAdapter._listImage.remove(position);
                profileViewModel.setListPostProfileActor(_actorPostAdapter._listPostActor);

                _actorPostAdapter.notifyDataSetChanged();
            }
        };

        return listener;
    }


    private int getMaxPostIdActor() {
        try{
            Connection con = _database.connectToDatabase();
            User currentUser = _sessionManager.getUserDetail();

            String query = "SELECT Id,Image,Title,Status" +
                    "  FROM [Post] Where Actor = "+currentUser.id +" AND Id>"+_maxIdPostProfileActor;
            ResultSet result = _database.excuteCommand(con, query);
            int maxIdPostProfileActor = _maxIdPostProfileActor;

            while(result.next()){
                int postId = result.getInt("Id");
                String title = result.getString("Title");
                int status = result.getInt("Status");
                int imageId = result.getInt("Image");

                PostProfile postProfile = new PostProfile(postId, currentUser.id, title, status, imageId,0);
                profileViewModel.getListPostProfileActor().getValue().add(postProfile);
                if(postId > maxIdPostProfileActor){
                    maxIdPostProfileActor = postId;
                }
            }

            _maxIdPostProfileActor = maxIdPostProfileActor;

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _maxIdPostProfileActor;

    }

    private ArrayList<PostProfile> getInitListPostProfileReceive() {
        ArrayList<PostProfile> listPostProfile = new ArrayList<PostProfile>();

        try{
            Connection con = _database.connectToDatabase();
            User currentUser = _sessionManager.getUserDetail();

            String query = "SELECT p.Actor ,p.Id, p.Image, p.Title, p.Status, p.Receiver" +
                    " FROM [Post] p " +
                    " WHERE p.Receiver = "+currentUser.id+
                    " ORDER BY Id DESC";
            ResultSet result = _database.excuteCommand(con, query);

            while(result.next()){
                int postId = result.getInt("Id");
                String title = result.getString("Title");
                int status = result.getInt("Status");
                int imageId = result.getInt("Image");
                int receiverId = result.getInt("Receiver");
                int actorId = result.getInt("Actor");

                PostProfile postProfile = new PostProfile(postId, currentUser.id, title, status, imageId,receiverId);
                postProfile.actorId = actorId;
                listPostProfile.add(postProfile);
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listPostProfile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_CODE_EDIT_PROFILE:
                if(resultCode == Activity.RESULT_OK){
                    User user = _sessionManager.getUserDetail();
                    _lbllop.setText("Lớp: " + user.clazz);
                    _lblmssv.setText("MSSV: " + user.studentId);
                    _lblsdt.setText("SĐT: " + user.phone);
                    _avatarUser.setImageBitmap(BitmapFactory.decodeFile(user.avatar));
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<PostProfile> getInitListPostProfileActor() {
        ArrayList<PostProfile> listPostProfile = new ArrayList<PostProfile>();

        try{
            Connection con = _database.connectToDatabase();
            User currentUser = _sessionManager.getUserDetail();

            String query = "SELECT Id,Image,Title,Status" +
                    "  FROM [Post] Where Actor = "+currentUser.id+
                    "  ORDER BY Id DESC";
            ResultSet result = _database.excuteCommand(con, query);
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

            _maxIdPostProfileActor = maxIdPostProfileActor;

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listPostProfile;
    }

    @Override
    public void onStop() {
        if(!_isRedirectToActivity){
            for (Bitmap img:_listImagePostActor) {
                if(img != null){
                    img.recycle();
                }
            }

            for (Bitmap img:_listImagePostReceive) {
                if(img != null){
                    img.recycle();
                }
            }

            _isRedirectToActivity = false;
        }
        super.onStop();
    }
}