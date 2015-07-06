package com.byteshaft.silentrecord.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    public static Button mBtnDatePicker;
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
    private SharedPreferences datePreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mHelpers = new Helpers(getActivity());
        datePreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mBtnDatePicker = (Button) view.findViewById(R.id.btnSetDate);
        mPicButton = (Button) view.findViewById(R.id.button_photo_schedule);
        mVideoBtn = (Button) view.findViewById(R.id.button_video_schedule);
        mBtnDatePicker.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        mVideoBtn.setOnClickListener(this);
        mYear = datePreference.getInt("year", 0);
        mMonth = datePreference.getInt("month", 0);
        mDay = datePreference.getInt("day", 0);
        mHours = datePreference.getInt("hours", 0);
        mMinutes = datePreference.getInt("minutes", 0);
        setBackgroundForButtonPresent();
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), false, false);
        if (savedInstanceState != null) {
            DatePickerDialog datePickerDialog = (DatePickerDialog)
                    getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (datePickerDialog != null) {
                datePickerDialog.setOnDateSetListener(this);
            }

            TimePickerDialog timePickerDialog = (TimePickerDialog)
                    getFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (timePickerDialog != null) {
                timePickerDialog.setOnTimeSetListener(this);
            }
        }
        return view;
    }

    private void setBackgroundForButtonPresent() {
        if (Helpers.getPicAlarmStatus()) {
            mPicButton.setBackgroundResource(R.drawable.pic_alarm_set);
            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" +  (mMonth+1) + "/" + mYear);
            mBtnDatePicker.setClickable(false);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
            mVideoBtn.setVisibility(View.INVISIBLE);
        } else if (Helpers.getVideoAlarmStatus()) {
            mVideoBtn.setBackgroundResource(R.drawable.video_alarm_set);
            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" +  (mMonth+1) + "/" + mYear);
            mBtnDatePicker.setClickable(false);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
            mPicButton.setVisibility(View.INVISIBLE);
        } else {
            mPicButton.setBackgroundResource(R.drawable.pic_alarm_notset);
            mVideoBtn.setBackgroundResource(R.drawable.video_alarm_notset);
            mBtnDatePicker.setText("Tap to set a Schedule");
            mBtnDatePicker.setClickable(true);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
            mPicButton.setVisibility(View.VISIBLE);
            mVideoBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                datePickerDialog.setYearRange(2015, 2037);
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.button_photo_schedule:
                if (!Helpers.getPicAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"Please set a Schedule first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getPicAlarmStatus()) {
                    mVideoBtn.setVisibility(View.VISIBLE);
                    mPicButton.setBackgroundResource(R.drawable.pic_alarm_notset);
                    mBtnDatePicker.setText("Tap to set a Schedule");
                    mBtnDatePicker.setClickable(true);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                    mHelpers.removePreviousAlarm();
                    Helpers.setPicAlarm(false);
                    Helpers.setDate(false);
                    Helpers.setTime(false);
                    Toast.makeText(getActivity(),"Schedule is removed", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getTime() && Helpers.getDate()) {
                    mVideoBtn.setVisibility(View.INVISIBLE);
                    mPicButton.setBackgroundResource(R.drawable.pic_alarm_set);
                    mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" +  (mMonth+1) + "/" + mYear);
                    mBtnDatePicker.setClickable(false);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
                    Helpers.setPicAlarm(true);
                    mHelpers.setAlarm(mDay,mMonth,mYear,mHours,mMinutes, "pic");
                }
                break;
            case R.id.button_video_schedule:
                if (!Helpers.getVideoAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"Please set a Schedule first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getVideoAlarmStatus()) {
                    mPicButton.setVisibility(View.VISIBLE);
                    mVideoBtn.setBackgroundResource(R.drawable.video_alarm_notset);
                    mBtnDatePicker.setText("Tap to set a Schedule");
                    mBtnDatePicker.setClickable(true);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                    mHelpers.removePreviousAlarm();
                    Helpers.setVideoAlarm(false);
                    Helpers.setDate(false);
                    Helpers.setTime(false);
                    Toast.makeText(getActivity(),"Schedule is removed", Toast.LENGTH_SHORT).show();

                } else if (Helpers.getTime() && Helpers.getDate()) {
                    mPicButton.setVisibility(View.INVISIBLE);
                    mVideoBtn.setBackgroundResource(R.drawable.video_alarm_set);
                    Helpers.setVideoAlarm(true);
                    mHelpers.setAlarm(mDay, mMonth, mYear, mHours, mMinutes, "video");
                    mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" + (mMonth+1) + "/" + mYear);
                    mBtnDatePicker.setClickable(false);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(getActivity(),"Date: "+ day + "/"+ (month + 1) +"/" + year, Toast.LENGTH_SHORT).show();
        datePreference.edit().putInt("day", day).apply();
        datePreference.edit().putInt("month", month).apply();
        datePreference.edit().putInt("year", year).apply();
        mYear = year;
        mMonth = month;
        mDay = day;
        Helpers.setDate(true);
        timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hours, int minutes) {
        Toast.makeText(getActivity(),"Time: "+ hours + ":"+ minutes, Toast.LENGTH_SHORT).show();
        datePreference.edit().putInt("hours", hours).apply();
        datePreference.edit().putInt("minutes", minutes).apply();
        mHours = hours;
        mMinutes = minutes;
        Helpers.setTime(true);
    }
}
