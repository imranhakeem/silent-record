package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;

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
                    AppGlobals.getContext().stopService(recordService);
                } else {
                    if (!FlashlightGlobals.isResourceOccupied() && !PictureService.isTakingPicture()) {
                        AppGlobals.getContext().stopService(recordService);
                    }
                }
            }
        }
    }
}
