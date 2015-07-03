package com.byteshaft.silentrecord.utils;

import android.content.SharedPreferences;

import com.byteshaft.silentrecord.AppGlobals;

public class Values {

    public static int[] getVideoDimensions() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        String out = preferences.getString("video_resolution", null);
        if (out == null) {
            return new int[] {640, 480};
        }

        String[] dimensions = out.split("X");
        return new int[] {Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1])};
    }

    public static int[] getPictureDimension() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        String out = preferences.getString("image_resolution", null);
        if (out == null) {
            return new int[] {640, 480};
        }

        String[] dimensions = out.split("X");
        return new int[] {Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1])};
    }

    public static String getPictureSceneMode() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getString("picture_scene_mode", "auto");
    }

    public static String getVideoSceneMode() {
        SharedPreferences preferences = AppGlobals.getPreferenceManager();
        return preferences.getString("video_scene_mode", "auto");
    }
}
