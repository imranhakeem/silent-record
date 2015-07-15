package com.byteshaft.silentrecord.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.byteshaft.ezflashlight.CameraStateChangeListener;
import com.byteshaft.ezflashlight.Flashlight;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.utils.AppConstants;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
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

public class PictureService extends Service implements CameraStateChangeListener,
        Camera.ShutterCallback, Camera.PictureCallback {

    private Flashlight mFlashlight;
    private static boolean sIsTakingPicture;
    Helpers mHelpers;

    private void setIsTakingPicture(boolean takingPicture) {
        sIsTakingPicture = takingPicture;
    }

    public static boolean isTakingPicture() {
        return sIsTakingPicture;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHelpers = new Helpers(getApplicationContext());
        setIsTakingPicture(true);
        int camera = CameraCharacteristics.getCameraIndex(Helpers.getSelectedCamera());
        mFlashlight = new Flashlight(getApplicationContext());
        mFlashlight.setCameraStateChangedListener(this);
        mFlashlight.setUpCameraPreview(camera);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mFlashlight.releaseAllResources();
        setIsTakingPicture(false);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mFlashlight.releaseAllResources();
        setIsTakingPicture(false);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void takePicture(final Camera camera) {
        camera.setDisplayOrientation(0);
        Camera.Parameters parameters = camera.getParameters();
        setOrientation(parameters);
        parameters.setZoom(Integer.valueOf(Helpers.readZoomSettings()));
        parameters.setPictureSize(
                Values.getPictureDimension()[0], Values.getPictureDimension()[1]);
        parameters.setSceneMode(Values.getPictureSceneMode());
        if (Helpers.isFlashLightEnabled()) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
        camera.startPreview();
        if (Helpers.isAutoFocusEnabled()) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, final Camera camera) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Silencer.silentSystemStream(2000);
                            camera.takePicture(PictureService.this, null, null, PictureService.this);
                            Toast.makeText(getApplicationContext(), "Photo Captured", Toast.LENGTH_SHORT).show();
                        }
                    }, 500);
                }
            });
        } else {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Silencer.silentSystemStream(2000);
                    camera.takePicture(PictureService.this, null, null, PictureService.this);
                    Toast.makeText(getApplicationContext(), "Photo Captured", Toast.LENGTH_SHORT).show();
                }
            }, 500);

        }
    }

    @Override
    public void onCameraInitialized() {

    }

    @Override
    public void onCameraViewSetup(Camera camera, SurfaceHolder surfaceHolder) {
        takePicture(camera);
    }

    @Override
    public void onCameraBusy() {

    }

    private void setOrientation(Camera.Parameters parameters) {
        String selectedCamera = Helpers.getValueFromKey("default_camera");
        if (selectedCamera.equals(AppConstants.CAMERA_FRONT)) {
            parameters.setRotation(270);
        } else if (selectedCamera.equals(AppConstants.CAMERA_REAR)) {
            parameters.setRotation(90);
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
        File directory = AppGlobals.getPicturesDirectory();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Helpers.updateFile(file);
        } else {
            Helpers.refreshMediaScan(directory);
        }
        stopSelf();
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
