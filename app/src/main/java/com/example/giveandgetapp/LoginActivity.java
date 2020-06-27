package com.example.giveandgetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SymbolTable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends Activity {
    private Database _database;
    private Button _btnLogin;
    private Button _btnRegister;
    private Button _btnCancel;
    private EditText _txtEmail;
    private EditText _txtPassword;
    private ProgressBar _progressBar;
    private SessionManager _sessionManager;
    private Button _btnAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sessionManager = new SessionManager(this);

//        _sessionManager.logout();
        //Check is Login
        boolean isLogin = _sessionManager.isLogin();
        if(isLogin){
            moveToMainActivity();
        }

        //Setup View
        setContentView(R.layout.activity_login);

        _database = new Database(this);

        //View element
        _btnLogin = findViewById(R.id.btn_login);
        _btnRegister = findViewById(R.id.btn_register);
        _btnCancel = findViewById(R.id.btn_cancel);
        _btnAboutUs = findViewById(R.id.btnAboutUs);
        _txtEmail = findViewById(R.id.txt_email);
        _txtPassword = findViewById(R.id.txt_password);
        _progressBar = findViewById(R.id.progressBar);

        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Add Click listener for Login btn
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progressBar.setVisibility(View.VISIBLE);

                String email = _txtEmail.getText().toString().trim();
                String password = _txtPassword.getText().toString().trim();


                Connection con = _database.connectToDatabase();
                String query = "SELECT * FROM [User] WHERE Email = '"+email+"' AND Password = '"+password+"' AND IsVerify=1";
                ResultSet resultSet = _database.excuteCommand(con, query);
                try {
                    if(resultSet.next()){
                        if(createSessionForUser(con, resultSet)) {
                            con.close();
                            moveToMainActivity();
                            return;
                        }

                        con.close();
                        moveToMainActivity();
                        return;
                    }

                    con.close();
                    Toast.makeText(LoginActivity.this , "Không thể đăng nhập" , Toast.LENGTH_LONG).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                _progressBar.setVisibility(View.INVISIBLE);
            }
        });

        //Action button Register
        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        _btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder _dialogAboutUs = new AlertDialog.Builder(LoginActivity.this);
                View abc = getLayoutInflater().inflate(R.layout.dialog_about_us,null);
                _dialogAboutUs.setView(abc);
                final AlertDialog dialog = _dialogAboutUs.create();
                Button _cancelDialogAboutUs = (Button) abc.findViewById(R.id.btnCancelAboutUs);
                _cancelDialogAboutUs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    private void moveToMainActivity(){
        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
        goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToNextActivity);
        ActivityCompat.finishAffinity(this);

    }

    private boolean createSessionForUser(Connection con, ResultSet resultSet){
        try {
            Bitmap avatarImg;
            int idR;
            String studentIdR;
            String nameR;
            String emailR;
            String classR;
            String phoneR;
            int genderR;

            avatarImg = _database.getImageInDatabase(con, resultSet.getInt("Avatar"));
           idR = resultSet.getInt("Id");
           studentIdR = resultSet.getString("StudentId");
           nameR = resultSet.getString("Name");
           emailR = resultSet.getString("Email");
           classR = resultSet.getString("Class");
           phoneR = resultSet.getString("Phone");
           genderR = resultSet.getInt("Gender");

            boolean isCreatedSession = _sessionManager.createSession(idR,avatarImg,studentIdR,emailR,nameR,classR,phoneR,genderR);

            return isCreatedSession;
       } catch (SQLException e) {
           e.printStackTrace();
       }
        return false;
    }





}
