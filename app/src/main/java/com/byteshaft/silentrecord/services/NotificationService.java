package com.byteshaft.silentrecord.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.byteshaft.silentrecord.notification.NotificationWidget;
import com.byteshaft.silentrecord.utils.AppConstants;

public class NotificationService extends Service {

    public static NotificationService sInstance;

    private void setInstance(NotificationService service) {
        sInstance = service;
    }

    public static boolean isRunning() {
        return sInstance != null;
    }

    public static NotificationService getInstance() {
        return sInstance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setInstance(this);
        updateNotification(null);
        return START_STICKY;
    }

    public void updateNotification(String time) {
        startForeground(
                AppConstants.NOTIFICATION_ID,
                NotificationWidget.get(time));
    }

    @Override
    public void onDestroy() {
        setInstance(null);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
