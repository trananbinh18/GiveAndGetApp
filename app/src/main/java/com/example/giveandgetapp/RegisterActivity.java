package com.example.giveandgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText _txtNameRegister;
    private EditText _txtMSSVRegister;
    private EditText _txtSDTRegister;
    private EditText _txtEmailRegister;
    private EditText _txtPasswordRegister;
    private EditText _txtVerifyPasswordRegister;
    private RadioGroup _rdrGender;
    private Button _btnSaveRegister;
    private Button _btnCancelRegister;
    private TextView _txtMessageErrorRegister;
    private ProgressBar _progressBar;


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
        _progressBar = findViewById(R.id.progressBar);
        _txtVerifyPasswordRegister = findViewById(R.id.txtVerifyPasswordRegister);
        _txtMessageErrorRegister = findViewById(R.id.messageErrorRegister);
        _rdrGender = findViewById(R.id.rdrGender);

        _btnSaveRegister = findViewById(R.id.btnSaveRegister);
        _btnCancelRegister = findViewById(R.id.btnCancelRegister);

        // Button Cancel
        _btnCancelRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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

                String [] arrEmail = valueEmail.split("@");
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
                if (!valuePassword.equals(valueVerifyPassword))
                {
                    _txtMessageErrorRegister.setText("Mật khẩu không giống nhau");
                    return;
                }
                if (valueName.isEmpty() || valueSDT.isEmpty() || valueEmail.isEmpty() || valuePassword.isEmpty() || valueVerifyPassword.isEmpty())
                {
                    _txtMessageErrorRegister.setText("Vui lòng nhập những trường bắt buộc");
                    return;
                }
                if(!(arrEmail[1].equals(new String("vanlanguni.vn")) || arrEmail[1].equals(new String("vanlanguni.edu.vn")))){
                    _txtMessageErrorRegister.setText("Vui lòng sử dụng Email của trường.");
                    return;
                }



                register();


            }
        });
    }

    private void register(){
        _btnSaveRegister.setClickable(false);
        _progressBar.setVisibility(View.VISIBLE);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://cntttest.vanlanguni.edu.vn:18080/Cap22T6/api/PublicAPI/RegisterUser";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            _btnSaveRegister.setClickable(true);
                            JSONObject jsonObject = new JSONObject(response);
                            String errorString = jsonObject.get("Data").toString();
                            if(errorString.length() != 0){
                                _txtMessageErrorRegister.setText(errorString);
                            }else{
                                _txtMessageErrorRegister.setTextColor(Color.GREEN);
                                _txtMessageErrorRegister.setText("Tài khoản đã được đăng ký \n hãy xác nhận email trước khi đăng nhập");
                            }
                            _progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            _txtMessageErrorRegister.setText("Không thể kết nối tới server");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _txtMessageErrorRegister.setText("Không thể kết nối tới server");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("mssv",_txtMSSVRegister.getText().toString());
                params.put("phone",_txtSDTRegister.getText().toString());
                params.put("email",_txtEmailRegister.getText().toString());
                params.put("password",_txtPasswordRegister.getText().toString());
                params.put("name",_txtNameRegister.getText().toString());
                params.put("gender",(_rdrGender.getCheckedRadioButtonId() == R.id.rbtnMale)?"1":"0");

                return params;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


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
