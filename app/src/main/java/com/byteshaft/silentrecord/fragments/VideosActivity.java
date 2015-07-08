package com.byteshaft.silentrecord.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;


public class VideosActivity extends Fragment implements View.OnClickListener {

    private static VideosActivity sInstance;
    public ImageButton mVideoButtonVideoActivity;
    public TextView mLabelRecordTime;

    public static VideosActivity getInstance() {
        return sInstance;
    }

    public static boolean isRunning() {
        return sInstance != null;
    }

    private static void setInstance(VideosActivity videosActivity) {
        sInstance = videosActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setInstance(this);
        View view = inflater.inflate(R.layout.videos, container, false);
        mVideoButtonVideoActivity = (ImageButton) view.findViewById(R.id.buttonRecording);
        mLabelRecordTime = (TextView) view.findViewById(R.id.video_record_time);
        mVideoButtonVideoActivity.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        Context context = getActivity().getApplicationContext();
        Intent serviceIntent = AppGlobals.getRecordServiceIntent();
        switch (view.getId()) {
            case R.id.buttonRecording:
                if (RecordService.isRecording()) {
                    context.stopService(serviceIntent);
                    mVideoButtonVideoActivity.setImageResource(R.drawable.widget_effect_two);
                } else if (!PictureService.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    context.startService(serviceIntent);
                    mVideoButtonVideoActivity.setImageResource(R.drawable.camcoder_rec_two);
                }
                break;
        }
    }
}
