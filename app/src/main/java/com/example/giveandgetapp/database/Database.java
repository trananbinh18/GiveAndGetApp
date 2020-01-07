package com.example.giveandgetapp.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.example.giveandgetapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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




}
