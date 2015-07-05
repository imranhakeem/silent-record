package com.byteshaft.silentrecord.fragments;

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

public class SettingFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private Helpers mHelpers;

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

        ListPreference listPreference = (ListPreference) findPreference("video_resolution");
        setEntriesAndValues(listPreference, characteristics.getSupportedVideoResolutions());
        setDefaultEntryIfNotPreviouslySet(listPreference);
        listPreference.setSummary(mHelpers.getValueFromKey("video_resolution"));

        listPreference = (ListPreference) findPreference("video_format");
        setDefaultEntryIfNotPreviouslySet(listPreference);
        listPreference.setSummary(mHelpers.getValueFromKey("video_format"));

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("max_video");
        editTextPreference.setSummary(mHelpers.getValueFromKey("max_video"));

        listPreference = (ListPreference) findPreference("picture_scene_mode");
        setEntriesAndValues(listPreference, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(listPreference);

        listPreference = (ListPreference) findPreference("video_scene_mode");
        setEntriesAndValues(listPreference, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(listPreference);
        listPreference.setSummary(mHelpers.getValueFromKey("video_scene_mode"));

        listPreference = (ListPreference) findPreference("image_resolution");
        setEntriesAndValues(listPreference, characteristics.getSupportedPictureSizes());
        setDefaultEntryIfNotPreviouslySet(listPreference);
        listPreference.setSummary(mHelpers.getValueFromKey("image_resolution"));

        listPreference = (ListPreference) findPreference("camera_zoom_control");
        listPreference.setEntryValues(characteristics.getSupportedZoomLevels());
        setDefaultEntryIfNotPreviouslySet(listPreference);
        listPreference.setSummary(mHelpers.getValueFromKey("camera_zoom_control"));
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
}

