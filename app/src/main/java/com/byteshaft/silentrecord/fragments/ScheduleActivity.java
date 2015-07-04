package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.byteshaft.silentrecord.utils.Helpers;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.byteshaft.silentrecord.R;

import java.util.Calendar;

public class ScheduleActivity extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button mBtnTimePicker;
    private Button mBtnDatePicker;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static final String DATEPICKER_TAG = "datepicker";
    private static final String TIMEPICKER_TAG = "timepicker";
    private Helpers mHelpers;
    private Button mPicButton;
    private Button mVideoBtn;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHours;
    private int mMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        mHelpers = new Helpers(getActivity());
        mBtnDatePicker = (Button) view.findViewById(R.id.btnSetDate);
        mBtnTimePicker = (Button) view.findViewById(R.id.btnSetTime);
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
        mBtnTimePicker.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        mVideoBtn.setOnClickListener(this);
        return view;
    }

    private void setBackgroundForButtonPresent() {
            mBtnDatePicker.setBackgroundResource(R.drawable.date);
            mBtnTimePicker.setBackgroundResource(R.drawable.time);
        if (Helpers.getPicAlarmStatus()) {
            mPicButton.setBackgroundResource(R.drawable.pic_set);
        } else {
            mPicButton.setBackgroundResource(R.drawable.pic);
        }
        if (Helpers.getVideoAlarmStatus()) {
            System.out.println("this");
            mVideoBtn.setBackgroundResource(R.drawable.video_alarm);
        } else {
            System.out.println("that");
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
            case R.id.btnSetTime:
                timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
                break;
            case R.id.buttonPic:
                if (!Helpers.getPicAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"please select date or time first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getPicAlarmStatus()) {
                    mPicButton.setBackgroundResource(R.drawable.pic);
                    mHelpers.removePreviousAlarm();
                    mBtnDatePicker.setBackgroundResource(R.drawable.date);
                    mBtnTimePicker.setBackgroundResource(R.drawable.time);
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
                    mBtnDatePicker.setBackgroundResource(R.drawable.date);
                    mBtnTimePicker.setBackgroundResource(R.drawable.time);
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
        Toast.makeText(getActivity(),"year: "+ year + "Month :"+ month +"Day :" +day,Toast.LENGTH_SHORT).show();
        mBtnDatePicker.setBackgroundResource(R.drawable.date_set);
        mYear = year;
        mMonth = month;
        mDay = day;
        Helpers.setDate(true);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hours, int minutes) {
        Toast.makeText(getActivity(),"Hours: "+ hours + "Minutes :"+ minutes,Toast.LENGTH_SHORT).show();
        mBtnTimePicker.setBackgroundResource(R.drawable.time_set);
        mHours = hours;
        mMinutes = minutes;
        Helpers.setTime(true);


    }
}
