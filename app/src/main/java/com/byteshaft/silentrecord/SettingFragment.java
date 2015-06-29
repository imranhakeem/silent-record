package com.byteshaft.silentrecord;

import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;


public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        Preference test = findPreference("video_resolution");
        ListPreference lp = (ListPreference) test;
        lp.setEntries(getBackCameraResolution());
        lp.setEntryValues(getBackCameraResolution());
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
