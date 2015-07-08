package com.byteshaft.silentrecord.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.ezflashlight.FlashlightGlobals;
import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.services.PictureService;
import com.byteshaft.silentrecord.services.RecordService;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pictureService = AppGlobals.getPictureServiceIntent();
        Intent serviceIntent = AppGlobals.getRecordServiceIntent();
        String widget = intent.getStringExtra("key");

        if (widget.equals("1") && !FlashlightGlobals.isResourceOccupied()) {
            AppGlobals.getContext().startService(pictureService);
        } else if (widget.equals("2")) {
            if (RecordService.isRecording()) {
                AppGlobals.getContext().stopService(serviceIntent);
            } else {
                if (!PictureService.isTakingPicture() && !FlashlightGlobals.isResourceOccupied()) {
                    AppGlobals.getContext().startService(serviceIntent);
                }
            }
        }
    }
}
