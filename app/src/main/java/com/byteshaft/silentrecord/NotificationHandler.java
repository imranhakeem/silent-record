package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.ezflashlight.FlashlightGlobals;

public class NotificationHandler extends BroadcastReceiver {

    CustomCamera customCamera = CustomCamera.getInstance(AppGlobals.getContext());

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getExtras().getString("do_action");
        if (action != null) {
            if (action.equals("take_picture")) {
                if (!FlashlightGlobals.isResourceOccupied()) {
                    customCamera.takePicture();
                }
            }else if (action.equals("record_video")) {
                if (customCamera.isRecording()) {
                    customCamera.stopRecording();
                } else {
                    if (!FlashlightGlobals.isResourceOccupied() && !CustomCamera.isTakingPicture()) {
                        customCamera.startRecording();
                    }
                }
            }
        }
    }
}
