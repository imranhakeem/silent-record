package com.byteshaft.silentrecord.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.utils.Helpers;

import java.text.ParseException;
import java.util.Date;


public class BootStateReceiver extends BroadcastReceiver {
    Helpers mHelpers;


    @Override
    public void onReceive(Context context, Intent intent) {
        mHelpers = new Helpers(context);
        Log.i(AppGlobals.getLogTag(getClass()), "Reading previous alarm values");
        if (Helpers.getPreviousAlarmStatus()) {
            try {
                String previousDate = Helpers.getCurrentAlarmDetails("day")+"/"+
                        (Helpers.getCurrentAlarmDetails("month")+1)+"/"+
                        Helpers.getCurrentAlarmDetails("year")+" "+
                        Helpers.getCurrentAlarmDetails("hours") + ":" +
                        Helpers.getCurrentAlarmDetails("minutes");
                Date date = Helpers.getTimeFormat().parse(previousDate);
                Date now = Helpers.getTimeFormat().parse(mHelpers.getAmPm());
                if (date.after(now)) {
                    mHelpers.setAlarm(Helpers.getCurrentAlarmDetails("year"),
                            Helpers.getCurrentAlarmDetails("month"), Helpers.getCurrentAlarmDetails("day"),
                            Helpers.getCurrentAlarmDetails("hours"),
                            Helpers.getCurrentAlarmDetails("minutes"),Helpers.getLatsCameraEvent());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }
}