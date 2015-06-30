package com.byteshaft.silentrecord;

import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;


public class SettingFragment extends PreferenceFragment {

    private Preference mPreference;
    private Camera mCamera;
    ListPreference listPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        mPreference = findPreference("camera_zoom_control");
        listPreference = (ListPreference) mPreference;
        listPreference.setEntryValues(getCameraZoomControl());
        Preference videoPrefs = findPreference("video_resolution");
        listPreference = (ListPreference) videoPrefs;
        listPreference.setEntries(getBackCameraResolution());
        listPreference.setEntryValues(getBackCameraResolution());

        Preference imagePrefs = findPreference("image_resolution");
        listPreference = (ListPreference) imagePrefs;
        listPreference.setEntries(getBackCameraResolution());
        listPreference.setEntryValues(getBackCameraResolution());
    }

    String[] getCameraZoomControl() {
        ArrayList<String> list = new ArrayList<>();
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        int zoom  = parameters.getMaxZoom();
        if (parameters.isZoomSupported()) {
            for (int i = 1; i <= 3; i++) {
                String res = String.valueOf((zoom / 3) * i);
                list.add(res);
                System.out.println(res);
            }
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return list.toArray(new String[list.size()]);

    }

    String[] getBackCameraResolution() {
        int videoHeight;
        int videoWidth;
        ArrayList<String> resolutionList = new ArrayList<>();
        int noOfCameras = Camera.getNumberOfCameras();
        for (int i = 0;i < noOfCameras;i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera camera = Camera.open(i);
                Camera.Parameters cameraParams = camera.getParameters();
                for (Camera.Size size : cameraParams.getSupportedPictureSizes()) {
                    videoHeight = size.width;
                    videoWidth  = size.height;
                    String height = String.valueOf(videoHeight);
                    String width = String.valueOf(videoWidth);
                    resolutionList.add(height +" X " +width);
                }
                camera.release();
            }
        }
        String names[]=resolutionList.toArray(new String[resolutionList.size()]);
        return  names;
    }
}
