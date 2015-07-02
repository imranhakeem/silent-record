package com.byteshaft.silentrecord;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;


public class Helpers extends ContextWrapper {

    SharedPreferences mSharedPreferences;

    public Helpers(Context base) {
        super(base);
    }

    String readZoomSettings() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String zoomControl = mSharedPreferences.getString("camera_zoom_control", "0");
        return zoomControl;
    }

    void spyVideosDirectory() {
        File recordingsDirectory = new File(Environment.getExternalStorageDirectory() + "/" + "SpyVideos");
        if (!recordingsDirectory.exists()) {
            recordingsDirectory.mkdir();
        }
    }

    void spyPicturesDirectory() {
        File recordingsDirectory = new File(Environment.getExternalStorageDirectory() + "/" + "SpyPics");
        if (!recordingsDirectory.exists()) {
            recordingsDirectory.mkdir();
        }
    }

    ArrayList<String> getNameFromFolder() {
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
}
