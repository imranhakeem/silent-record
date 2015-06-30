package com.byteshaft.silentrecord;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Helpers extends ContextWrapper {

    SharedPreferences mSharedPreferences;

    public Helpers(Context base) {
        super(base);
    }

    String readZoomSettings() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String zoomControl = mSharedPreferences.getString("camera_zoom_control", "20");
        return zoomControl;
    }
}
