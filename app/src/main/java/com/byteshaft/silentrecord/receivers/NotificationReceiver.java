package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.silentrecord.ConfirmationDialog;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dialog = new Intent(context, ConfirmationDialog.class);
        dialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialog);
    }
}
