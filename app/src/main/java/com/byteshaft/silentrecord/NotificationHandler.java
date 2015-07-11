package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;
import com.byteshaft.silentrecord.utils.Helpers;

public class NotificationHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent recordService = AppGlobals.getRecordServiceIntent();
        Intent pictureService = AppGlobals.getPictureServiceIntent();
        String action = intent.getExtras().getString("do_action");
        if (action != null) {
            if (action.equals("take_picture")) {
                if (!FlashlightGlobals.isResourceOccupied()) {
                    AppGlobals.getContext().startService(pictureService);
                }
            } else if (action.equals("record_video")) {
                if (RecordService.isRecording()) {
                    handleStopRequest(context, recordService);
                } else {
                    if (!FlashlightGlobals.isResourceOccupied() && !PictureService.isTakingPicture()) {
                        AppGlobals.getContext().startService(recordService);
                    }
                }
            }
        }
    }

    public static void handleStopRequest(Context context, Intent serviceIntent) {
        if (Helpers.isScreenLocked()) {
            AppGlobals.getContext().stopService(serviceIntent);
        } else {
            // Hide the notification bar
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
            // Pop the stop confirmation dialog
            Intent dialog = new Intent(context, ConfirmationDialog.class);
            dialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dialog);
        }
    }
}