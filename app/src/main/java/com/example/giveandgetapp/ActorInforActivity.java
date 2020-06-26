package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.Image;

import java.sql.Connection;
import java.sql.ResultSet;

public class ActorInforActivity extends AppCompatActivity {

    private int actorId;
    private boolean _isFromRating;

    private ImageView avataruser;
    private TextView sobaidang;
    private TextView sodanhgia;
    private TextView sobaocao;
    private TextView lbltenuser;
    private TextView lbllopuser;
    private TextView lblmssvuser;
    private TextView lblsdtuser;
    public Button _btnKetthucActorInfor;
    public TextView _labelInfo;


    private Database _database;

    //Round rating count
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_infor);
        this.actorId = getIntent().getIntExtra("Actor_Id",0);
        this._isFromRating = getIntent().getBooleanExtra("IsFromRating", false);


        this.avataruser = findViewById(R.id.avataruser);
        this.sobaidang = findViewById(R.id.sobaidang);
        this.sodanhgia = findViewById(R.id.sodanhgia);
        this.sobaocao = findViewById(R.id.sobaocao);
        _btnKetthucActorInfor = findViewById(R.id.btnKethucActorInfor);
        _labelInfo = findViewById(R.id.thongtinActorInfoActivity);
        if(_isFromRating)
        {
            _btnKetthucActorInfor.setVisibility(View.VISIBLE);
            _labelInfo.setVisibility(View.VISIBLE);
        }

        this.lbltenuser = findViewById(R.id.lbltenuser);
        this.lbllopuser = findViewById(R.id.lbllopuser);
        this.lblmssvuser = findViewById(R.id.lblmssvuser);
        this.lblsdtuser = findViewById(R.id.lblsdtuser);

        _database = new Database(this);
        Connection con = _database.connectToDatabase();

        String query = "SELECT [Avatar]" +
                "      ,[StudentId]" +
                "      ,[Email]" +
                "      ,[Name]" +
                "      ,[Class]" +
                "      ,[Phone]" +
                "      ,[ReportCount]" +
                "      ,[RatingCount]" +
                "      ,[NumberPostHadRated]" +
                "  FROM [User]" +
                "  WHERE Id = "+actorId;

        ResultSet resultSet = _database.excuteCommand(con, query);

        try{
            if(resultSet.next()){
                avataruser.setImageBitmap(_database.getImageInDatabaseInSquire(con, resultSet.getInt("Avatar")));
                sobaidang.setText(resultSet.getInt("NumberPostHadRated")+"");
                int rpc = resultSet.getInt("ReportCount");
                float rtc = resultSet.getFloat("RatingCount");
                double parsertcToDouble = rtc;
                double rating = roundHalf(parsertcToDouble);
                sodanhgia.setText(rating+"");
                sobaocao.setText(resultSet.getInt("ReportCount")+"");

                lbltenuser.setText("Tên: "+resultSet.getString("Name"));
                lbllopuser.setText("Lớp: "+resultSet.getString("Class"));
                lblmssvuser.setText("MSSV: "+resultSet.getString("StudentId"));
                lblsdtuser.setText("SDT: "+resultSet.getString("Phone"));
            }

            con.close();

        }catch (Exception e){

        }

        _btnKetthucActorInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                finish();
            }
        });
    }
}
