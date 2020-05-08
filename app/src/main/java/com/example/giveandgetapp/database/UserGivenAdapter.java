package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giveandgetapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class UserGivenAdapter extends BaseAdapter {
    public Context _context;
    public ArrayList<UserGiven> _listUserGiven;
    private LayoutInflater inflater;
    private double _pecent;
    private int _giveType;
    public int _idUserChoosed;
    private ScrollView _parentScrollView;

    public UserGivenAdapter(Context context, ArrayList<UserGiven> listUserGiven, int giveType, ScrollView parentScrollView){
        this._context = context;
        this._listUserGiven = listUserGiven;
        this._giveType = giveType;
        this._pecent = (_listUserGiven.size() != 0)?100/ (_listUserGiven.size()):0;
        this._parentScrollView = parentScrollView;
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

        //If it is random
        if(this._giveType != 1){
            imageGiven.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context,"Chọn vào nút random để chọn người ngẫu nhiên.",Toast.LENGTH_LONG).show();
                    _parentScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            _parentScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            });
        }else{
            txtPercent.setVisibility(View.GONE);
            UserGiven userGiven = _listUserGiven.get(i);
            imageGiven.setOnClickListener(getGiveClickListener(userGiven, viewGroup));
        }

        UserGiven userGiven = _listUserGiven.get(i);
        txtUserName.setText(userGiven.userName);
        imgAvatarUser.setImageBitmap(userGiven.userImage);
        txtPercent.setText(String.format("%.2f",_pecent));


        return view;
    }

    private View.OnClickListener getGiveClickListener(UserGiven userGiven, ViewGroup parrent) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0 ;i<parrent.getChildCount();i++){
                    ImageButton imgBtn = parrent.getChildAt(i).findViewById(R.id.iconGiven);
                    imgBtn.setImageResource(R.drawable.ic_hand_foreground);
                }

                ImageButton imageButton = (ImageButton)v;
                _idUserChoosed = userGiven.userId;
                imageButton.setImageResource(R.drawable.ic_hand_fill_foreground);
            }
        };

        return listener;
    }
}
