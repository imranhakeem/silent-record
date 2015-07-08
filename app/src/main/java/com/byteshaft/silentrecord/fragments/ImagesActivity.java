package com.byteshaft.silentrecord.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;


public class ImagesActivity extends Fragment implements View.OnClickListener{

    View view;
    ImageButton mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.images, container, false);
        mButton = (ImageButton) view.findViewById(R.id.buttonPicture);
        mButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent pictureService = AppGlobals.getPictureServiceIntent();
        Context context = AppGlobals.getContext();
        switch (view.getId()) {
            case R.id.buttonPicture:
                if (!FlashlightGlobals.isResourceOccupied()) {
                    context.startService(pictureService);
                }
                break;
        }

    }

}
