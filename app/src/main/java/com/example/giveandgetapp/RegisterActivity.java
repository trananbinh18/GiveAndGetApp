package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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

        //Action button Register
        _btnSaveRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueName = _txtNameRegister.getText().toString();
                String valueMSSV = _txtMSSVRegister.getText().toString();
                String valueSDT = _txtSDTRegister.getText().toString();
                String valueEmail = _txtEmailRegister.getText().toString();
                String valuePassword = _txtPasswordRegister.getText().toString();
                String valueVerifyPassword = _txtVerifyPasswordRegister.getText().toString();
                if(!validCellPhone(valueSDT))
                {
                    _txtMessageErrorRegister.setText("Vui lòng nhập đúng định dạng số điện thoại");
                    return;
                }
                if(!isEmailValid(valueEmail))
                {
                    _txtMessageErrorRegister.setText("Vui lòng nhập đúng định dạng email");
                    return;
                }
                if (valuePassword != valueVerifyPassword)
                {
                    _txtMessageErrorRegister.setText("Mật khẩu không giống nhau");
                    return;
                }
                if (valueName.isEmpty() || valueSDT.isEmpty() || valueEmail.isEmpty() || valuePassword.isEmpty() || valueVerifyPassword.isEmpty())
                {
                    _txtMessageErrorRegister.setText("Vui lòng nhập những trường bắt buộc");
                    return;
                }
            }
        });
    }

    //Check email format
    public static  boolean isEmailValid (CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Check phone format
    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }
}
