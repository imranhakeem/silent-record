package com.byteshaft.silentrecord.utils;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.byteshaft.silentrecord.AppGlobals;

import java.io.File;
import java.io.IOException;

public class CustomMediaRecorder extends MediaRecorder implements MediaRecorder.OnErrorListener {

    private static CustomMediaRecorder sInstance;
    private Handler mHandler;
    private boolean mHandlerSet;
    private String mPath;

    public CustomMediaRecorder() {
        super();
        setOnErrorListener(this);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
    }

    public void start(Camera camera, SurfaceHolder holder, int time) {
        int videoWidth = Values.getVideoDimensions()[0];
        int videoHeight = Values.getVideoDimensions()[1];
        setCamera(camera);
        setAudioSource(MediaRecorder.AudioSource.MIC);
        setVideoSource(MediaRecorder.VideoSource.CAMERA);
        setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        setVideoEncodingBitRate(getBitRateForResolution(videoWidth, videoHeight));
        String selectedCamera = Helpers.getValueFromKey("default_camera");
        if (selectedCamera.equals(AppConstants.CAMERA_FRONT)) {
            setOrientationHint(270);
        } else if (selectedCamera.equals(AppConstants.CAMERA_REAR)) {
            setOrientationHint(90);
        }
        setVideoSize(videoWidth, videoHeight);
        setPreviewDisplay(holder.getSurface());
        try {
            prepare();
            Silencer.silentSystemStream(2000);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UiUpdater uiUpdater = new UiUpdater();
        uiUpdater.updateRecordingTimeInUi();
        Handler handler = getHandler();
        handler.postDelayed(mRecordingStopper, time);
        mHandlerSet = true;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        if (mHandlerSet) {
            getHandler().removeCallbacks(mRecordingStopper);
        }
        reset();
        File directory = AppGlobals.getVideosDirectory();
        File file = new File(mPath);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Helpers.updateFile(file);
        } else {
            Helpers.refreshMediaScan(directory);
        }
    }

    @Override
    public void setOutputFile(String path) throws IllegalStateException {
        super.setOutputFile(path);
        mPath = path;
    }

    private int getBitRateForResolution(int width, int height) {
        // Not perfect but gets use there.
        return (width * height) * 6;
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        switch (i) {
            case MediaRecorder.MEDIA_ERROR_SERVER_DIED:
                sInstance = new CustomMediaRecorder();
        }
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new android.os.Handler();
        }
        return mHandler;
    }

    private Runnable mRecordingStopper = new Runnable() {
        @Override
        public void run() {
            mHandlerSet = false;
            Intent recordIntent = AppGlobals.getRecordServiceIntent();
            AppGlobals.getContext().stopService(recordIntent);
        }
    };
}
