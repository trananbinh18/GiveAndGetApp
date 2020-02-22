package com.example.giveandgetapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditProfileActivity extends AppCompatActivity {

    private SessionManager _sessionManager;
    private Database _database;
    private ImageView _imageUser;
    private TextView _txtChangeAvatar;
    private EditText _txtName;
    private EditText _txtGioitinh;
    private EditText _txtLop;
    private EditText _txtMssv;
    private EditText _txtSdt;
    private EditText _txtEmail;
    private Button _btnLuu;
    private Button _btnHuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Khai báo
        _sessionManager = new SessionManager(this);
        User user = _sessionManager.getUserDetail();
        _imageUser = findViewById(R.id.avateruseredit);
        _txtChangeAvatar = findViewById(R.id.txtchangeavatar);
        _txtName = findViewById(R.id.txtnameedit);
        _txtGioitinh = findViewById(R.id.txtgioitinhedit);
        _txtLop = findViewById(R.id.txtlopedit);
        _txtMssv = findViewById(R.id.txtmssvedit);
        _txtSdt = findViewById(R.id.txtsdtedit);

        _txtEmail = findViewById(R.id.txtemailedit);
        _txtEmail.setFocusable(true);
        _txtEmail.setFocusableInTouchMode(true);
        _txtEmail.setInputType(InputType.TYPE_NULL);

        _btnLuu = findViewById(R.id.btnluuedit);
        _btnHuy = findViewById(R.id.btnhuyedit);

        //Load data
        _database = new Database(this);
        Connection con = _database.connectToDatabase();

        _imageUser.setImageBitmap(BitmapFactory.decodeFile(user.avatar));
        _txtName.setText(user.name);
        if(user.gender == 1){
            _txtGioitinh.setText("Nam");
        }else {
            _txtGioitinh.setText("Nữ");
        }
        _txtLop.setText(user.clazz);
        _txtMssv.setText(user.studentId);
        _txtSdt.setText(user.phone);
        _txtEmail.setText(user.email);

        //Lưu
        _btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Hủy
        _btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.super.onBackPressed();
            }
        });

        //Thay đổi ảnh đại diện
        _txtChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

