package com.byteshaft.silentrecord.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.CustomCamera;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomCamera customCamera = CustomCamera.getInstance(AppGlobals.getContext());
        String widget = intent.getStringExtra("key");

        if (widget.equals("1") && !FlashlightGlobals.isResourceOccupied()) {
            customCamera.takePicture();
        } else if (widget.equals("2")) {
            if (CustomCamera.isRecording()) {
                customCamera.stopRecording();
            } else {
                if (!CustomCamera.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    customCamera.startRecording();
                }
            }
        }
    }
}
