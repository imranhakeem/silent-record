package com.byteshaft.silentrecord;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.byteshaft.ezflashlight.FlashlightGlobals;

import java.util.Calendar;

public class ScheduleActivity extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private boolean mIsRecording;
    private Button mVideoButton;
    private Button mPictureButton;
    private Button mBtnTimePicker;
    private Button mBtnDatePicker;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    static final String DATEPICKER_TAG = "datepicker";
    static final String TIMEPICKER_TAG = "timepicker";
    private Helpers mHelpers;
    private Button mPicButton;
    private Button mVideoBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        mHelpers = new Helpers(getActivity());
        mPictureButton = (Button) view.findViewById(R.id.button_picture);
        mVideoButton = (Button) view.findViewById(R.id.button_record);
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
        mPictureButton.setOnClickListener(this);
        mVideoButton.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        mVideoBtn.setOnClickListener(this);
        return view;
    }

    private void setBackgroundForButtonPresent() {
        if (mHelpers.getDate()) {
            mBtnDatePicker.setBackgroundResource(R.drawable.date_set);
        } else {
            mBtnDatePicker.setBackgroundResource(R.drawable.date);
        }
        if (mHelpers.getTime()) {
            mBtnTimePicker.setBackgroundResource(R.drawable.time_set);
        } else {
            mBtnTimePicker.setBackgroundResource(R.drawable.time);
        }
        if (mHelpers.getTime() && mHelpers.getDate()) {
            mPicButton.setBackgroundResource(R.drawable.pic_set);
        } else {
            mPicButton.setBackgroundResource(R.drawable.pic);
        }
    }

    @Override
    public void onClick(View view) {
        CustomCamera camera  = CustomCamera.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.button_picture:
                if (!FlashlightGlobals.isResourceOccupied()) {
                    camera.takePicture();
                }
                break;
            case R.id.button_record:
                if (mIsRecording) {
                    camera.stopRecording();
                    mVideoButton.setText("Start recording");
                    mIsRecording = false;
                } else {
                    camera.startRecording();
                    mVideoButton.setText("Stop recording");
                    mIsRecording = true;
                }
                break;
            case R.id.btnSetDate:
                datePickerDialog.setYearRange(2015, 2037);
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.btnSetTime:
                timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
                break;
            case R.id.buttonPic:

                break;
            case R.id.buttonVideo:
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(getActivity(),"year: "+ year + "Month :"+ month +"Day :" +day,Toast.LENGTH_SHORT).show();
        mBtnDatePicker.setBackgroundResource(R.drawable.date_set);
        mHelpers.setDate(true);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hours, int minutes) {
        Toast.makeText(getActivity(),"Hours: "+ hours + "Minutes :"+ minutes,Toast.LENGTH_SHORT).show();
        mBtnTimePicker.setBackgroundResource(R.drawable.time_set);
        mHelpers.setTime(true);


    }
}
