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


public class VideosActivity extends Fragment implements View.OnClickListener {

    Button mButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.videos,container,false);
        mButton = (Button) view.findViewById(R.id.buttonRecording);
        mButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        CustomCamera camera  = CustomCamera.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.buttonRecording:
                if (CustomCamera.isRecording()) {
                    camera.stopRecording();
                    mButton.setText("Start recording");
                } else if (!CustomCamera.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    camera.startRecording();
                    mButton.setText("Stop recording");
                }
                break;
        }

    }
}
