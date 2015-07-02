package com.byteshaft.silentrecord.utils;

import android.content.Context;
import android.media.AudioManager;

import com.byteshaft.silentrecord.AppGlobals;

public class Silencer {

    public static synchronized void silentSystemStream(int millis) {
        Context context = AppGlobals.getContext();
        final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int previousVolume = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        manager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                manager.setStreamVolume(AudioManager.STREAM_SYSTEM, previousVolume, 0);
            }
        }, millis);
    }
}
