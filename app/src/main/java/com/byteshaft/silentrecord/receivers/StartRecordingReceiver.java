package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.CustomCamera;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.fragments.ScheduleActivity;
import com.byteshaft.silentrecord.utils.Helpers;


public class StartRecordingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomCamera camera  = CustomCamera.getInstance(context.getApplicationContext());
        String operationType = intent.getAction();
        System.out.println(operationType);
        if (operationType.equals("com.byteShaft.Alarm")) {
            Log.i(AppGlobals.getLogTag(getClass()),"Capturing image ...");
            if (!FlashlightGlobals.isResourceOccupied()) {
                camera.takePicture();
            }
            Helpers.setPicAlarm(false);
            Helpers.setDate(false);
            Helpers.setTime(false);
            ScheduleActivity.mPicButton.setBackgroundResource(R.drawable.pic_alarm_notset);
            ScheduleActivity.mBtnDatePicker.setText("Set a Schedule");
            ScheduleActivity.mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
        } else if (operationType.equals("com.byteShaft.VideoAlarm")) {
            Log.i(AppGlobals.getLogTag(getClass()),"Capturing video ...");
            if (!CustomCamera.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                camera.startRecording();
            }
            Helpers.setVideoAlarm(false);
            Helpers.setDate(false);
            Helpers.setTime(false);
            ScheduleActivity.mVideoBtn.setBackgroundResource(R.drawable.video_alarm_notset);
            ScheduleActivity.mBtnDatePicker.setText("Set a Schedule");
            ScheduleActivity.mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
        }

    }
}
