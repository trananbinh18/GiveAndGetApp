package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private EditText _txtNameRegister;
    private EditText _txtMSSVRegister;
    private EditText _txtSDTRegister;
    private EditText _txtEmailRegister;
    private EditText _txtPasswordRegister;
    private EditText _txtVerifyPasswordRegister;
    private Button _btnSaveRegister;
    private Button _btnCancelRegister;
    private TextView _txtMessageErrorRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Khai báo
        _txtNameRegister = findViewById(R.id.txtNameRegister);
        _txtMSSVRegister = findViewById(R.id.txtMSSVRegister);
        _txtSDTRegister = findViewById(R.id.txtSDTRegister);
        _txtEmailRegister = findViewById(R.id.txtEmailRegister);
        _txtPasswordRegister = findViewById(R.id.txtPasswordRegister);
        _txtVerifyPasswordRegister = findViewById(R.id.txtVerifyPasswordRegister);

        _btnSaveRegister = findViewById(R.id.btnSaveRegister);
        _btnCancelRegister = findViewById(R.id.btnCancelRegister);
        _txtMessageErrorRegister = findViewById(R.id.messageErrorRegister);
    }
}
