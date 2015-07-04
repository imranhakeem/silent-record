package com.byteshaft.silentrecord.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;

import java.io.File;
import java.util.ArrayList;


public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    public String readZoomSettings() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getString("camera_zoom_control", "0");
    }

    public void showCameraResourceBusyDialog(final Activity activity) {
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

    public boolean isAppRunningForTheFirstTime() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getBoolean("first_run", true);
    }

    public void setIsAppRunningForTheFirstTime(boolean firstTime) {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        preferences.edit().putBoolean("first_run", firstTime).apply();
    }

    public Camera openCamera() {
        try {
            return Camera.open();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public void spyVideosDirectory() {
        File recordingsDirectory = new File(Environment.getExternalStorageDirectory() + "/" + "SpyVideos");
        if (!recordingsDirectory.exists()) {
            recordingsDirectory.mkdir();
        }
    }

    public void spyPicturesDirectory() {
        File recordingsDirectory = new File(Environment.getExternalStorageDirectory() + "/" + "SpyPics");
        if (!recordingsDirectory.exists()) {
            recordingsDirectory.mkdir();
        }
    }

    public ArrayList<String> getNameFromFolder() {
        ArrayList<String> arrayList = new ArrayList<>();
        File filePath = Environment.getExternalStorageDirectory();
        File fileDirectory = new File(filePath, "SpyVideos");
        for (File file : fileDirectory.listFiles()) {
            if (file.isFile()) {
                String name = file.getName();
                arrayList.add(name);
            }
        }
        return arrayList;
    }

    public void hideVideoFiles() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyVideos";
        File videosDirectory = new File(rootPath);
        for (File file : videosDirectory.listFiles()) {
            String fileName = file.getName();
            if (!fileName.startsWith(".")) {
                File hiddenFile = new File(videosDirectory, "." + fileName);
                file.renameTo(hiddenFile);
            }
        }
    }

    public void unhideVideoFiles() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyVideos";
        File videosDirectory = new File(rootPath);
        for (File file : videosDirectory.listFiles()) {
            String fileName = file.getName();
            if (fileName.startsWith(".")) {
                File unhidden = new File(videosDirectory, fileName.substring(1));
                file.renameTo(unhidden);
            }
        }
    }

    public void hideImageFiles() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyPics";
        File videosDirectory = new File(rootPath);
        for (File file : videosDirectory.listFiles()) {
            String fileName = file.getName();
            if (!fileName.startsWith(".")) {
                File hiddenFile = new File(videosDirectory, "." + fileName);
                file.renameTo(hiddenFile);
            }
        }
    }

    public void unhideImageFiles() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyPics";
        File videosDirectory = new File(rootPath);
        for (File file : videosDirectory.listFiles()) {
            String fileName = file.getName();
            if (fileName.startsWith(".")) {
                File unhidden = new File(videosDirectory, fileName.substring(1));
                file.renameTo(unhidden);
            }
        }
    }

    public boolean isImageHiderOn() {
        SharedPreferences sharedPreferences = AppGlobals.getPreferenceManager();
        return sharedPreferences.getBoolean("image_visibility", false);
    }

    public boolean isVideoHiderOn() {
        SharedPreferences sharedPreferences = AppGlobals.getPreferenceManager();
        return sharedPreferences.getBoolean("video_visibility", false);
    }
}
