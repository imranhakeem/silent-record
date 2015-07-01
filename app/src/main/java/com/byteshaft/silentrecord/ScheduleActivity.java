package com.byteshaft.silentrecord;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.byteshaft.ezflashlight.FlashlightGlobals;

public class ScheduleActivity extends Fragment implements View.OnClickListener {

    private boolean mIsRecording;
    private Button mVideoButton;
    private Button mPictureButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        mPictureButton = (Button) view.findViewById(R.id.button_picture);
        mVideoButton = (Button) view.findViewById(R.id.button_record);
        mPictureButton.setOnClickListener(this);
        mVideoButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        CustomCamera camera  = CustomCamera.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.button_picture:
                if (!FlashlightGlobals.isResourceOccupied()) {
                    camera.takePicture();
                }
                break;
            case R.id.button_record:
                if (mIsRecording) {
                    camera.stopRecording();
                    mVideoButton.setText("Start recording");
                    mIsRecording = false;
                } else {
                    camera.startRecording();
                    mVideoButton.setText("Stop recording");
                    mIsRecording = true;
                }
                break;
        }
    }
}
