package com.byteshaft.silentrecord.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import com.byteshaft.silentrecord.AppGlobals;

import java.util.List;

@SuppressWarnings("deprecation")
public class CameraCharacteristics {

    private static SharedPreferences mPreferences = AppGlobals.getPreferenceManager();

    public CameraCharacteristics(Activity activity) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera rearCamera = Helpers.openCamera(i);
                if (rearCamera == null) {
                    Helpers.showCameraResourceBusyDialog(activity);
                } else {
                    Camera.Parameters parameters = rearCamera.getParameters();
                    setDeviceHasRearFacingCamera(true);
                    writeCameraIndex(AppConstants.CAMERA_REAR, i);
                    writeAllCameraCharacteristics(AppConstants.CAMERA_REAR, parameters);
                    rearCamera.release();
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Camera frontCamera = Helpers.openCamera(i);
                if (frontCamera == null) {
                    Helpers.showCameraResourceBusyDialog(activity);
                } else {
                    Camera.Parameters parameters = frontCamera.getParameters();
                    setDeviceHasFrontFacingCamera(true);
                    writeCameraIndex(AppConstants.CAMERA_FRONT, i);
                    writeAllCameraCharacteristics(AppConstants.CAMERA_FRONT, parameters);
                    frontCamera.release();
                }
            }
            writeNumberOfCameras();
        }
    }

    private void writeAllCameraCharacteristics(String cameraFace, Camera.Parameters parameters) {
        writeSupportedVideoResolutions(cameraFace, parameters);
        writeSupportedPictureSizes(cameraFace, parameters);
        writeSupportedSceneModes(cameraFace, parameters);
        writeSupportedZoomLevels(cameraFace, parameters);
    }

    private void setDeviceHasFrontFacingCamera(boolean hasFrontCamera) {
        mPreferences.edit().putBoolean("has_front_camera", hasFrontCamera).apply();
    }

    private void setDeviceHasRearFacingCamera(boolean hasRearCamera) {
        mPreferences.edit().putBoolean("has_rear_camera", hasRearCamera).apply();
    }

    public static boolean hasFrontCamera() {
        return mPreferences.getBoolean("has_front_camera", false);
    }

    public static boolean hasRearCamera() {
        return mPreferences.getBoolean("has_rear_camera", false);
    }

    private void writeCameraIndex(String cameraFace, int index) {
        mPreferences.edit().putInt(cameraFace, index).apply();
    }

    public static int getCameraIndex(String cameraFace) {
        return mPreferences.getInt(cameraFace, -1);
    }

    private void writeNumberOfCameras() {
        mPreferences.edit().putInt("number_of_cameras", Camera.getNumberOfCameras()).apply();
    }

    private void writeSupportedVideoResolutions(String cameraFacing, Camera.Parameters parameters) {
        List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
        if (videoSizes == null) {
            videoSizes = parameters.getSupportedPreviewSizes();
        }
        StringBuilder builder = new StringBuilder();
        for (Camera.Size size : videoSizes) {
            String resolution = size.width + "X" + size.height;
            builder.append(resolution);
            builder.append(",");
        }
        String key = String.format("video_resolutions_%s", cameraFacing);
        String out = builder.toString();
        mPreferences.edit().putString(key, out).apply();
    }

    private void writeSupportedPictureSizes(String cameraFacing, Camera.Parameters parameters) {
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        StringBuilder builder = new StringBuilder();
        for (Camera.Size size : pictureSizes) {
            String resolution = size.width + "X" + size.height;
            builder.append(resolution);
            builder.append(",");
        }
        String key = String.format("picture_sizes_%s", cameraFacing);
        String out = builder.toString();
        mPreferences.edit().putString(key, out).apply();
    }

    private void writeSupportedSceneModes(String cameraFacing, Camera.Parameters parameters) {
        String key = String.format("scene_modes_%s", cameraFacing);
        List<String> sceneModes = parameters.getSupportedSceneModes();
        if (sceneModes == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String mode : sceneModes) {
            builder.append(mode);
            builder.append(",");
        }
        String out = builder.toString();
        mPreferences.edit().putString(key, out).apply();
    }

    private void writeSupportedZoomLevels(String cameraFacing, Camera.Parameters parameters) {
        StringBuilder builder = new StringBuilder();
        int zoom  = parameters.getMaxZoom();
        if (parameters.isZoomSupported()) {
            builder.append("0,");
            for (int i = 1; i <= 3; i++) {
                String res = String.valueOf((zoom / 3) * i);
                builder.append(res);
                builder.append(",");
            }
            String key = String.format("zoom_levels_%s", cameraFacing);
            String out = builder.toString();
            mPreferences.edit().putString(key, out).apply();
        }
    }

    public static String[] getSupportedVideoResolutions(String cameraFacing) {
        String key = String.format("video_resolutions_%s", cameraFacing);
        String raw = mPreferences.getString(key, null);
        String[] resolutionsArray = null;
        if (raw != null) {
            resolutionsArray = raw.split(",");
        }
        return resolutionsArray;
    }

    public static int getNumberOfCameras() {
        return mPreferences.getInt("number_of_cameras", 1);
    }

    public static String[] getSupportedPictureSizes(String cameraFacing) {
        String key = String.format("picture_sizes_%s", cameraFacing);
        String raw = mPreferences.getString(key, null);
        String[] resolutionsArray = null;
        if (raw != null) {
            resolutionsArray = raw.split(",");
        }
        return resolutionsArray;
    }

    public static String[] getSupportedSceneModes(String cameraFacing) {
        String key = String.format("scene_modes_%s", cameraFacing);
        String raw = mPreferences.getString(key, null);
        String[] modesArray = null;
        if (raw != null) {
            modesArray = raw.split(",");
        }
        return modesArray;
    }

    public static String[] getSupportedZoomLevels(String cameraFacing) {
        String key = String.format("zoom_levels_%s", cameraFacing);
        String raw = mPreferences.getString(key, null);
        String[] modesArray = null;
        if (raw != null) {
            modesArray = raw.split(",");
        }
        return modesArray;
    }
}
