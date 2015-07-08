package com.byteshaft.silentrecord;

import android.content.Context;
import android.content.ContextWrapper;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.byteshaft.ezflashlight.CameraStateChangeListener;
import com.byteshaft.ezflashlight.Flashlight;
import com.byteshaft.silentrecord.notification.LollipopNotification;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.CustomMediaRecorder;
import com.byteshaft.silentrecord.utils.Helpers;
import com.byteshaft.silentrecord.utils.Silencer;
import com.byteshaft.silentrecord.utils.Values;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CustomCamera extends ContextWrapper implements CameraStateChangeListener,
        Camera.ShutterCallback, Camera.PictureCallback {

    private static CustomCamera sCustomCamera;
    private Flashlight mFlashlight;
    private int mCameraRequest;
    private CustomMediaRecorder mMediaRecorder;
    private Helpers mHelpers;
    private static boolean sIsRecording;
    private static boolean sIsTakingPicture;

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

    public static boolean isRecording() {
        return sIsRecording;
    }

    public static boolean isTakingPicture() {
        return sIsTakingPicture;
    }

    public void takePicture() {
        mCameraRequest = CameraRequest.TAKE_PICTURE;
        sIsTakingPicture = true;
        int camera = CameraCharacteristics.getCameraIndex(Helpers.getSelectedCamera());
        mFlashlight.setUpCameraPreview(camera);
    }

    private void takePicture(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(90);
        parameters.setZoom(Integer.valueOf(mHelpers.readZoomSettings()));
        parameters.setPictureSize(
                Values.getPictureDimension()[0], Values.getPictureDimension()[1]);
        parameters.setSceneMode(Values.getPictureSceneMode());
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                Silencer.silentSystemStream(2000);
                camera.takePicture(CustomCamera.this, null, null, CustomCamera.this);
                Toast.makeText(getApplicationContext(), "Photo Captured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startRecording() {
        mCameraRequest = CameraRequest.START_RECORDING;
        sIsRecording = true;
        int camera = CameraCharacteristics.getCameraIndex(Helpers.getSelectedCamera());
        mFlashlight.setUpCameraPreview(camera);
        if (CustomCamera.isRecording()) {
            LollipopNotification.hideNotification();
        }
    }

    private void startRecording(Camera camera, SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setSceneMode(Values.getVideoSceneMode());
        parameters.setZoom(Integer.valueOf(mHelpers.readZoomSettings()));
        camera.setParameters(parameters);
        camera.unlock();
        String path = Environment.getExternalStorageDirectory() + "/" + "SpyVideos" + "/"+ getTimeStamp() + ".3gp";
        mMediaRecorder = CustomMediaRecorder.getInstance();
        mMediaRecorder.setOutputFile(path);
        int time = (int) TimeUnit.MINUTES.toMillis(Integer.valueOf(mHelpers.readMaxVideoValue()));
        mMediaRecorder.start(camera, holder, time);
        Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
    }

    public void stopRecording() {
        Silencer.silentSystemStream(2000);
        mMediaRecorder.stop();
        mFlashlight.releaseAllResources();
        sIsRecording = false;
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
        if (Helpers.isWidgetSwitchOn()) {
            LollipopNotification.showNotification();
        }
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
        switch (mCameraRequest) {
            case CameraRequest.START_RECORDING:
                sIsRecording = false;
                break;
            case CameraRequest.TAKE_PICTURE:
                sIsTakingPicture = false;
                break;
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        String out = Environment.getExternalStorageDirectory() + "/" + "SpyPics" + "/" + getTimeStamp() + ".jpg";
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
        sIsTakingPicture = false;
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
}
