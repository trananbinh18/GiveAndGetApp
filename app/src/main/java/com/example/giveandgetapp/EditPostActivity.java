package com.example.giveandgetapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;

import com.example.giveandgetapp.database.Catalog;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.giveandgetapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditPostActivity extends AppCompatActivity {

    private Database _database;
    private SessionManager _sessionManager;
    private ImageButton _imgbtnPic1EditPost;
    private ImageButton _imgbtnPic2EditPost;
    private ImageButton _imgbtnPic3EditPost;
    private Spinner _cbxCatalogyEditPost;
    private EditText _txtTitleEditPost;
    private EditText _txtContentEditPost;
    private RadioButton _rbtnTuchonEditPost;
    private RadioButton _rbtnNgaunhienEditPost;
    private RadioButton _rbtn3ngayEditPost;
    private RadioButton _rbtn5ngayEditPost;
    private RadioButton _rbtn7ngayEditPost;
    private Button _btnLuuEditPost;
    private Button _btnHuyEditPost;
    private ArrayAdapter<Catalog> adapterSpiner;
    private ArrayList<Catalog> arrayData;
    private int _postId;
    private ArrayList<Bitmap> listImage;
    private boolean[] _isPickImage;
    private int[] _oldImageListId;
    private RadioGroup _rdoGroupExpireType;
    private RadioGroup _rdoGroupPickType;

    public final int PICK_IMAGE_ONE = 1;
    public final int PICK_IMAGE_TWO = 2;
    public final int PICK_IMAGE_THREE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        _sessionManager = new SessionManager(this);
        User user = _sessionManager.getUserDetail();
        _imgbtnPic1EditPost = findViewById(R.id.imgPicker1editpost);
        _imgbtnPic2EditPost = findViewById(R.id.imgPicker2editpost);
        _imgbtnPic3EditPost = findViewById(R.id.imgPicker3editpost);
        _cbxCatalogyEditPost = findViewById(R.id.cbxCatalogyeditpost);
        _txtTitleEditPost = findViewById(R.id.txtTitleeditpost);
        _txtContentEditPost = findViewById(R.id.txtContenteditpost);
        _rbtnTuchonEditPost = findViewById(R.id.rdoPickeditpost);
        _rbtnNgaunhienEditPost = findViewById(R.id.rdoRandomeditpost);
        _rbtn3ngayEditPost = findViewById(R.id.rdo3Dayeditpost);
        _rbtn5ngayEditPost = findViewById(R.id.rdo5Dayeditpost);
        _rbtn7ngayEditPost = findViewById(R.id.rdo7Dayeditpost);
        _btnLuuEditPost = findViewById(R.id.btnLuueditpost);
        _btnHuyEditPost = findViewById(R.id.btnHuyeditpost);
        _rdoGroupExpireType = findViewById(R.id.rdoGroupExpireType);
        _rdoGroupPickType = findViewById(R.id.rdoGroupPickType);


        _oldImageListId = new int[3];

        _isPickImage = new boolean[]{
                false,false,false
        };

        listImage = new ArrayList<Bitmap>();

        _imgbtnPic1EditPost.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_ONE));
        _imgbtnPic2EditPost.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_TWO));
        _imgbtnPic3EditPost.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_THREE));

        this._postId = getIntent().getIntExtra("Post_Id",0);

        //Load Data
        _database = new Database(this);
        Connection con = _database.connectToDatabase();
        String query = "SELECT * FROM Post WHERE Id = "+_postId;

        //Load data to spinner
        String queryCatalogy = "SELECT * FROM Catalog";
        ResultSet resultSet1 = _database.excuteCommand(con, queryCatalogy);
        try {
            arrayData = new ArrayList<Catalog>();
            while (resultSet1.next()){
                int id = resultSet1.getInt("Id");
                String name = resultSet1.getString("Name");
                Catalog element = new Catalog(id, name);
                arrayData.add(element);
            }
            adapterSpiner = new ArrayAdapter<Catalog>(this, android.R.layout.simple_spinner_dropdown_item, arrayData);
            adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _cbxCatalogyEditPost.setAdapter(adapterSpiner);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //Load Data Post
        ResultSet resultSet = _database.excuteCommand(con, query);
        try {
            if(resultSet.next()){
                Bitmap img = _database.getImageInDatabase(con,resultSet.getInt("Image"));
                _imgbtnPic1EditPost.setImageBitmap(img);
                listImage.add(img);
                _oldImageListId[0] = resultSet.getInt("Image");

                Bitmap img2 = _database.getImageInDatabase(con,resultSet.getInt("Image2"));
                if(img2 != null){
                    _imgbtnPic2EditPost.setImageBitmap(img2);
                    listImage.add(img2);
                    _oldImageListId[1] = resultSet.getInt("Image2");
                }

                Bitmap img3 = _database.getImageInDatabase(con,resultSet.getInt("Image3"));
                if(img3 != null){
                    _imgbtnPic3EditPost.setImageBitmap(img3);
                    listImage.add(img3);
                    _oldImageListId[2] = resultSet.getInt("Image3");
                }

                int catalogId = resultSet.getInt("CatalogId");
                for(int i=0;i<arrayData.size();i++)
                {
                    if(catalogId == arrayData.get(i).id){
                        _cbxCatalogyEditPost.setSelection(i);
                        break;
                    }
                }

                _txtTitleEditPost.setText(resultSet.getString("Title"));
                _txtContentEditPost.setText(resultSet.getString("Contents"));
                if(resultSet.getInt("GiveType") == 1)
                {
                    _rbtnTuchonEditPost.setChecked(true);
                }else if(resultSet.getInt("GiveType") == 2)
                {
                    _rbtnNgaunhienEditPost.setChecked(true);
                }
                if(resultSet.getInt("ExpireType")== 3)
                {
                    _rbtn3ngayEditPost.setChecked(true);
                } else if(resultSet.getInt("ExpireType")== 5)
                {
                    _rbtn5ngayEditPost.setChecked(true);
                } else if(resultSet.getInt("ExpireType")== 7)
                {
                    _rbtn7ngayEditPost.setChecked(true);
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Nút Hủy
        _btnHuyEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _btnLuuEditPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(checkIsValid(getApplicationContext())){
                    Connection con = _database.connectToDatabase();
                    try{
                        int[] arrayImageId = uploadImage();

                        //Get User session
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        User user = sessionManager.getUserDetail();

                        //Setup addition data
                        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        int amountDay = (_rdoGroupExpireType.getCheckedRadioButtonId() == R.id.rdo3Dayeditpost)?3:
                                (_rdoGroupExpireType.getCheckedRadioButtonId() == R.id.rdo5Dayeditpost)?5:7;
                        calendar.add(Calendar.DAY_OF_MONTH, amountDay);

                        //Setup Data
                        String catalogId = ((Catalog)_cbxCatalogyEditPost.getSelectedItem()).id+"";
                        String image = (arrayImageId[0]==0)?"NULL":arrayImageId[0]+"";
                        String image2 = (arrayImageId[1]==0)?"NULL":arrayImageId[1]+"";
                        String image3 = (arrayImageId[2]==0)?"NULL":arrayImageId[2]+"";
                        String title = _txtTitleEditPost.getText().toString();
                        String content = _txtContentEditPost.getText().toString();
                        String expire_date = formater.format(calendar.getTime());
                        String given_type = (_rdoGroupPickType.getCheckedRadioButtonId() == R.id.rdoPickeditpost)?"1":"2";
                        String expireType = amountDay+"";

                        String queryGetAllReciever = "SELECT UserId" +
                                "  FROM [Receive] " +
                                "  WHERE PostId = " + _postId;
                        ResultSet resultSet = _database.excuteCommand(con, queryGetAllReciever);

                        while (resultSet.next()){
                            String create_date = formater.format(date);

                            String queryAddNotification = "INSERT INTO [Notification]" +
                                    "           (PostId,UserId,Status,CreateDate,Title,Contents,Type)" +
                                    "     VALUES" +
                                    "           ("+_postId+","+resultSet.getInt("UserId") +
                                    "           ,1" +
                                    "           ," + "CONVERT(datetime,'" +create_date+"',120)"+
                                    "           ,N'Bài viết của "+user.name+" đã được chỉnh sửa nội dung'" +
                                    "           ,N'Bài viết "+user.name+" đã được chỉnh sửa nội dung'" +
                                    "           ,1)";

                            _database.excuteCommand(con,queryAddNotification);
                        }

                        String query = "UPDATE [Post]" +
                                "   SET CatalogId = "+catalogId +
                                "      ,Image = " + image+
                                "      ,Image2 = " + image2+
                                "      ,Image3 = " + image3+
                                "      ,Title = " + "N'" +title+ "'" +
                                "      ,Contents = " + "N'" +content+ "'" +
                                "      ,ExpireDate = " + "CONVERT(datetime,'" +expire_date+ "',120)"+
                                "      ,GiveType = " +given_type+
                                "      ,ExpireType = " +expireType+
                                " WHERE Id = "+_postId;


                        _database.excuteCommand(con, query);
                        con.close();
                        Toast.makeText(getApplicationContext(), "Chỉnh sửa bài viết hoàn tất" , Toast.LENGTH_LONG).show();



                    }catch (SQLException e) {

                    }finally {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent data = new Intent();
                    data.setData(Uri.parse(_postId+""));
                    setResult(RESULT_OK, data);

                    finish();
                }
            }
        });



    }

    @Override
    protected void onDestroy() {
        for (Bitmap img: listImage){
            if(img != null){
                img.recycle();
            }
        }
        super.onDestroy();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICK_IMAGE_ONE:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null){
                        Uri imgURI = data.getData();
                        _imgbtnPic1EditPost.setImageURI(imgURI);
                        _isPickImage[0] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgbtnPic1EditPost.setImageBitmap(photo);
                        _isPickImage[0] = true;
                    }
                }
                break;
            case PICK_IMAGE_TWO:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null){
                        Uri imgURI = data.getData();
                        _imgbtnPic2EditPost.setImageURI(imgURI);
                        _isPickImage[1] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgbtnPic2EditPost.setImageBitmap(photo);
                        _isPickImage[1] = true;
                    }

                }
                break;
            case PICK_IMAGE_THREE:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null) {
                        Uri imgURI = data.getData();
                        _imgbtnPic3EditPost.setImageURI(imgURI);
                        _isPickImage[2] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgbtnPic3EditPost.setImageBitmap(photo);
                        _isPickImage[2] = true;
                    }

                }
                break;
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    private boolean checkIsValid(Context context) {
       if(_txtTitleEditPost.getText().toString().length() == 0){
            Toast.makeText( context, "Không thể đăng bài: Không được để trống Tiêu đề" , Toast.LENGTH_LONG).show();
            return false;
        }else if(_txtContentEditPost.getText().toString().length() == 0){
            Toast.makeText( context, "Không thể đăng bài: Không được để trống Nội dung" , Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private int[] uploadImage() throws SQLException {
        int[] arrayImageId = new int[3];
        if(_isPickImage[0] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgbtnPic1EditPost.getDrawable()).getBitmap();
            byte[] arrayByte = _database.convertBitmapToBytes(bitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[0] = imageId;
            con.close();
        }else{
            arrayImageId[0] = _oldImageListId[0];
        }

        if(_isPickImage[1] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgbtnPic2EditPost.getDrawable()).getBitmap();
            byte[] arrayByte = _database.convertBitmapToBytes(bitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[1] = imageId;
            con.close();
        }else {
            arrayImageId[1] = _oldImageListId[1];
        }

        if(_isPickImage[2] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgbtnPic3EditPost.getDrawable()).getBitmap();
            byte[] arrayByte = _database.convertBitmapToBytes(bitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[2] = imageId;
            con.close();
        }else {
            arrayImageId[2] = _oldImageListId[2];
        }

        return arrayImageId;
    }
}

