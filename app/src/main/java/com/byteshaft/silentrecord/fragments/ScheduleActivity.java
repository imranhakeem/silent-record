package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.silentrecord.utils.Helpers;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.byteshaft.silentrecord.R;

import java.util.Calendar;

public class ScheduleActivity extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button mBtnDatePicker;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static final String DATEPICKER_TAG = "datepicker";
    private static final String TIMEPICKER_TAG = "timepicker";
    private Helpers mHelpers;
    public static Button mPicButton;
    public static Button mVideoBtn;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHours;
    private int mMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mHelpers = new Helpers(getActivity());
        mBtnDatePicker = (Button) view.findViewById(R.id.btnSetDate);
        mPicButton = (Button) view.findViewById(R.id.buttonPic);
        mVideoBtn = (Button) view.findViewById(R.id.buttonVideo);
        setBackgroundForButtonPresent();
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), false, false);
        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog)
                    getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog)
                    getFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }
        mBtnDatePicker.setOnClickListener(this);
//        mBtnTimePicker.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        mVideoBtn.setOnClickListener(this);
        return view;
    }

    private void setBackgroundForButtonPresent() {
        if (Helpers.getPicAlarmStatus()) {
            mPicButton.setBackgroundResource(R.drawable.pic_set);
        } else {
            mPicButton.setBackgroundResource(R.drawable.pic);
        }
        if (Helpers.getVideoAlarmStatus()) {
            mVideoBtn.setBackgroundResource(R.drawable.video_alarm);
        } else {
            mVideoBtn.setBackgroundColor(R.drawable.alarm_not_set);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                datePickerDialog.setYearRange(2015, 2037);
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.buttonPic:
                if (!Helpers.getPicAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"please select date or time first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getPicAlarmStatus()) {
                    mPicButton.setBackgroundResource(R.drawable.pic);
                    mHelpers.removePreviousAlarm();
                    mBtnDatePicker.setBackgroundResource(R.drawable.date);
                    Helpers.setPicAlarm(false);
                    Helpers.setDate(false);
                    Helpers.setTime(false);
                } else if (Helpers.getTime() && Helpers.getDate()) {
                    mPicButton.setBackgroundResource(R.drawable.pic_set);
                    Helpers.setPicAlarm(true);
                    mHelpers.setAlarm(mDay,mMonth,mYear,mHours,mMinutes, "pic");
                }
                break;
            case R.id.buttonVideo:
                if (!Helpers.getVideoAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"please select date or time first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getVideoAlarmStatus()) {
                    mVideoBtn.setBackgroundResource(R.drawable.alarm_not_set);
                    mHelpers.removePreviousAlarm();
                    Helpers.setVideoAlarm(false);
                    Helpers.setDate(false);
                    Helpers.setTime(false);
                } else if (Helpers.getTime() && Helpers.getDate()) {
                    mVideoBtn.setBackgroundResource(R.drawable.video_alarm);
                    Helpers.setVideoAlarm(true);
                    mHelpers.setAlarm(mDay,mMonth,mYear,mHours,mMinutes, "video");
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(getActivity(),"Date: "+ day + " Month :"+ month +" Year :" +day,Toast.LENGTH_SHORT).show();
        mYear = year;
        mMonth = month;
        mDay = day;
        Helpers.setDate(true);
        timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hours, int minutes) {
        Toast.makeText(getActivity(),"Hours :"+ hours + " Minutes :"+ minutes,Toast.LENGTH_SHORT).show();
        mHours = hours;
        mMinutes = minutes;
        Helpers.setTime(true);


    }
}
