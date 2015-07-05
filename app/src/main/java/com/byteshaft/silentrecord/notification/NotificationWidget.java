package com.byteshaft.silentrecord.notification;

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

    public static boolean isShown() {
        return sIsShown;
    }

    public static void show(String formattedTime) {
        RemoteViews notifyView = new RemoteViews(sContext.getPackageName(), R.layout.notification);
        PendingIntent pendingIntentPicture = getPendingIntentForNotification("take_picture");
        notifyView.setOnClickPendingIntent(R.id.photo_button_widget, pendingIntentPicture);
        PendingIntent pendingIntentVideo = getPendingIntentForNotification("record_video");
        notifyView.setOnClickPendingIntent(R.id.video_button_widget, pendingIntentVideo);
        if (formattedTime != null) {
            notifyView.setTextViewText(R.id.textview_notification, formattedTime);
        } else {
            notifyView.setTextViewText(R.id.textview_notification, "00:00");
        }
        NotificationCompat.Builder builder = getNotificationCharacteristics(notifyView);
        mNotifyManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(AppConstants.NOTIFICATION_ID, builder.build());
        sIsShown = true;
    }

    public static void hide() {
        mNotifyManager.cancel(AppConstants.NOTIFICATION_ID);
        sIsShown = false;
    }

    private static PendingIntent getPendingIntentForNotification(String command) {
        Intent intent = new Intent(AppGlobals.getContext(), NotificationHandler.class);
        intent.setAction("perform_notification_button");
        intent.putExtra("do_action", command);
        return PendingIntent.getBroadcast(
                sContext, 0, intent, 0);
    }

    private static NotificationCompat.Builder getNotificationCharacteristics(RemoteViews contentView) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(sContext);
        builder.setSmallIcon(R.drawable.notification_template_icon_bg);
        builder.setContent(contentView);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        return builder;
    }
}
