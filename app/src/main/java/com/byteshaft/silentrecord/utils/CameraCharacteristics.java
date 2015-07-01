package com.byteshaft.silentrecord.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;

import java.util.List;

public class CameraCharacteristics extends ContextWrapper {

    private SharedPreferences mPreferences;
    private Camera.Parameters mParameters;

    public CameraCharacteristics(Context base, Camera camera) {
        super(base);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mParameters = camera.getParameters();
        writeSupportedVideoResolutions();
        writeSupportedPictureSizes();
        writeSupportedSceneModes();
        writeSupportedZoomLevels();
        camera.release();
    }

    public CameraCharacteristics(Context base) {
        super(base);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private void writeSupportedVideoResolutions() {
        List<Camera.Size> videoSizes = mParameters.getSupportedVideoSizes();
        if (videoSizes == null) {
            videoSizes = mParameters.getSupportedPreviewSizes();
        }
        StringBuilder builder = new StringBuilder();
        for (Camera.Size size : videoSizes) {
            String resolution = size.width + "X" + size.height;
            builder.append(resolution);
            builder.append(",");
        }
        String out = builder.toString();
        mPreferences.edit().putString("video_resolutions", out).apply();
    }

    private void writeSupportedPictureSizes() {
        List<Camera.Size> pictureSizes = mParameters.getSupportedPictureSizes();
        StringBuilder builder = new StringBuilder();
        for (Camera.Size size : pictureSizes) {
            String resolution = size.width + "X" + size.height;
            builder.append(resolution);
            builder.append(",");
        }
        String out = builder.toString();
        mPreferences.edit().putString("picture_sizes", out).apply();
    }

    private void writeSupportedSceneModes() {
        List<String> sceneModes = mParameters.getSupportedSceneModes();
        StringBuilder builder = new StringBuilder();
        for (String mode : sceneModes) {
            builder.append(mode);
            builder.append(",");
        }
        String out = builder.toString();
        mPreferences.edit().putString("scene_modes", out).apply();
    }

    private void writeSupportedZoomLevels() {
        StringBuilder builder = new StringBuilder();
        int zoom  = mParameters.getMaxZoom();
        if (mParameters.isZoomSupported()) {
            for (int i = 1; i <= 3; i++) {
                String res = String.valueOf((zoom / 3) * i);
                builder.append(res);
                builder.append(",");
            }

            String out = builder.toString();
            mPreferences.edit().putString("zoom_levels", out).apply();
        }
    }

    public String[] getSupportedVideoResolutions() {
        String raw = mPreferences.getString("video_resolutions", null);
        String[] resolutionsArray = null;
        if (raw != null) {
            resolutionsArray = raw.split(",");
        }
        return resolutionsArray;
    }

    public String[] getSupportedPictureSizes() {
        String raw = mPreferences.getString("picture_sizes", null);
        String[] resolutionsArray = null;
        if (raw != null) {
            resolutionsArray = raw.split(",");
        }
        return resolutionsArray;
    }

    public String[] getSupportedSceneModes() {
        String raw = mPreferences.getString("scene_modes", null);
        String[] modesArray = null;
        if (raw != null) {
            modesArray = raw.split(",");
        }
        return modesArray;
    }

    public String[] getSupportedZoomLevels() {
        String raw = mPreferences.getString("zoom_levels", null);
        String[] modesArray = null;
        if (raw != null) {
            modesArray = raw.split(",");
        }
        return modesArray;
    }
}
