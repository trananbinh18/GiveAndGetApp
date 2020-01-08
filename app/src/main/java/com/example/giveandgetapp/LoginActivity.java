package com.example.giveandgetapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.giveandgetapp.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends Activity {
    private Database _database;
    private Button _btnLogin;
    private Button _btnCancel;
    private EditText _txtEmail;
    private EditText _txtPassword;
    private ProgressBar _progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _database = new Database(this);

        //View element
        _btnLogin = findViewById(R.id.btn_login);
        _btnCancel = findViewById(R.id.btn_cancel);
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
                String query = "SELECT * FROM [User] WHERE Email = '"+email+"' AND Password = '"+password+"'";
                ResultSet resultSet = _database.excuteCommand(con, query);
                try {
                    if(resultSet.next()){
                        con.close();
                        moveToMainActivity();

                    }else{
                        con.close();
                        Toast.makeText(LoginActivity.this , "Login Fail" , Toast.LENGTH_LONG).show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                _progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void moveToMainActivity(){
        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goToNextActivity);
    }


}
