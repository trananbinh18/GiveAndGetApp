package com.example.giveandgetapp.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ResultSearchAdapter extends BaseAdapter {
    public Context _context;
    public ArrayList<ResultSearch> _listResultSearch;
    private LayoutInflater inflater;

    public ResultSearchAdapter(Context context, ArrayList<ResultSearch> listResultSearch){
        this._context = context;
        this._listResultSearch = listResultSearch;
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

        ResultSearch resultSearch = _listResultSearch.get(i);
        txtTitlePostSearch.setText(resultSearch.postTitle);
        imagePost.setImageBitmap(resultSearch.postImage);

        return view;
    }


}