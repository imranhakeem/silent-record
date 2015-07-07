package com.byteshaft.silentrecord.utils;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import com.byteshaft.silentrecord.CustomCamera;
import com.byteshaft.silentrecord.notification.NotificationWidget;
import com.byteshaft.silentrecord.notification.RecordingNotification;

import java.io.IOException;

public class CustomMediaRecorder extends MediaRecorder {

    private static CustomMediaRecorder sInstance;

    private CustomMediaRecorder() {
        super();
    }

    public static CustomMediaRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new CustomMediaRecorder();
        }
        return sInstance;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        RecordingNotification.show();
    }

    public void start(Camera camera, SurfaceHolder holder, int time) {
        int videoWidth = Values.getVideoDimensions()[0];
        int videoHeight = Values.getVideoDimensions()[1];
        setCamera(camera);
        setAudioSource(MediaRecorder.AudioSource.MIC);
        setVideoSource(MediaRecorder.VideoSource.CAMERA);
        setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        setVideoEncodingBitRate(getBitRateForResolution(videoWidth, videoHeight));
        setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        setOrientationHint(90);
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
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CustomCamera.isRecording()) {
                    stop();
                }
            }
        }, time);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        reset();
        RecordingNotification.hide();
//        NotificationWidget.hide();
    }

    private int getBitRateForResolution(int width, int height) {
        // Not perfect but gets use there.
        return (width * height) * 8;
    }
}
