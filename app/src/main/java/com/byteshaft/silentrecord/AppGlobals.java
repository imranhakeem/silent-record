package com.byteshaft.silentrecord;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppGlobals extends Application {

    private static SharedPreferences sPreferences;
    private static Context sContext;
    static String LOG_TAG = "silent_recorder";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static SharedPreferences getPreferenceManager() {
        return sPreferences;
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getLogTag(Class aClass) {
        return LOG_TAG + "/" + aClass.getSimpleName();
    }
}
