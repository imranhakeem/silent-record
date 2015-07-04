package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StartRecordingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String operationType = intent.getStringExtra("operationType");
        System.out.println(operationType);

    }
}
