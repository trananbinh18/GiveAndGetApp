package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.Image;

import java.sql.Connection;
import java.sql.ResultSet;

public class ActorInforActivity extends AppCompatActivity {

    private int actorId;

    private ImageView avataruser;
    private TextView sobaidang;
    private TextView sodanhgia;
    private TextView sobaocao;
    private TextView lbltenuser;
    private TextView lbllopuser;
    private TextView lblmssvuser;
    private TextView lblsdtuser;

    private Database _database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_infor);

        this.actorId = getIntent().getIntExtra("Actor_Id",0);

        this.avataruser = findViewById(R.id.avataruser);
        this.sobaidang = findViewById(R.id.sobaidang);
        this.sodanhgia = findViewById(R.id.sodanhgia);
        this.sobaocao = findViewById(R.id.sobaocao);

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
                sodanhgia.setText(resultSet.getFloat("RatingCount")+"");
                sobaocao.setText(resultSet.getInt("ReportCount")+"");

                lbltenuser.setText("Tên: "+resultSet.getString("Name"));
                lbllopuser.setText("Lớp: "+resultSet.getString("Class"));
                lblmssvuser.setText("MSSV: "+resultSet.getString("StudentId"));
                lblsdtuser.setText("SDT: "+resultSet.getString("Phone"));
            }

            con.close();

        }catch (Exception e){

        }



    }
}
