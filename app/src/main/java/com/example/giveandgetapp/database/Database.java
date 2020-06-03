package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.example.giveandgetapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Context _context;

    public Database(Context context){
        this._context = context;
    }



    public Connection connectToDatabase()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            ConnectionURL = _context.getResources().getString(R.string.connection_string);


            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }



    public ResultSet excuteCommand(Connection con, String query){
        ResultSet rs = null;

        try {

            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }


    public int saveImageIntoDatabase(Connection con, byte[] byteImageArray) {
        try {
            String query = "INSERT INTO [Image] (Image) VALUES (?);" +
                    " SELECT SCOPE_IDENTITY()";
            PreparedStatement pre = con.prepareStatement(query);
            pre.setBytes(1, byteImageArray);

            ResultSet rs = pre.executeQuery();
            rs.next();

            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();

            return 0;
        }

    }

    public Bitmap getImageInDatabase(Connection con, int idImage) {
        try {
            String query = "SELECT Image FROM [Image] WHERE Id = "+idImage;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(!rs.next()){
                return null;
            }

            Blob blob = rs.getBlob("Image");
            byte[] immAsBytes = blob.getBytes(1, (int)blob.length());
            Bitmap img = BitmapFactory.decodeByteArray(immAsBytes, 0 ,immAsBytes.length);

            return img;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getImageInDatabaseInSquire(Connection con, int idImage) {
        try {
            String query = "SELECT Image FROM [Image] WHERE Id = "+idImage;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(!rs.next()){
                return null;
            }

            Blob blob = rs.getBlob("Image");
            byte[] immAsBytes = blob.getBytes(1, (int)blob.length());
            Bitmap img = BitmapFactory.decodeByteArray(immAsBytes, 0 ,immAsBytes.length);
            Bitmap resizeImg = Bitmap.createScaledBitmap(img, 320, 320, false);
            img.recycle();

            return resizeImg;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getImageInDatabaseInRec(Connection con, int idImage) {
        try {
            String query = "SELECT Image FROM [Image] WHERE Id = "+idImage;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(!rs.next()){
                return null;
            }

            Blob blob = rs.getBlob("Image");
            byte[] immAsBytes = blob.getBytes(1, (int)blob.length());
            Bitmap img = BitmapFactory.decodeByteArray(immAsBytes, 0 ,immAsBytes.length);
            Bitmap resizeImg = Bitmap.createScaledBitmap(img, 320, 750, false);
            img.recycle();

            return resizeImg;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public byte[] convertBitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        return byteArray;
    }



    public byte[] convertImageInputStreamToBytes(InputStream inputStream)  {

        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public Bitmap getBimapByBlob(Blob blob){
        try{
            byte[] immAsBytes = blob.getBytes(1, (int)blob.length());
            Bitmap img = BitmapFactory.decodeByteArray(immAsBytes, 0 ,immAsBytes.length);

            return img;
        }catch (SQLException e){
            return null;
        }

    }




}
