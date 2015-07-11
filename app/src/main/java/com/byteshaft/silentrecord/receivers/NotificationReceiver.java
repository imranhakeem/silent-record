package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.ConfirmationDialog;
import com.byteshaft.silentrecord.services.RecordService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent dialog = new Intent(context, ConfirmationDialog.class);
        context.startActivity(dialog);

//        if (RecordService.isRecording()) {
//            AppGlobals.getContext().stopService(service);
//        }
    }
}
