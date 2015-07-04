package com.byteshaft.silentrecord.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.Environment;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import java.util.Calendar;
import java.io.File;
import java.util.ArrayList;


public class Helpers extends ContextWrapper {

    private AlarmManager mAlarmManager;
    private PendingIntent mPIntent;

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

    private SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void setPicAlarm(boolean picAlarm) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("picAlarm", picAlarm).commit();
    }

    public boolean getPicAlarmStatus() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("picAlarm", false);
    }

    public void setVideoAlarm(boolean videoAlarm) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("videoAlarm", videoAlarm).commit();
    }

    public boolean getVideoAlarmStatus() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("videoAlarm", false);
    }

    public void setTime(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("time_set", value).commit();
    }

    public boolean getTime() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("time_set", false);
    }

    public void setDate(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("date_time", value).commit();
    }

    public boolean getDate() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("date_time", false);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    public void removePreviousAlarm(){
        if (mPIntent != null) {
            mAlarmManager.cancel(mPIntent);
        }
    }

    public void setAlarm(int date, int month,
                                   int year, int hour, int minutes, String operationType) {
        mAlarmManager = getAlarmManager();
        Intent intent = new Intent("com.byteShaft.Alarm");
        intent.putExtra("operationType",operationType);
        mPIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, date);  //1-31
        calendar.set(Calendar.MONTH, month);  //first month is 0!!! January is zero!!!
        calendar.set(Calendar.YEAR, year);//year...

        calendar.set(Calendar.HOUR_OF_DAY, hour);  //HOUR
        calendar.set(Calendar.MINUTE, minutes);       //MIN
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPIntent);
        Log.i(AppGlobals.getLogTag(getClass()), "setting alarm of :" + calendar.getTime());
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
