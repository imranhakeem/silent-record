package com.byteshaft.silentrecord.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.CustomCamera;
import com.byteshaft.silentrecord.R;

import java.util.Formatter;
import java.util.Locale;

public class WidgetReceiver extends BroadcastReceiver {

    RemoteViews remoteViews = new RemoteViews(AppGlobals.getContext().getPackageName(), R.layout.app_widget);
    ComponentName thisWidget = new ComponentName( AppGlobals.getContext(), WidgetProvider.class);
    CustomCamera customCamera = CustomCamera.getInstance(AppGlobals.getContext());

    int counter = 1;

    @Override
    public void onReceive(Context context, Intent intent) {


        String widget = intent.getStringExtra("key");

        if (widget.equals("1")) {
            if (!FlashlightGlobals.isResourceOccupied()) {
                customCamera.takePicture();
            }
        } else if (widget.equals("2")) {
            if (customCamera.isRecording()) {
                customCamera.stopRecording();
                updateWidgetUI();
            } else {
                if (!CustomCamera.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    customCamera.startRecording();
                    System.out.print(customCamera.isRecording());
                    updateWidgetUI();
                    }
                }
            }
        }

    private void updateWidgetUI() {
        if(!CustomCamera.isRecording()) {
            remoteViews.setViewVisibility(R.id.textview_widget, View.GONE);
            remoteViews.setImageViewResource(R.id.video_button_widget, R.drawable.camcoder);
            AppWidgetManager.getInstance( AppGlobals.getContext() ).updateAppWidget(thisWidget, remoteViews);
            return;
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (counter % 2 == 0) {
                    remoteViews.setImageViewResource(R.id.video_button_widget, R.drawable.camcoder_rec_two);
                } else {
                    remoteViews.setImageViewResource(R.id.video_button_widget, R.drawable.camcoder_rec);
                }
                remoteViews.setViewVisibility(R.id.textview_widget, View.VISIBLE);
                remoteViews.setTextViewText(R.id.textview_widget, getFormattedTime(counter * 1000));
                AppWidgetManager.getInstance( AppGlobals.getContext() ).updateAppWidget(thisWidget, remoteViews);
                updateWidgetUI();
                counter++;
            }

        },1000);

        }
    String getFormattedTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
