package com.byteshaft.silentrecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

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
        Preference reset = findPreference("reset");
        listPreference = (ListPreference) mPreference;
        listPreference.setEntryValues(getCameraZoomControl());
        listPreference.setValueIndex(0);
        Preference videoPrefs = findPreference("video_resolution");
        listPreference = (ListPreference) videoPrefs;
        listPreference.setEntries(getBackCameraResolution());
        listPreference.setEntryValues(getBackCameraResolution());

        Preference imagePrefs = findPreference("image_resolution");
        listPreference = (ListPreference) imagePrefs;
        listPreference.setEntries(getBackCameraResolution());
        listPreference.setEntryValues(getBackCameraResolution());
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Are you sure?");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences shared = PreferenceManager.
                                getDefaultSharedPreferences(getActivity());
                        shared.edit().putString("camera_zoom_control", "0").apply();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return false;
            }
        });
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
