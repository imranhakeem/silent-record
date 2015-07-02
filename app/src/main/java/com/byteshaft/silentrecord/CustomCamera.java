package com.byteshaft.silentrecord;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceHolder;

import com.byteshaft.ezflashlight.CameraStateChangeListener;
import com.byteshaft.ezflashlight.Flashlight;
import com.byteshaft.silentrecord.utils.Helpers;
import com.byteshaft.silentrecord.utils.Silencer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CustomCamera extends ContextWrapper implements CameraStateChangeListener,
        Camera.ShutterCallback, Camera.PictureCallback {

    private static CustomCamera sCustomCamera;
    private Flashlight mFlashlight;
    private int mCameraRequest;
    private MediaRecorder mMediaRecorder;
    private Helpers mHelpers;

    private static class CameraRequest {
        static final int START_RECORDING = 1;
        static final int TAKE_PICTURE = 2;
    }

    private CustomCamera(Context base) {
        super(base);
        mFlashlight = new Flashlight(getApplicationContext());
        mFlashlight.setCameraStateChangedListener(this);
        mHelpers = new Helpers(base);
    }

    public static CustomCamera getInstance(Context context) {
        if (sCustomCamera == null) {
            sCustomCamera = new CustomCamera(context);
        }
        return sCustomCamera;
    }

    public void takePicture() {
        mCameraRequest = CameraRequest.TAKE_PICTURE;
        mFlashlight.setupCameraPreview();
    }

    private void takePicture(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(90);
        parameters.setZoom(Integer.valueOf(mHelpers.readZoomSettings()));
        parameters.setPictureSize(getPictureDimension()[0], getPictureDimension()[1]);
        parameters.setSceneMode(getPictureSceneMode());
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                Silencer.silentSystemStream(2000);
                camera.takePicture(CustomCamera.this, null, null, CustomCamera.this);
            }
        });
    }

    public void startRecording() {
        mCameraRequest = CameraRequest.START_RECORDING;
        mFlashlight.setupCameraPreview();
    }

    private void startRecording(Camera camera, SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setSceneMode(getVideoSceneMode());
        camera.setParameters(parameters);
        camera.unlock();
        String path = Environment.getExternalStorageDirectory() + File.separator + getTimeStamp() + "test.3gp";
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setVideoSize(getVideoDimensions()[0], getVideoDimensions()[1]);
        mMediaRecorder.setPreviewDisplay(holder.getSurface());
        mMediaRecorder.setOutputFile(path);
        try {
            mMediaRecorder.prepare();
            Silencer.silentSystemStream(2000);
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        Silencer.silentSystemStream(2000);
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mFlashlight.releaseAllResources();
        mMediaRecorder = null;
    }

    @Override
    public void onCameraInitialized() {

    }

    @Override
    public void onCameraViewSetup(Camera camera, SurfaceHolder holder) {
        switch (mCameraRequest) {
            case CameraRequest.TAKE_PICTURE:
                takePicture(camera);
                break;
            case CameraRequest.START_RECORDING:
                startRecording(camera, holder);
                break;
        }
    }

    @Override
    public void onCameraBusy() {

    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        String out = Environment.getExternalStorageDirectory() + File.separator + getTimeStamp() + "test.jpg";
        File file = new File(out);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFlashlight.releaseAllResources();
    }

    @Override
    public void onShutter() {

    }

    private String getTimeStamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyyMMddhhmmss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private int[] getVideoDimensions() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        String out = preferences.getString("video_resolution", null);
        if (out == null) {
            return new int[] {640, 480};
        }

        String[] dimensions = out.split("X");
        return new int[] {Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1])};
    }

    private int[] getPictureDimension() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        String out = preferences.getString("image_resolution", null);
        if (out == null) {
            return new int[] {640, 480};
        }

        String[] dimensions = out.split("X");
        return new int[] {Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1])};
    }

    private String getPictureSceneMode() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getString("picture_scene_mode", "auto");
    }

    private String getVideoSceneMode() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getString("video_scene_mode", "auto");
    }
}
