package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.byteshaft.ezflashlight.FlashlightGlobals;

public class NotificationHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomCamera customCamera = CustomCamera.getInstance(AppGlobals.getContext());
        String action = intent.getExtras().getString("do_action");
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
        if (action != null) {
            if (action.equals("take_picture")) {
                if (!FlashlightGlobals.isResourceOccupied()) {
                    customCamera.takePicture();
                }
            } else if (action.equals("record_video")) {
                if (CustomCamera.isRecording()) {
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
