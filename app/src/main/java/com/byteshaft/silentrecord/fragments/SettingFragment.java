package com.byteshaft.silentrecord.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.notification.LollipopNotification;
import com.byteshaft.silentrecord.notification.NotificationWidget;
import com.byteshaft.silentrecord.services.NotificationService;
import com.byteshaft.silentrecord.services.RecordService;
import com.byteshaft.silentrecord.utils.AppConstants;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.Helpers;
import com.github.machinarius.preferencefragment.PreferenceFragment;

public class SettingFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Helpers mHelpers;
    private SwitchPreference notificationSwitch;
    public static SwitchPreference widgetSwitch;
//    private SwitchPreference notificationSwitch;
    private EditTextPreference editTextPreference;
    private ListPreference VideoResolution;
    private ListPreference pictureSceneMode;
//    private ListPreference videoSceneMode;
    private ListPreference imageResolution;
    private ListPreference cameraZoomControl;
    private ListPreference mCameraFaces;
    private EditTextPreference pinEditText;
    private String[] supportedZoomLevels;
    private ListPreference defaultCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        mHelpers = new Helpers(getActivity());
        setUpCameraSelection();
        loadAndSetUpSettingsFragment();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void loadAndSetUpSettingsFragment() {
        String selectedCamera = mHelpers.getValueFromKey("default_camera");
        pinEditText = (EditTextPreference) findPreference("pin_code");

        widgetSwitch = (SwitchPreference) findPreference("notifidget");
        widgetSwitch.setOnPreferenceChangeListener(this);

        SwitchPreference videoSwitch = (SwitchPreference) findPreference("video_visibility");
        videoSwitch.setOnPreferenceChangeListener(this);
        SwitchPreference imageSwitch = (SwitchPreference) findPreference("image_visibility");
        imageSwitch.setOnPreferenceChangeListener(this);
//        SwitchPreference notificationSwitch = (SwitchPreference) findPreference("notification_widget");
//        notificationSwitch.setOnPreferenceChangeListener(this);

        defaultCamera = (ListPreference) findPreference("default_camera");
        defaultCamera.setSummary(mHelpers.getValueFromKey("default_camera"));


        VideoResolution = (ListPreference) findPreference("video_resolution");
        setEntriesAndValues(
                VideoResolution,
                CameraCharacteristics.getSupportedVideoResolutions(selectedCamera)
        );
        setDefaultEntryIfNotPreviouslySet(VideoResolution);
        VideoResolution.setSummary(Helpers.getValueFromKey("video_resolution"));

//        videoFormat = (ListPreference) findPreference("video_format");
//        setVideoFormatSummary();

        editTextPreference = (EditTextPreference) findPreference("max_video");
        editTextPreference.setSummary(Helpers.getValueFromKey("max_video") + " minutes");

        handleSceneModes(selectedCamera);

        imageResolution = (ListPreference) findPreference("image_resolution");
        setEntriesAndValues(
                imageResolution,
                CameraCharacteristics.getSupportedPictureSizes(selectedCamera)
        );
        setDefaultEntryIfNotPreviouslySet(imageResolution);
        imageResolution.setSummary(Helpers.getValueFromKey("image_resolution"));
        handlesZoomLevels(selectedCamera);
    }

//    private void setVideoFormatSummary() {
//        String summaryValue = mHelpers.getValueFromKey("video_format");
//        switch (Integer.valueOf(summaryValue)) {
//            case 1:
//                videoFormat.setSummary("mp4");
//                break;
//            case 2:
//                videoFormat.setSummary("3gp");
//                break;
//        }
//    }

    private void handlesZoomLevels(String selectedCamera) {
        cameraZoomControl = (ListPreference) findPreference("camera_zoom_control");
        String zoomValue = Helpers.getValueFromKey("camera_zoom_control");
        supportedZoomLevels = CameraCharacteristics.getSupportedZoomLevels(selectedCamera);
        cameraZoomControl.setEntryValues(supportedZoomLevels);
        if (supportedZoomLevels == null) {
            cameraZoomControl.setEnabled(false);
            return;
        } else {
            cameraZoomControl.setEnabled(true);
        }
        if (zoomValue.equals(" ")) {
            cameraZoomControl.setValueIndex(0);
            return;
        }
        setDefaultEntryIfNotPreviouslySet(cameraZoomControl);
        handleZoomSummaries();
    }

    private void handleZoomSummaries() {
        String zoomValue = Helpers.getValueFromKey("camera_zoom_control");
        if (zoomValue.equals(" ")) {
            zoomValue = "0";
        }
        if (supportedZoomLevels[0].equals(zoomValue)) {
            cameraZoomControl.setSummary("default");
        } else if (supportedZoomLevels[1].equals(zoomValue)) {
            cameraZoomControl.setSummary("2x");
        } else if (supportedZoomLevels[2].equals(zoomValue)) {
            cameraZoomControl.setSummary("3x");
        } else if (supportedZoomLevels[3].equals(zoomValue)) {
            cameraZoomControl.setSummary("4x");
        }
    }

    private void handleSceneModes(String selectedCamera) {
        pictureSceneMode = (ListPreference) findPreference("picture_scene_mode");
//        videoSceneMode = (ListPreference) findPreference("video_scene_mode");

        String[] supportedModes = CameraCharacteristics.getSupportedSceneModes(selectedCamera);
        if (supportedModes == null) {
            pictureSceneMode.setEnabled(false);
//            videoSceneMode.setEnabled(false);
            return;
        } else {
            pictureSceneMode.setEnabled(true);
//            videoSceneMode.setEnabled(true);
        }

        setEntriesAndValues(pictureSceneMode, supportedModes);
        setDefaultEntryIfNotPreviouslySet(pictureSceneMode);
        pictureSceneMode.setSummary(Helpers.getValueFromKey("picture_scene_mode"));

//        setEntriesAndValues(videoSceneMode, supportedModes);
//        setDefaultEntryIfNotPreviouslySet(videoSceneMode);
//        videoSceneMode.setSummary(Helpers.getValueFromKey("video_scene_mode"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setDefaultEntryIfNotPreviouslySet(ListPreference listPreference) {
        String out = listPreference.getValue();
        if (out == null) {
            listPreference.setValueIndex(0);
        }
    }

    private void setEntriesAndValues(ListPreference listPreference, String[] data) {
        listPreference.setEntries(data);
        listPreference.setEntryValues(data);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        switch (preference.getKey()) {

            case "notifidget":
                if (!Helpers.isWidgetSwitchOn() && !RecordService.isRecording()) {
                    Intent service = new Intent(getActivity(), NotificationService.class);
                    getActivity().startService(service);
                } else {
                    Intent service = new Intent(getActivity(), NotificationService.class);
                    getActivity().stopService(service);
                }
            case "password_key":
                if (!Helpers.isPasswordEnabled()) {
                    // enable code goes here
                } else {
                    // disable code goes here
                }
                break;
            case "video_visibility":
                if (!Helpers.isVideoHiderOn()) {
                    Helpers.hideFilesInDirectory(AppGlobals.DIRECTORY.VIDEOS);
                } else {
                    Helpers.unHideFilesInDirectory(AppGlobals.DIRECTORY.VIDEOS);
                }
                break;
            case "image_visibility":
                if (!Helpers.isImageHiderOn()) {
                    Helpers.hideFilesInDirectory(AppGlobals.DIRECTORY.PICTURES);
                } else {
                    Helpers.unHideFilesInDirectory(AppGlobals.DIRECTORY.PICTURES);
                }
                break;
            case "notification_widget":
                if (!mHelpers.isNotificationWidgetOn()) {
                    NotificationWidget.show(null);
                } else {
                    NotificationWidget.hide();
                    break;
                }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "max_video":
                editTextPreference.setSummary(mHelpers.getValueFromKey(key)+" minutes");
                break;
            case "video_resolution":
                VideoResolution.setSummary(mHelpers.getValueFromKey(key));
                break;
            case "picture_scene_mode":
                pictureSceneMode.setSummary(mHelpers.getValueFromKey(key));
                break;
//            case "video_scene_mode":
//                videoSceneMode.setSummary(mHelpers.getValueFromKey(key));
//                break;
            case "image_resolution":
                imageResolution.setSummary(mHelpers.getValueFromKey(key));
                break;
            case "default_camera":
                defaultCamera.setSummary(mHelpers.getValueFromKey(key));
                resetAllValues();
                loadAndSetUpSettingsFragment();
                break;
        }
    }

    private void resetAllValues() {
        VideoResolution.setValue(null);
        imageResolution.setValue(null);
        pictureSceneMode.setValue(null);
//        videoSceneMode.setValue(null);
        cameraZoomControl.setValue(null);
    }

    private void setUpCameraSelection() {
        mCameraFaces = (ListPreference) findPreference("default_camera");
        mCameraFaces.setOnPreferenceChangeListener(this);
        if (CameraCharacteristics.getNumberOfCameras() == 1) {
            if (CameraCharacteristics.hasFrontCamera()) {
                setEntriesAndValues(
                        mCameraFaces,
                        new String[]{AppConstants.CAMERA_FRONT}
                );
                mCameraFaces.setValueIndex(0);
            } else if (CameraCharacteristics.hasRearCamera()) {
                setEntriesAndValues(
                        mCameraFaces,
                        new String[]{AppConstants.CAMERA_REAR}
                );
                mCameraFaces.setValueIndex(0);
            }
            mCameraFaces.setEnabled(false);
        } else if (CameraCharacteristics.getNumberOfCameras() == 2) {
            setEntriesAndValues(
                    mCameraFaces,
                    new String[]{AppConstants.CAMERA_REAR, AppConstants.CAMERA_FRONT}
            );
            setDefaultEntryIfNotPreviouslySet(mCameraFaces);
        }
    }
}
