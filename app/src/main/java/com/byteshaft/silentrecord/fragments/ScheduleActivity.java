package com.byteshaft.silentrecord.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.Helpers;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    public static Button mBtnDatePicker;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static final String DATE_PICKER_TAG = "date_picker";
    private static final String TIME_PICKER_TAG = "time_picker";
    private Helpers mHelpers;
    public static Button mPictureButton;
    public static Button mVideoButton;
    private Button mButtonMax;
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
        mPictureButton = (Button) view.findViewById(R.id.button_photo_schedule);
        mVideoButton = (Button) view.findViewById(R.id.button_video_schedule);
        mButtonMax = (Button) view.findViewById(R.id.buttonMax);
        mBtnDatePicker.setOnClickListener(this);
        mPictureButton.setOnClickListener(this);
        mVideoButton.setOnClickListener(this);
        mButtonMax.setOnClickListener(this);
        mHours = datePreference.getInt("hours", 0);
        mMinutes = datePreference.getInt("minutes", 0);
        mDay = datePreference.getInt("day", 0);
        mMonth = datePreference.getInt("month", 0);
        mYear = datePreference.getInt("year", 0);
        setBackgroundForButtonPresent();
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), false, false);
        if (savedInstanceState != null) {
            DatePickerDialog datePickerDialog = (DatePickerDialog)
                    getFragmentManager().findFragmentByTag(DATE_PICKER_TAG);
            if (datePickerDialog != null) {
                datePickerDialog.setOnDateSetListener(this);
            }

            TimePickerDialog timePickerDialog = (TimePickerDialog)
                    getFragmentManager().findFragmentByTag(TIME_PICKER_TAG);
            if (timePickerDialog != null) {
                timePickerDialog.setOnTimeSetListener(this);
            }
        }
        return view;
    }

    private void setBackgroundForButtonPresent() {
        if (Helpers.getPicAlarmStatus()) {
            mPictureButton.setBackgroundResource(R.drawable.pic_alarm_set);
            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" + mMonth + "/" + mYear);
            mBtnDatePicker.setClickable(false);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
            mVideoButton.setVisibility(View.INVISIBLE);
        } else if (Helpers.getVideoAlarmStatus()) {
            mVideoButton.setBackgroundResource(R.drawable.video_alarm_set);
            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" + mMonth + "/" + mYear);
            mBtnDatePicker.setClickable(false);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
            mPictureButton.setVisibility(View.INVISIBLE);
        } else {
            mPictureButton.setBackgroundResource(R.drawable.pic_alarm_notset);
            mVideoButton.setBackgroundResource(R.drawable.video_alarm_notset);
            mBtnDatePicker.setText("Tap to set a Schedule");
            mBtnDatePicker.setClickable(true);
            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
            mPictureButton.setVisibility(View.VISIBLE);
            mVideoButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                datePickerDialog.setYearRange(2015, 2037);
                datePickerDialog.show(getFragmentManager(), DATE_PICKER_TAG);
                break;
            case R.id.button_photo_schedule:
                if (!Helpers.getPicAlarmStatus() && !Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"Please set a Schedule first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getPicAlarmStatus()) {
                    mVideoButton.setVisibility(View.VISIBLE);
                    mPictureButton.setBackgroundResource(R.drawable.pic_alarm_notset);
                    mBtnDatePicker.setText("Tap to set a Schedule");
                    mBtnDatePicker.setClickable(true);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                    mHelpers.removePreviousAlarm();
                    Helpers.setPicAlarm(false);
                    Helpers.setDate(false);
                    Helpers.setTime(false);
                    Toast.makeText(getActivity(),"Schedule is removed", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getTime() && Helpers.getDate()) {
                    String time = mDay+"/"+(mMonth+1)+"/"+mYear+" "+ mHours + ":" + mMinutes;
                    try {
                        Date now = Helpers.getTimeFormat().parse(mHelpers.getAmPm());
                        Date date = Helpers.getTimeFormat().parse(time);
                        if (date.after(now)) {
                            mVideoButton.setVisibility(View.INVISIBLE);
                            mPictureButton.setBackgroundResource(R.drawable.pic_alarm_set);
                            Helpers.setPicAlarm(true);
                            mHelpers.setAlarm(mYear, mMonth, mDay, mHours, mMinutes, "pic");
                            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" + (mMonth+1) + "/" + mYear);
                            mBtnDatePicker.setClickable(false);
                            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
                        } else {
                            Toast.makeText(getActivity(), "invalid time selection", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        Log.i(AppGlobals.getLogTag(getClass()), "parse exception");
                    }
                }
                break;
            case R.id.button_video_schedule:
                if (!Helpers.getVideoAlarmStatus() &&!Helpers.getTime() && !Helpers.getDate()) {
                    Toast.makeText(getActivity(),"Please set a Schedule first", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getVideoAlarmStatus()) {
                    mPictureButton.setVisibility(View.VISIBLE);
                    mVideoButton.setBackgroundResource(R.drawable.video_alarm_notset);
                    mBtnDatePicker.setText("Tap to set a Schedule");
                    mBtnDatePicker.setClickable(true);
                    mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background);
                    mHelpers.removePreviousAlarm();
                    Helpers.setVideoAlarm(false);
                    Helpers.setTime(false);
                    Toast.makeText(getActivity(),"Schedule is removed", Toast.LENGTH_SHORT).show();
                } else if (Helpers.getTime() && Helpers.getDate()) {
                    String time = mDay+"/"+(mMonth+1)+"/"+mYear+" "+ mHours + ":" + mMinutes;
                    try {
                        Date now = Helpers.getTimeFormat().parse(mHelpers.getAmPm());
                        Date date = Helpers.getTimeFormat().parse(time);
                        if (date.after(now)) {
                            mPictureButton.setVisibility(View.INVISIBLE);
                            mVideoButton.setBackgroundResource(R.drawable.video_alarm_set);
                            Helpers.setVideoAlarm(true);
                            mHelpers.setAlarm(mYear, mMonth, mDay, mHours, mMinutes, "video");
                            mBtnDatePicker.setText("Schedule is set\n" + mHours + ":" + mMinutes + " - " + mDay + "/" + (mMonth+1) + "/" + mYear);
                            mBtnDatePicker.setClickable(false);
                            mBtnDatePicker.setBackgroundResource(R.drawable.schedule_background_set);
                        } else {
                            Toast.makeText(getActivity(), "invalid time selection", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        Log.i(AppGlobals.getLogTag(getClass()), "parse exception");
                    }
                }
                break;
            case R.id.buttonMax:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog);
        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        dialog.setTitle("Max Time");
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()) {
                    SharedPreferences sharedPreferences = AppGlobals.getPreferenceManager();
                    sharedPreferences.edit().putString("max_video", editText.getText().
                            toString()).apply();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "please enter max duration", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // if button is clicked, close the custom dialog

        dialog.show();
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
        timePickerDialog.show(getFragmentManager(), TIME_PICKER_TAG);
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
