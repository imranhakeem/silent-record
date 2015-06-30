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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        mPreference = findPreference("camera_zoom_control");
        ListPreference listPreference = (ListPreference) mPreference;
        listPreference.setEntryValues(getCameraZoomControl());
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
}
