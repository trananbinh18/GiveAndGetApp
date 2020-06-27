package com.example.giveandgetapp.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.giveandgetapp.PostDetailActivity;
import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ResultSearchAdapter extends BaseAdapter {
    public Context _context;
    public ArrayList<ResultSearch> _listResultSearch;
    private LayoutInflater inflater;
    private Activity activity;

    public ResultSearchAdapter(Activity activity, Context context, ArrayList<ResultSearch> listResultSearch){
        this._context = context;
        this._listResultSearch = listResultSearch;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return _listResultSearch.size();
    }

    @Override
    public Object getItem(int i) {
        return _listResultSearch.get(i);
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
            view = inflater.inflate(R.layout.listviewresult_searchfragment, null);
        ImageView imagePost = view.findViewById(R.id.imagePostSearch);
        TextView txtTitlePostSearch = view.findViewById(R.id.posttitleresultsearch);
        TextView txtStatusPostSearch = view.findViewById(R.id.poststatusresultsearch);
        LinearLayout layout1searchfragment = view.findViewById(R.id.layout1searchfragment);

        ResultSearch resultSearch = _listResultSearch.get(i);
        txtTitlePostSearch.setText(resultSearch.postTitle);
        if(resultSearch.postStatus == 1){
            txtStatusPostSearch.setText("Trạng thái bài viết: Còn hạn");
        }else if (resultSearch.postStatus == 2){
            txtStatusPostSearch.setText("Trạng thái bài viết: Hết hạn");
        }else if (resultSearch.postStatus == 3){
            txtStatusPostSearch.setText("Trạng thái bài viết: Chờ xác nhận");
        }else if (resultSearch.postStatus == 4){
            txtStatusPostSearch.setText("Trạng thái bài viết: Chờ đánh giá");
        }else {
            txtStatusPostSearch.setText("Trạng thái bài viết: Đã đóng");
        }
        imagePost.setImageBitmap(resultSearch.postImage);

        layout1searchfragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("Post_Id",resultSearch.postId);
                activity.startActivityForResult(intent,10);
            }
        });

        return view;
    }


}