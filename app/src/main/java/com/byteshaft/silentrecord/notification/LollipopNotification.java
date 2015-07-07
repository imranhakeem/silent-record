package com.byteshaft.silentrecord.notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.receivers.LollipopNotificationReceiver;
import com.byteshaft.silentrecord.utils.AppConstants;

public class LollipopNotification {

    private static NotificationManager sNotificationManagerLollipop;

    public static void showNotification() {
        Context context = AppGlobals.getContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Silent Record");
        mBuilder.setContentText("Tap to start recording");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        Intent intent = new Intent(context, LollipopNotificationReceiver.class);
        intent.setAction("com.byteshaft.LOLLIPOP_NOTIFICATION_START");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AppGlobals.getContext(), 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager manager = getNotificationManagerLollipop();
        manager.notify(AppConstants.NOTIFICATION_STOP, mBuilder.build());
    }

    public static void hideNotification() {
        NotificationManager manager = getNotificationManagerLollipop();
        manager.cancel(AppConstants.NOTIFICATION_STOP);
    }


    private static NotificationManager getNotificationManagerLollipop() {
        if (sNotificationManagerLollipop == null) {
            Context context = AppGlobals.getContext();
            sNotificationManagerLollipop =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return sNotificationManagerLollipop;
    }
}

