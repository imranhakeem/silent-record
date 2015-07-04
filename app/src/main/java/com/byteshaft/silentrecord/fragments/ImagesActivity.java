package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.CustomCamera;
import com.byteshaft.silentrecord.R;


public class ImagesActivity extends Fragment  implements View.OnClickListener{

    View view;
    Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.images, container, false);
        mButton = (Button) view.findViewById(R.id.buttonPicture);
        mButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        CustomCamera camera  = CustomCamera.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.buttonPicture:
                if (!FlashlightGlobals.isResourceOccupied()) {
                    camera.takePicture();
                }
                break;
        }

    }
}
