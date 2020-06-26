package com.example.giveandgetapp.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.giveandgetapp.R;

import java.util.ArrayList;

public class ReceivePostAdapter extends BaseAdapter {
    private Context _context;
    public ArrayList<PostProfile> _listPostReceive;
    private LayoutInflater inflater;
    private ArrayList<Bitmap> _listImage;



    public ReceivePostAdapter(Context context, ArrayList<PostProfile> listPostReceive, ArrayList<Bitmap> listImage){
        this._context = context;
        this._listPostReceive = listPostReceive;
        this._listImage = listImage;
    }

    public void setListPostReceive(ArrayList<PostProfile> listPostReceive, ArrayList<Bitmap> listImage){
        this._listPostReceive = listPostReceive;
        this._listImage = listImage;
    }

    @Override
    public int getCount() {
        return _listPostReceive.size();
    }

    @Override
    public Object getItem(int position) {
        return _listPostReceive.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.post_item_actor, null);

        PostProfile item =_listPostReceive.get(position);

        LinearLayout layoutItem = convertView.findViewById(R.id.layoutItem);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);
        ImageView imgStatus = convertView.findViewById(R.id.imgStatus);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);

//        String statusStr = (item.status == 1)?"Chưa hết hạn":(item.status == 2)?"Đã hết hạn":(item.status == 3)?"Đang chờ Xác nhận":"Đóng";
//        txtStatus.setText(statusStr);
        switch (item.status){
            case 1:
                imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_fiber_manual_record_green_24dp));
                break;
            case 2:
                imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_fiber_manual_record_red_24dp));
                break;
            case 3:
                imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_fiber_manual_record_pink_24dp));
                break;
            case 4:
                imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_fiber_manual_record_yellow_24dp));
                break;
            default:
                imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_fiber_manual_record_gray_24dp));
                break;
        }

//        txtTitle.setText(item.title);
        layoutItem.setBackground(new BitmapDrawable(_context.getResources(), _listImage.get(position)));


        return convertView;
    }
}
