package com.byteshaft.silentrecord.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;
import com.byteshaft.silentrecord.utils.Helpers;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Helpers.isAppRunningForTheFirstTime()) {
            Toast.makeText(
                    context,
                    "The App needs to be run at least once before using the widget",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent pictureService = AppGlobals.getPictureServiceIntent();
        Intent serviceIntent = AppGlobals.getRecordServiceIntent();
        String widget = intent.getStringExtra("key");

        if (widget.equals("1") && !FlashlightGlobals.isResourceOccupied()) {
            AppGlobals.getContext().startService(pictureService);
        } else if (widget.equals("2")) {
            if (RecordService.isRecording()) {
                AppGlobals.getContext().stopService(serviceIntent);
            } else {
                if (!PictureService.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    AppGlobals.getContext().startService(serviceIntent);
                }
            }
        }
        RemoteViews remoteViews = WidgetProvider.setUpWidgetView(context);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetProvider.class);
        widgetManager.updateAppWidget(widgetComponent, remoteViews);
    }
}
