package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ResultListUserAdapter extends BaseAdapter {
    public Context _context;
    public ArrayList<ResultListUser> _listResultUser;
    private LayoutInflater inflater;
    private Activity activity;

    public ResultListUserAdapter(Activity activity, Context context, ArrayList<ResultListUser> listResultUser){
        this._context = context;
        this._listResultUser = listResultUser;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return _listResultUser.size();
    }

    @Override
    public Object getItem(int i) {
        return _listResultUser.get(i);
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
            view = inflater.inflate(R.layout.listuser_like_register, null);
        ImageView _userImage = view.findViewById(R.id.imageUserListUser);
        TextView _userName = view.findViewById(R.id.nameUserListUser);
        TextView _timeLiked = view.findViewById(R.id.timeLikedListUser);
        TextView _timeRegistered = view.findViewById(R.id.timeRegisterListUser);
        LinearLayout _layout1ListUser = view.findViewById(R.id.layout1listUser);

        ResultListUser resultListUser = _listResultUser.get(i);
        _userImage.setImageBitmap(resultListUser.userImage);
        _userName.setText(resultListUser.userName);
        _timeLiked.setText(resultListUser.timeLiked.toString());
        _timeRegistered.setText(resultListUser.timeRegistered.toString());


        _layout1ListUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("Post_Id",resultListUser.postId);
                activity.startActivityForResult(intent,10);
            }
        });

        return view;
    }
}
