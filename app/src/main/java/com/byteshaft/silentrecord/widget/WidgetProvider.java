package com.byteshaft.silentrecord.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.services.WidgetUpdateService;
import com.byteshaft.silentrecord.utils.Helpers;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (!WidgetUpdateService.isRunning()) {
            Intent updateServiceIntent = new Intent(context, WidgetUpdateService.class);
            context.startService(updateServiceIntent);
        }
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = setUpWidgetView(context);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    static RemoteViews setUpWidgetView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Intent intent1 = new Intent(context, WidgetReceiver.class);
        intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent1.putExtra("key", "1");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.photo_button_widget, pendingIntent1);

        Intent intent2 = new Intent(context, WidgetReceiver.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent2.putExtra("key", "2");
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.video_button_widget, pendingIntent2);
        return remoteViews;
    }


    @Override
    public void onEnabled(Context context) {
        Helpers.setIsWidgetEnabledOnHome(true);
        Intent updateServiceIntent = new Intent(context, WidgetUpdateService.class);
        context.startService(updateServiceIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Helpers.setIsWidgetEnabledOnHome(false);
        Intent updateServiceIntent = new Intent(context, WidgetUpdateService.class);
        context.stopService(updateServiceIntent);
    }
}

