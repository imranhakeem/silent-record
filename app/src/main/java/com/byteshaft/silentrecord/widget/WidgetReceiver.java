package com.byteshaft.silentrecord.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.CustomCamera;

public class WidgetReceiver extends BroadcastReceiver {

    private boolean mIsRecording;

    @Override
    public void onReceive(Context context, Intent intent) {

        CustomCamera customCamera = CustomCamera.getInstance(context);

        String widget = intent.getStringExtra("key");

        if (widget.equals("1")) {
            if (!FlashlightGlobals.isResourceOccupied()) {
                customCamera.takePicture();
            }
        } else if (widget.equals("2")) {
            if (customCamera.isRecording()) {
                customCamera.stopRecording();
                mIsRecording = false;
            } else {
                if (!CustomCamera.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    customCamera.startRecording();
                    mIsRecording = true;
                    }
                }
            }
        }
    }
