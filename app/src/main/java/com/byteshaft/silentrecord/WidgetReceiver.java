package com.byteshaft.silentrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String widget = intent.getStringExtra("key");

        if (widget.equals("1")) {
            Toast.makeText(context, "Widget Button One", Toast.LENGTH_SHORT).show();
        } else if (widget.equals("2")) {
            Toast.makeText(context, "Widget Button Two", Toast.LENGTH_SHORT).show();
        }
    }
}