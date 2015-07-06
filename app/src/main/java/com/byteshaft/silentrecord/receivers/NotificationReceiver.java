package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.silentrecord.CustomCamera;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CustomCamera.isRecording()) {
            CustomCamera.getInstance(context).stopRecording();
        }
    }
}
