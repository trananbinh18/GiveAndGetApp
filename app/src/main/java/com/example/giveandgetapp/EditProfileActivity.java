package com.example.giveandgetapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private User _user;
    private TextView _txtMessageError;

    public final int REQUEST_CODE_IMG_AVATAR = 1;
    private boolean isChangeAvatar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Khai báo
        _sessionManager = new SessionManager(this);
        _user = _sessionManager.getUserDetail();
        _imageUser = findViewById(R.id.avateruseredit);
        _txtChangeAvatar = findViewById(R.id.txtchangeavatar);
        _txtName = findViewById(R.id.txtnameedit);
        _txtGioitinh = findViewById(R.id.txtgioitinhedit);
        _txtLop = findViewById(R.id.txtlopedit);
        _txtMssv = findViewById(R.id.txtmssvedit);
        _txtSdt = findViewById(R.id.txtsdtedit);
        _txtMessageError = findViewById(R.id.messageerror);

        _txtEmail = findViewById(R.id.txtemailedit);
        _txtEmail.setFocusable(true);
        _txtEmail.setFocusableInTouchMode(true);
        _txtEmail.setInputType(InputType.TYPE_NULL);
        _txtGioitinh.setFocusableInTouchMode(true);
        _txtGioitinh.setInputType(InputType.TYPE_NULL);

        _btnLuu = findViewById(R.id.btnluuedit);
        _btnHuy = findViewById(R.id.btnhuyedit);

        //Load data
        _database = new Database(this);
        Connection con = _database.connectToDatabase();

        _imageUser.setImageBitmap(BitmapFactory.decodeFile(_user.avatar));
        _txtName.setText(_user.name);
        if(_user.gender == 1){
            _txtGioitinh.setText("Nam");
        }else {
            _txtGioitinh.setText("Nữ");
        }
        _txtLop.setText(_user.clazz);
        _txtMssv.setText(_user.studentId);
        _txtSdt.setText(_user.phone);
        _txtEmail.setText(_user.email);




        //Lưu
        _btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check điều kiện input
                String congchuoi = _txtSdt.getText().toString()
                        + _txtMssv.getText().toString() + _txtLop.getText().toString();
//                String abc = congchuoi.replaceAll(" ","");
                String getName = _txtName.getText().toString();
                Pattern p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(congchuoi);
                boolean checkspecialchar = m.find();

                Pattern pname = Pattern.compile("[^A-Za-z0-9 àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ]", Pattern.CASE_INSENSITIVE);
                Matcher mname = pname.matcher(getName);
                boolean checkName = mname.find();
                if(checkName == true){
                    _txtMessageError.setText("Vui lòng không nhập kí tự đặc biệt");
                    return;
                }
                if(_txtName.getText().toString().isEmpty() || _txtGioitinh.getText().toString().isEmpty() || _txtSdt.getText().toString().isEmpty()
                        || _txtMssv.getText().toString().isEmpty() || _txtLop.getText().toString().isEmpty()){
                    _txtMessageError.setText("Vui lòng nhập những trường bắt buộc");
                    return;
                }
                if(checkspecialchar){
                    _txtMessageError.setText("Vui lòng không nhập kí tự đặc biệt");
                    return;
                }
                Connection con = _database.connectToDatabase();
                String strQueryAvatar = "";
                if(isChangeAvatar){
                    Bitmap bitmap = ((BitmapDrawable)_imageUser.getDrawable()).getBitmap();
                    Bitmap resizeBitmap =  Bitmap.createScaledBitmap(bitmap, 430, 430, false);

                    byte[] arrayByte = _database.convertBitmapToBytes(resizeBitmap);
                    int imageId = _database.saveImageIntoDatabase(con, arrayByte);
                    strQueryAvatar = " Avatar = "+imageId+ ",";
                }

                String strQueryUpdateUser = "" +
                        "UPDATE [User] " +
                        "   SET" + strQueryAvatar +
                        "      StudentId = '" + _txtMssv.getText().toString() + "'"+
                        "      ,Name = '" + _txtName.getText().toString()+ "'"+
                        "      ,Class = '" + _txtLop.getText().toString()+ "'"+
                        "      ,Phone = '" + _txtSdt.getText().toString()+ "'" +
                        " WHERE  Id = "+ _user.id;

                _database.excuteCommand(con, strQueryUpdateUser);

                if(isChangeAvatar){
                    _sessionManager.updateSession(((BitmapDrawable)_imageUser.getDrawable()).getBitmap(),_txtMssv.getText().toString(),_txtName.getText().toString(),_txtLop.getText().toString(),_txtSdt.getText().toString());
                }else {
                    _sessionManager.updateSession(null,_txtMssv.getText().toString(),_txtName.getText().toString(),_txtLop.getText().toString(),_txtSdt.getText().toString());
                }

                setResult(Activity.RESULT_OK);
                finish();


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
        _txtChangeAvatar.setOnClickListener(getListenerForImagePicker(REQUEST_CODE_IMG_AVATAR));
    }

    private View.OnClickListener getListenerForImagePicker(final int imagePickerIndex){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Storage
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                //Camera
                Intent[] intentArray = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, intent);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");


                startActivityForResult(Intent.createChooser(chooserIntent, "Chọn Ảnh"), imagePickerIndex);
            }
        };

        return listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_CODE_IMG_AVATAR:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null){
                        Uri imgURI = data.getData();
                        _imageUser.setImageURI(imgURI);
                        isChangeAvatar = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imageUser.setImageBitmap(photo);
                        isChangeAvatar = true;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}

