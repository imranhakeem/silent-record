package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.byteshaft.ezflashlight.FlashlightGlobals;

public class WidgetReceiver extends BroadcastReceiver {

    private boolean mIsRecording;

    @Override
    public void onReceive(Context context, Intent intent) {

        CustomCamera customCamera = CustomCamera.getInstance(context);

        String widget = intent.getStringExtra("key");

        if (widget.equals("1")) {
            if (!FlashlightGlobals.isResourceOccupied()) {
                customCamera.takePicture();
                Toast.makeText(context, "Photo Captured", Toast.LENGTH_SHORT).show();
            }
        } else if (widget.equals("2")) {
            if (mIsRecording) {
                customCamera.stopRecording();
                Toast.makeText(context, "Recording Stopped", Toast.LENGTH_SHORT).show();
                mIsRecording = false;
            } else {
                if (!FlashlightGlobals.isResourceOccupied()) {
                    customCamera.startRecording();
                    Toast.makeText(context, "Recording Started", Toast.LENGTH_SHORT).show();
                    mIsRecording = true;
                    }
                }
            }
        }
    }
