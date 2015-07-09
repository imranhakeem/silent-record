package com.byteshaft.silentrecord.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.byteshaft.ezflashlight.CameraStateChangeListener;
import com.byteshaft.ezflashlight.Flashlight;
import com.byteshaft.silentrecord.notification.LollipopNotification;
import com.byteshaft.silentrecord.notification.NotificationWidget;
import com.byteshaft.silentrecord.notification.RecordingNotification;
import com.byteshaft.silentrecord.utils.AppConstants;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.CustomMediaRecorder;
import com.byteshaft.silentrecord.utils.Helpers;
import com.byteshaft.silentrecord.utils.Silencer;
import com.byteshaft.silentrecord.utils.Values;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class RecordService extends Service implements CameraStateChangeListener {

    private CustomMediaRecorder mMediaRecorder;
    private Flashlight mFlashlight;
    private static boolean sIsRecording;
    private static RecordService sInstance;
    private Helpers mHelpers;

    private void setIsRecording(boolean recording) {
        sIsRecording = recording;
    }

    public static boolean isRecording() {
        return sIsRecording;
    }

    private void setInstance(RecordService service) {
        sInstance = service;
    }

    public static RecordService getInstance() {
        return sInstance;
    }

    public static boolean isRunning() {
        return sInstance != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHelpers = new Helpers(getApplicationContext());
        setInstance(this);
        setIsRecording(true);
        int camera = CameraCharacteristics.getCameraIndex(Helpers.getSelectedCamera());
        mMediaRecorder = new CustomMediaRecorder();
        mFlashlight = new Flashlight(getApplicationContext());
        mFlashlight.setCameraStateChangedListener(this);
        mFlashlight.setUpCameraPreview(camera);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isRecording()) {
            stopRecording();
        }
        setIsRecording(false);
        System.out.println("Service stopped");
        setInstance(null);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (isRecording()) {
            stopRecording();
        }
        setIsRecording(false);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startRecording(Camera camera, SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();
        setOrientation(parameters);
        parameters.setSceneMode(Values.getVideoSceneMode());
        parameters.setZoom(Integer.valueOf(Helpers.readZoomSettings()));
        if (mHelpers.isFlashLightEnabled()) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
        camera.unlock();
        String path = Environment.getExternalStorageDirectory() + "/" + "SpyVideos" + "/"+ getTimeStamp() + ".mp4";
        mMediaRecorder = new CustomMediaRecorder();
        mMediaRecorder.setOutputFile(path);
        int time = (int) TimeUnit.MINUTES.toMillis(Integer.valueOf(Helpers.readMaxVideoValue()));
        mMediaRecorder.start(camera, holder, time);
        Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
        if (Helpers.isWidgetSwitchOn()) {
            Intent service = new Intent(getApplicationContext(), NotificationService.class);
            stopService(service);
            updateNotification(null);
        } else {
            startForeground(
                    AppConstants.NOTIFICATION_ID,
                    RecordingNotification.getNotification().build());
        }
//        updateNotification(null);
    }

    private String getTimeStamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyyMMddhhmmss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private void setOrientation(Camera.Parameters parameters) {
        String selectedCamera = Helpers.getValueFromKey("default_camera");
        if (selectedCamera.equals(AppConstants.CAMERA_FRONT)) {
            parameters.setRotation(270);
        } else if (selectedCamera.equals(AppConstants.CAMERA_REAR)) {
            parameters.setRotation(90);
        }
    }

    private void stopRecording() {
        Silencer.silentSystemStream(2000);
        mMediaRecorder.stop();
        mFlashlight.releaseAllResources();
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
//        if (Helpers.isWidgetSwitchOn()) {
//            LollipopNotification.showNotification();
//        }
    }

    public void updateNotification(String time) {
        startForeground(
                AppConstants.NOTIFICATION_ID,
                NotificationWidget.get(time));
    }

    @Override
    public void onCameraInitialized() {

    }

    @Override
    public void onCameraViewSetup(Camera camera, SurfaceHolder surfaceHolder) {
        startRecording(camera, surfaceHolder);
    }

    @Override
    public void onCameraBusy() {

    }
}
