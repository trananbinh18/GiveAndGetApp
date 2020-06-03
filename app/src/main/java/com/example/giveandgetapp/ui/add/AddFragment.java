package com.example.giveandgetapp.ui.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.giveandgetapp.LoginActivity;
import com.example.giveandgetapp.R;
import com.example.giveandgetapp.database.Catalog;
import com.example.giveandgetapp.database.Database;
import com.example.giveandgetapp.database.SessionManager;
import com.example.giveandgetapp.database.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
    public final int PICK_IMAGE_ONE = 1;
    public final int PICK_IMAGE_TWO = 2;
    public final int PICK_IMAGE_THREE = 3;

    private boolean[] _isPickImage;

    //Component
    private ImageButton _imgPicker1;
    private ImageButton _imgPicker2;
    private ImageButton _imgPicker3;
    private Button _btnAdd;
    private Spinner _cbxCatalogy;
    private EditText _txtTitle;
    private EditText _txtContent;
    private RadioGroup _rdoGroupPickType;
    private RadioGroup _rdoGroupExpireType;

    private Database _database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_add, container, false);

        _isPickImage = new boolean[]{
                false,false,false
        };

        _database = new Database(root.getContext());

        _imgPicker1 = root.findViewById(R.id.imgPicker1);
        _imgPicker2 = root.findViewById(R.id.imgPicker2);
        _imgPicker3 = root.findViewById(R.id.imgPicker3);
        _btnAdd = root.findViewById(R.id.btnAdd);
        _cbxCatalogy = root.findViewById(R.id.cbxCatalogy);
        _txtTitle = root.findViewById(R.id.txtTitle);
        _txtContent = root.findViewById(R.id.txtContent);
        _rdoGroupPickType = root.findViewById(R.id.rdoGroupPickType);
        _rdoGroupExpireType = root.findViewById(R.id.rdoGroupExpireType);

        _imgPicker1.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_ONE));
        _imgPicker2.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_TWO));
        _imgPicker3.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_THREE));

        Connection con = _database.connectToDatabase();
        String query = "SELECT * FROM [Catalog]";
        ResultSet rs = _database.excuteCommand(con, query);
        try {
            ArrayList<Catalog> arrayData = new ArrayList<Catalog>();
            while (rs.next()){
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                Catalog element = new Catalog(id, name);
                arrayData.add(element);
            }
            con.close();
            ArrayAdapter<Catalog> adapterSpiner = new ArrayAdapter<Catalog>(root.getContext(), android.R.layout.simple_spinner_dropdown_item, arrayData);
            adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _cbxCatalogy.setAdapter(adapterSpiner);

        }catch (SQLException e) {
            e.printStackTrace();
        }


        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(checkIsValid(root.getContext())){
                   try {
                       //Upload image
                       int[] arrayImageId = uploadImage();

                       //Get User session
                       SessionManager sessionManager = new SessionManager(root.getContext());
                       User user = sessionManager.getUserDetail();

                       //Setup addition data
                       SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                       Date date = new Date();
                       Calendar calendar = Calendar.getInstance();
                       calendar.setTime(date);

                       int amountDay = (_rdoGroupExpireType.getCheckedRadioButtonId() == R.id.rdo3Day)?3:
                               (_rdoGroupExpireType.getCheckedRadioButtonId() == R.id.rdo5Day)?5:7;
                       calendar.add(Calendar.DAY_OF_MONTH, amountDay);


                       //Setup Data
                       String catalogId = ((Catalog)_cbxCatalogy.getSelectedItem()).id+"";
                       String actor = user.id+"";
                       String image = (arrayImageId[0]==0)?"NULL":arrayImageId[0]+"";
                       String image2 = (arrayImageId[1]==0)?"NULL":arrayImageId[1]+"";
                       String image3 = (arrayImageId[2]==0)?"NULL":arrayImageId[2]+"";
                       String title = _txtTitle.getText().toString();
                       String content = _txtContent.getText().toString();
                       String create_date = formater.format(date);
                       String expire_date = formater.format(calendar.getTime());
                       String given_type = (_rdoGroupPickType.getCheckedRadioButtonId() == R.id.rdoPick)?"1":"2";
                       String status = "1";
                       String expireType = amountDay+"";

                       String query = "INSERT INTO [Post]" +
                               "           ([CatalogId]" +
                               "           ,[Actor]" +
                               "           ,[Image]" +
                               "           ,[Image2]" +
                               "           ,[Image3]" +
                               "           ,[Title]" +
                               "           ,[Contents]" +
                               "           ,[CreateDate]" +
                               "           ,[ExpireDate]" +
                               "           ,[GiveType]" +
                               "           ,[Status]" +
                               "           ,[ReceiverCount]" +
                               "           ,[ExpireType])" +
                               "     VALUES" +
                               "           ("+ catalogId +
                               "           ,"+ actor +
                               "           ," + image +
                               "           ," + image2 +
                               "           ," + image3 +
                               "           ," + "N'" +title+ "'"+
                               "           ," + "N'" +content+ "'" +
                               "           ," + "CONVERT(datetime,'" +create_date+"',120)" +
                               "           ," + "CONVERT(datetime,'" +expire_date+ "',120)" +
                               "           ," + given_type +
                               "           ," + status +
                               "           ," + "0" +
                               "           ," + expireType +
                               ")";

                       Connection con = _database.connectToDatabase();
                       _database.excuteCommand(con, query);
                       con.close();
                       Toast.makeText( root.getContext(), "Bài đăng đã hoàn tất" , Toast.LENGTH_LONG).show();

                       Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.navigation_dashboard);

                       resetFlagment();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
            }
        });
        return root;
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
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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



    private boolean checkIsValid(Context context) {
        if(_isPickImage[0] == false && _isPickImage[1] == false && _isPickImage[2] == false){
            Toast.makeText( context, "Không thể đăng bài: Chọn ít nhất một hình" , Toast.LENGTH_LONG).show();
            return false;
        }else if(_txtTitle.getText().toString().length() == 0){
            Toast.makeText( context, "Không thể đăng bài: Không được để trống Tiêu đề" , Toast.LENGTH_LONG).show();
            return false;
        }else if(_txtContent.getText().toString().length() == 0){
            Toast.makeText( context, "Không thể đăng bài: Không được để trống Nội dung" , Toast.LENGTH_LONG).show();
            return false;
        }else if(_rdoGroupPickType.getCheckedRadioButtonId() == -1){
            Toast.makeText( context, "Không thể đăng bài: Chưa chọn Hình thức" , Toast.LENGTH_LONG).show();
            return false;
        }else if(_rdoGroupExpireType.getCheckedRadioButtonId() == -1){
            Toast.makeText( context, "Không thể đăng bài: Chưa chọn Hạn bài" , Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private int[] uploadImage() throws SQLException {
        int[] arrayImageId = new int[3];
        if(_isPickImage[0] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgPicker1.getDrawable()).getBitmap();
            Bitmap resizeBitmap =  Bitmap.createScaledBitmap(bitmap, 730, 430, false);

            byte[] arrayByte = _database.convertBitmapToBytes(resizeBitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[0] = imageId;
            con.close();
        }

        if(_isPickImage[1] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgPicker2.getDrawable()).getBitmap();
            Bitmap resizeBitmap =  Bitmap.createScaledBitmap(bitmap, 730, 430, false);

            byte[] arrayByte = _database.convertBitmapToBytes(resizeBitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[1] = imageId;
            con.close();
        }

        if(_isPickImage[2] == true){
            Bitmap bitmap = ((BitmapDrawable)_imgPicker3.getDrawable()).getBitmap();
            Bitmap resizeBitmap =  Bitmap.createScaledBitmap(bitmap, 730, 430, false);

            byte[] arrayByte = _database.convertBitmapToBytes(resizeBitmap);
            Connection con = _database.connectToDatabase();
            int imageId = _database.saveImageIntoDatabase(con, arrayByte);
            arrayImageId[2] = imageId;
            con.close();
        }

        return arrayImageId;
    }


    private void resetFlagment(){
        _imgPicker1.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        _imgPicker2.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        _imgPicker3.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        _txtTitle.setText("");
        _txtContent.setText("");
        _rdoGroupPickType.clearCheck();
        _rdoGroupExpireType.clearCheck();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICK_IMAGE_ONE:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null){
                        Uri imgURI = data.getData();
                        _imgPicker1.setImageURI(imgURI);
                        _isPickImage[0] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgPicker1.setImageBitmap(photo);
                        _isPickImage[0] = true;
                    }
                }
                break;
            case PICK_IMAGE_TWO:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null){
                        Uri imgURI = data.getData();
                        _imgPicker2.setImageURI(imgURI);
                        _isPickImage[1] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgPicker2.setImageBitmap(photo);
                        _isPickImage[1] = true;
                    }

                }
                break;
            case PICK_IMAGE_THREE:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getDataString() != null) {
                        Uri imgURI = data.getData();
                        _imgPicker3.setImageURI(imgURI);
                        _isPickImage[2] = true;
                    }else{
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        _imgPicker3.setImageBitmap(photo);
                        _isPickImage[2] = true;
                    }

                }
                break;
        }
    }
}