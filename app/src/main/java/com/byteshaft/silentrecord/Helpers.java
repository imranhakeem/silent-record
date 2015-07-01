package com.byteshaft.silentrecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;


public class Helpers extends ContextWrapper {

    SharedPreferences mSharedPreferences;

    public Helpers(Context base) {
        super(base);
    }

    String readZoomSettings() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mSharedPreferences.getString("camera_zoom_control", "20");
    }

    void showCameraResourceBusyDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Camera busy");
        builder.setMessage("The App needs to read camera capabilities on first run, " +
                "please make sure camera is not in use by any other app");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                activity.finish();
            }
        });
        builder.create();
        builder.show();
    }

    boolean isAppRunningForTheFirstTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("first_run", true);
    }

    void setIsAppRunningForTheFirstTime(boolean firstTime) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putBoolean("first_run", firstTime).apply();
    }

    Camera openCamera() {
        Camera camera;
        try {
            camera = Camera.open();
            return camera;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
