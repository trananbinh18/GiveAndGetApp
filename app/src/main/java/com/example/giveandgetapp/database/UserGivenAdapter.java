package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class UserGivenAdapter extends BaseAdapter {
    public Context _context;
    public ArrayList<UserGiven> _listUserGiven;
    private LayoutInflater inflater;
    private double _pecent;

    public UserGivenAdapter(Context context, ArrayList<UserGiven> listUserGiven){
        this._context = context;
        this._listUserGiven = listUserGiven;
        _pecent = 100/ (_listUserGiven.size());

    }

    @Override
    public int getCount() {
        return _listUserGiven.size();
    }

    @Override
    public Object getItem(int i) {
        return _listUserGiven.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.listviewitem_giveactivity, null);

        ImageView imgAvatarUser = view.findViewById(R.id.userGivenImage);
        TextView txtUserName = view.findViewById(R.id.usernamegiveactivity);
        TextView txtPercent = view.findViewById(R.id.percentusergiveactivity);
        ImageButton imageGiven = view.findViewById(R.id.iconGiven);

        UserGiven userGiven = _listUserGiven.get(i);
        txtUserName.setText(userGiven.userName);
        imgAvatarUser.setImageBitmap(userGiven.userImage);
        txtPercent.setText(String.format("%.2f",_pecent));
        return view;
    }
}
