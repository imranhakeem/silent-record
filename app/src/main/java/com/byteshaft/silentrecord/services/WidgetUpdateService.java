package com.byteshaft.silentrecord.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.byteshaft.silentrecord.widget.WidgetReceiver;

public class WidgetUpdateService extends Service {

    private static WidgetUpdateService sInstance;

    public static boolean isRunning() {
        return sInstance != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sInstance = this;
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenStateListener, screenStateFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateListener);
        sInstance = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mScreenStateListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("SIL", "Screen turned ON");
                WidgetReceiver.updateWidget(context);
            }
        }
    };
}
