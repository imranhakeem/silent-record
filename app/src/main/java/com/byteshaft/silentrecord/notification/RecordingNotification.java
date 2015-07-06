package com.byteshaft.silentrecord.notification;



import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.receivers.NotificationReceiver;
import com.byteshaft.silentrecord.utils.AppConstants;

public class RecordingNotification {

    private static NotificationManager sNotificationManager;

    public static void show() {
        Context context = AppGlobals.getContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Silent Record");
        builder.setContentText("Tap to stop recording");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("com.byteshaft.NOTIFICATION_STOP");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AppGlobals.getContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = getNotificationManager();
        manager.notify(AppConstants.NOTIFICATION_STOP, builder.build());
    }

    public static void hide() {
        NotificationManager manager = getNotificationManager();
        manager.cancel(AppConstants.NOTIFICATION_STOP);
    }

    private static NotificationManager getNotificationManager() {
        if (sNotificationManager == null) {
            Context context = AppGlobals.getContext();
            sNotificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return sNotificationManager;
    }
}
