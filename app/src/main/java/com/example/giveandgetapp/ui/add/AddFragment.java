package com.example.giveandgetapp.ui.add;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.giveandgetapp.R;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
    public final int PICK_IMAGE_ONE = 1;
    public final int PICK_IMAGE_TWO = 2;
    public final int PICK_IMAGE_THREE = 3;

    private ImageButton imgPicker1;
    private ImageButton imgPicker2;
    private ImageButton imgPicker3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        imgPicker1 = root.findViewById(R.id.imgPicker1);
        imgPicker2 = root.findViewById(R.id.imgPicker2);
        imgPicker3 = root.findViewById(R.id.imgPicker3);

        imgPicker1.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_ONE));
        imgPicker2.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_TWO));
        imgPicker3.setOnClickListener(getListenerForImagePicker(PICK_IMAGE_THREE));

        return root;
    }

    private View.OnClickListener getListenerForImagePicker(final int imagePickerIndex){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), imagePickerIndex);
            }
        };
        return listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICK_IMAGE_ONE:
                if(resultCode == Activity.RESULT_OK){
                    Uri imgURI = data.getData();
                    imgPicker1.setImageURI(imgURI);
                }
                break;
            case PICK_IMAGE_TWO:
                if(resultCode == Activity.RESULT_OK){
                    Uri imgURI = data.getData();
                    imgPicker2.setImageURI(imgURI);
                }
                break;
            case PICK_IMAGE_THREE:
                if(resultCode == Activity.RESULT_OK){
                    Uri imgURI = data.getData();
                    imgPicker3.setImageURI(imgURI);
                }
                break;
        }
    }
}