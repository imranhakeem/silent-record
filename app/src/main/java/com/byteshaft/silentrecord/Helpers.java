package com.byteshaft.silentrecord;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Helpers extends ContextWrapper {

    SharedPreferences mSharedPreferences;
    private AlarmManager mAlarmManager;
    private PendingIntent mPIntent;
    private boolean mTimeSet;
    private boolean mDateSet;

    public Helpers(Context base) {
        super(base);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    String readZoomSettings() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String zoomControl = mSharedPreferences.getString("camera_zoom_control", "20");
        return zoomControl;
    }

    void setAlarmForVideoRecording(int date, int month,
                                   int year, int hour, int minutes) {
        mAlarmManager = getAlarmManager();
        Intent intent = new Intent("com.byteShaft.videoRecordingAlarm");
        mPIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE,date);  //1-31
        calendar.set(Calendar.MONTH, month);  //first month is 0!!! January is zero!!!
        calendar.set(Calendar.YEAR,year);//year...

        calendar.set(Calendar.HOUR_OF_DAY, hour);  //HOUR
        calendar.set(Calendar.MINUTE, minutes);       //MIN
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPIntent);
        Log.i("Video_Alarm,", "setting alarm of :" + calendar.getTime());
    }

    private SharedPreferences getPreferenceManager() {
       return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    void picAlarmset(boolean picAlarm) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("picAlarm", picAlarm).commit();
    }

    boolean getPicAlarmStatus() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("picAlarm", false);
    }

    void videoAlarmset(boolean picAlarm) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("picAlarm", picAlarm).commit();
    }

    boolean getVideoAlarmStatus() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("picAlarm", false);
    }

    void setTime(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("time_set", value).commit();
    }

    boolean getTime() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("time_set", false);
    }

    void setDate(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("date_time", value).commit();
    }

    boolean getDate() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean("date_time", false);
    }


}
