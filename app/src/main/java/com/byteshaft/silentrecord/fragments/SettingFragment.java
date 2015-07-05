package com.byteshaft.silentrecord.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.notification.NotificationWidget;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.Helpers;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.List;

public class SettingFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Helpers mHelpers;
    private SwitchPreference notificationSwitch;
    private EditTextPreference editTextPreference;
    private ListPreference VideoResolution;
    private ListPreference videoFormat;
    private ListPreference pictureSceneMode;
    private ListPreference videoSceneMode;
    private ListPreference imageResolution;
    private ListPreference cameraZoomControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        CameraCharacteristics characteristics = new CameraCharacteristics(
                getActivity().getApplicationContext());
        mHelpers = new Helpers(getActivity());

        SwitchPreference videoSwitch = (SwitchPreference) findPreference("video_visibility");
        videoSwitch.setOnPreferenceChangeListener(this);
        SwitchPreference imageSwitch = (SwitchPreference) findPreference("image_visibility");
        imageSwitch.setOnPreferenceChangeListener(this);
        SwitchPreference notificationSwitch = (SwitchPreference) findPreference("notification_widget");
        notificationSwitch.setOnPreferenceChangeListener(this);

        VideoResolution = (ListPreference) findPreference("video_resolution");
        setEntriesAndValues(VideoResolution, characteristics.getSupportedVideoResolutions());
        setDefaultEntryIfNotPreviouslySet(VideoResolution);
        VideoResolution.setSummary(mHelpers.getValueFromKey("video_resolution"));

        videoFormat = (ListPreference) findPreference("video_format");
        videoFormat.setSummary(mHelpers.getValueFromKey("video_format"));

        editTextPreference = (EditTextPreference) findPreference("max_video");
        editTextPreference.setSummary(mHelpers.getValueFromKey("max_video")+" minutes");

        pictureSceneMode = (ListPreference) findPreference("picture_scene_mode");
        setEntriesAndValues(pictureSceneMode, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(pictureSceneMode);

        videoSceneMode = (ListPreference) findPreference("video_scene_mode");
        setEntriesAndValues(videoSceneMode, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(videoSceneMode);
        videoSceneMode.setSummary(mHelpers.getValueFromKey("video_scene_mode"));

        imageResolution = (ListPreference) findPreference("image_resolution");
        setEntriesAndValues(imageResolution, characteristics.getSupportedPictureSizes());
        setDefaultEntryIfNotPreviouslySet(imageResolution);
        imageResolution.setSummary(mHelpers.getValueFromKey("image_resolution"));

        cameraZoomControl = (ListPreference) findPreference("camera_zoom_control");
        cameraZoomControl.setEntryValues(characteristics.getSupportedZoomLevels());
        setDefaultEntryIfNotPreviouslySet(cameraZoomControl);
        cameraZoomControl.setSummary(mHelpers.getValueFromKey("camera_zoom_control"));
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        editTextPreference.setSummary(mHelpers.getValueFromKey("max_video")+" minutes");
        VideoResolution.setSummary(mHelpers.getValueFromKey("video_resolution"));
        videoFormat.setSummary(mHelpers.getValueFromKey("video_format"));
        pictureSceneMode.setSummary(mHelpers.getValueFromKey("picture_scene_mode"));
        videoSceneMode.setSummary(mHelpers.getValueFromKey("video_scene_mode"));
        imageResolution.setSummary(mHelpers.getValueFromKey("image_resolution"));
        cameraZoomControl.setSummary(mHelpers.getValueFromKey("camera_zoom_control"));
    }
}

