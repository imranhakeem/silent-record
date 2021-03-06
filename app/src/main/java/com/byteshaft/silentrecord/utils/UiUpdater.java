package com.byteshaft.silentrecord.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.fragments.VideosActivity;
import com.byteshaft.silentrecord.services.NotificationService;
import com.byteshaft.silentrecord.services.RecordService;
import com.byteshaft.silentrecord.widget.WidgetProvider;

import java.util.Formatter;
import java.util.Locale;

public class UiUpdater {

    private final int ONE_SECOND = 1000;
    private static Handler sHandler;
    private int counter = 1;
    private RemoteViews mWidgetView;
    private ComponentName mWidgetComponent;

    public void updateRecordingTimeInUi() {
        Handler handler = getHandler();
        if (RecordService.isRecording()) {
            handler.postDelayed(uiUpdateRunnable, ONE_SECOND);
        } else {
            handler.removeCallbacks(uiUpdateRunnable);
            resetUi();
        }
    }

    private void updateRecordingTimeInApp(String formattedTime) {
        if (VideosActivity.isRunning() && RecordService.isRecording()) {
            VideosActivity.getInstance().mLabelRecordTime.setText(formattedTime);
            VideosActivity.getInstance().mLabelRecordTime.setVisibility(View.VISIBLE);
            if (counter % 2 == 0) {
                VideosActivity.getInstance().mVideoButtonVideoActivity.setImageResource(R.drawable.camcoder_rec_two);
            } else {
                VideosActivity.getInstance().mVideoButtonVideoActivity.setImageResource(R.drawable.camcoder_rec);
            }
        }
    }

    private void updateRecordingTimeInWidget(String formattedTime) {
        RemoteViews widgetView = getWidgetView();
        ComponentName widgetComponent = getWidgetComponent();
        if (counter % 2 == 0) {
            widgetView.setImageViewResource(R.id.video_button_widget, R.drawable.camcoder_rec_two);
        } else {
            widgetView.setImageViewResource(R.id.video_button_widget, R.drawable.camcoder_rec);
        }
        widgetView.setViewVisibility(R.id.textview_widget, View.VISIBLE);
        widgetView.setTextViewText(R.id.textview_widget, formattedTime);
        AppWidgetManager.getInstance(AppGlobals.getContext()).updateAppWidget(widgetComponent, widgetView);
    }

    private void updateRecordingTimeInNotification(String formattedTime) {
        if (RecordService.isRecording()) {
            RecordService.getInstance().updateNotification(formattedTime);
        }
    }

    private Runnable uiUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (RecordService.isRecording()) {
                String time = getFormattedTime(ONE_SECOND * counter);
                updateRecordingTimeInApp(time);
                updateRecordingTimeInWidget(time);
                if (Helpers.isWidgetSwitchOn()) {
                    updateRecordingTimeInNotification(time);
                }
                counter++;
            }
            updateRecordingTimeInUi();

        }
    };

    private Handler getHandler() {
        if (sHandler == null) {
            sHandler = new android.os.Handler();
        }
        return sHandler;
    }

    private void resetUi() {
        resetApp();
        resetWidget();
        resetNotification();
    }

    private void resetApp() {
        if (VideosActivity.isRunning()) {
            VideosActivity.getInstance().mLabelRecordTime.setVisibility(View.GONE);
            VideosActivity.getInstance().mLabelRecordTime.setText("00:00");
            VideosActivity.getInstance().mVideoButtonVideoActivity.setImageResource(R.drawable.camcoder);
        }
    }

    private void resetWidget() {
        RemoteViews widgetView = getWidgetView();
        ComponentName widgetComponent = getWidgetComponent();
        widgetView.setViewVisibility(R.id.textview_widget, View.GONE);
        widgetView.setImageViewResource(R.id.video_button_widget, R.drawable.widget_effect_two);
        AppWidgetManager.getInstance( AppGlobals.getContext() ).updateAppWidget(widgetComponent, widgetView);
    }

    private void resetNotification() {
        if (Helpers.isWidgetSwitchOn()) {
            Context context = AppGlobals.getContext();
            Intent service = new Intent(context, NotificationService.class);
            context.startService(service);
        }
    }

    private String getFormattedTime(int timeMs) {
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

    private RemoteViews getWidgetView() {
        if (mWidgetView == null) {
            mWidgetView = new RemoteViews(AppGlobals.getContext().getPackageName(), R.layout.app_widget);
        }
        return mWidgetView;
    }

    private ComponentName getWidgetComponent() {
        if (mWidgetComponent == null) {
            mWidgetComponent = new ComponentName(AppGlobals.getContext(), WidgetProvider.class);
        }
        return mWidgetComponent;
    }
}
