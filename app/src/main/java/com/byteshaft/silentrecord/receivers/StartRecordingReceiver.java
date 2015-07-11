package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.fragments.ScheduleActivity;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;
import com.byteshaft.silentrecord.utils.Helpers;


public class StartRecordingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pictureService = AppGlobals.getPictureServiceIntent();
        Intent service = new Intent(context, RecordService.class);
        String operationType = intent.getStringExtra("operationType");
        if (operationType.equals("pic")) {
            Log.i(AppGlobals.getLogTag(getClass()), "Capturing image ...");
            if (!FlashlightGlobals.isResourceOccupied()) {
                AppGlobals.getContext().startService(pictureService);
                Helpers.setPicAlarm(false);
                Helpers.setTime(false);
                Helpers.setDate(false);
                if (AppGlobals.sActivityForeground) {
                    ScheduleActivity.mPictureButton.setBackgroundResource(R.drawable.pic_alarm_notset);
                    ScheduleActivity.mBtnDatePicker.setText("Tap to set a Schedule");
                    ScheduleActivity.mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                    ScheduleActivity.mVideoButton.setVisibility(View.VISIBLE);
                    ScheduleActivity.mBtnDatePicker.setClickable(true);
                }
                    Helpers.previousAlarmStatus(false);
                }
            } else if (operationType.equals("video")) {
                Log.i(AppGlobals.getLogTag(getClass()), "Capturing video ...");
                if (!PictureService.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    AppGlobals.getContext().startService(service);
                    Helpers.setVideoAlarm(false);
                    Helpers.setTime(false);
                    Helpers.setDate(false);
                    Helpers.previousAlarmStatus(false);
                    if (AppGlobals.sActivityForeground) {
                        ScheduleActivity.mVideoButton.setBackgroundResource(R.drawable.video_alarm_notset);
                        ScheduleActivity.mBtnDatePicker.setText("Tap to set a Schedule");
                        ScheduleActivity.mBtnDatePicker.setClickable(true);
                        ScheduleActivity.mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                        ScheduleActivity.mPictureButton.setVisibility(View.VISIBLE);
                    }
                }
        }
    }
}
