package com.byteshaft.silentrecord.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.receivers.NotificationReceiver;

public class RecordingNotification {

    public static NotificationCompat.Builder getNotification() {
        Context context = AppGlobals.getContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Recording Video");
        builder.setContentText("Tap to stop recording");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("com.byteshaft.NOTIFICATION_STOP");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AppGlobals.getContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder;
    }
}
