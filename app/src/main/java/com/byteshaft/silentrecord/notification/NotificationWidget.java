package com.byteshaft.silentrecord.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.NotificationHandler;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.AppConstants;

public class NotificationWidget {

    private static NotificationManager mNotifyManager;
    private static boolean sIsShown;
    private static Context sContext = AppGlobals.getContext();
    private static NotificationCompat.Builder sBuilder;

    public static boolean isShown() {
        return sIsShown;
    }

    public static void show(String formattedTime) {
        Notification notification = get(formattedTime);
        mNotifyManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(AppConstants.NOTIFICATION_ID, notification);
        sIsShown = true;
    }

    public static Notification get(String formattedTime) {
        RemoteViews notifyView = new RemoteViews(sContext.getPackageName(), R.layout.notification);
        PendingIntent pendingIntentPicture = getPendingIntentForNotification("take_picture", 0);
        notifyView.setOnClickPendingIntent(R.id.photo_button_widget, pendingIntentPicture);
        PendingIntent pendingIntentVideo = getPendingIntentForNotification("record_video", 1);
        notifyView.setOnClickPendingIntent(R.id.video_button_widget, pendingIntentVideo);
        if (formattedTime != null) {
            notifyView.setTextViewText(R.id.textview_notification, formattedTime);
        } else {
            notifyView.setTextViewText(R.id.textview_notification, "00:00");
        }

        return getNotificationCharacteristics(notifyView).build();
    }

    public static void hide() {
        if (isShown()) {
            mNotifyManager.cancel(AppConstants.NOTIFICATION_ID);
        }
        sIsShown = false;
    }

    private static PendingIntent getPendingIntentForNotification(String command, int index) {
        Intent intent = new Intent(AppGlobals.getContext(), NotificationHandler.class);
        intent.setAction("perform_notification_button");
        intent.putExtra("do_action", command);
        return PendingIntent.getBroadcast(
                sContext, index, intent, 0);
    }

    private static NotificationCompat.Builder getNotificationCharacteristics(RemoteViews contentView) {
        if (sBuilder == null) {
            sBuilder = new NotificationCompat.Builder(sContext);
        }
        sBuilder.setSmallIcon(R.mipmap.ic_launcher);
        sBuilder.setContent(contentView);
        sBuilder.setAutoCancel(false);
        sBuilder.setOngoing(true);
        return sBuilder;
    }
}
