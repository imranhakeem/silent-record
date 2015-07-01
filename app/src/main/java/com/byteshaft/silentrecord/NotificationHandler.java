package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getExtras().getString("do_action");
        if (action != null) {
            if (action.equals("take_picture")) {
                Toast.makeText(context, "Picture Taken!", Toast.LENGTH_SHORT).show();
            }else if (action.equals("record_video")) {
                Toast.makeText(context, "Recording Started!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
