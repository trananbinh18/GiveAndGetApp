package com.example.giveandgetapp.ui.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.LoginActivity;
import com.example.giveandgetapp.MainActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileFragment extends Fragment {

    //Component
    private Database _database;
    private SessionManager _sessionManager;
    private TabHost _tabHost;
    private ImageView _iconBack;
    private ImageView _avatarUser;
    private Button _btnEditProfile;
    private TextView _lbllop;
    private TextView _lblmssv;
    private TextView _lblsdt;
    private TextView _sobaidang;
    private TextView _sodanhgia;
    private TextView _sobaocao;
    private Button _btnLogout;
    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        _sessionManager = new SessionManager(root.getContext());
        User user = _sessionManager.getUserDetail();

        _lbllop = root.findViewById(R.id.lbllopuser);
        _lblmssv = root.findViewById(R.id.lblmssvuser);
        _lblsdt = root.findViewById(R.id.lblsdtuser);
        _avatarUser = root.findViewById(R.id.avataruser);
        _sobaidang = root.findViewById(R.id.sobaidang);
        _sodanhgia = root.findViewById(R.id.sodanhgia);
        _sobaocao = root.findViewById(R.id.sobaocao);
        _btnLogout = root.findViewById(R.id.btnlogout);

        //Load bài đăng + báo cáo
        _database = new Database(root.getContext());
        Connection con = _database.connectToDatabase();
        String query = "SELECT ReportCount, RatingCount FROM [User] WHERE Id = "+ user.id;
        String querypost = "SELECT COUNT (*) FROM Post WHERE Actor =  "+user.id;
        ResultSet resultSet = _database.excuteCommand(con, query);
        ResultSet resultquerypost = _database.excuteCommand(con, querypost);
        try {
            if(resultSet.next()){
                int rpc = resultSet.getInt("ReportCount");
                int rtc = resultSet.getInt("RatingCount");

                _sodanhgia.setText(rpc+"");
                _sobaocao.setText(rtc+"");

            }

            if(resultquerypost.next()){
                int pc = resultquerypost.getInt(1);
                _sobaidang.setText(pc+"");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        //Load thông tin user
        _lbllop.setText("Lớp: " + user.clazz);
        _lblmssv.setText("MSSV: " + user.studentId);
        _lblsdt.setText("SĐT: " + user.phone);
        _avatarUser.setImageBitmap(BitmapFactory.decodeFile(user.avatar));

        _tabHost = root.findViewById(R.id.tabhost);
        _btnEditProfile = root.findViewById(R.id.btneditprofile);
        _btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Button logout
        _btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               _sessionManager.logout();
            }
        });
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
        return root;
    }
}