package com.byteshaft.silentrecord;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ScheduleActivity extends Fragment implements View.OnClickListener {

    private boolean mIsRecording;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        Button pictureButton = (Button) view.findViewById(R.id.button_picture);
        Button videoButton = (Button) view.findViewById(R.id.button_record);
        pictureButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        CustomCamera camera  = CustomCamera.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.button_picture:
                camera.takePicture();
                break;
            case R.id.button_record:
                if (mIsRecording) {
                    camera.stopRecording();
                    mIsRecording = false;
                } else {
                    camera.startRecording();
                    mIsRecording = true;
                }
                break;
        }
    }
}
