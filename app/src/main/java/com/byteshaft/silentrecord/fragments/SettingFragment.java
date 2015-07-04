package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.Helpers;
import com.github.machinarius.preferencefragment.PreferenceFragment;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    ListPreference listPreference;
    SwitchPreference switchPreference;
    Helpers helpers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        CameraCharacteristics characteristics = new CameraCharacteristics(
                getActivity().getApplicationContext());

        helpers = new Helpers(getActivity());

        switchPreference = (SwitchPreference) findPreference("video_visibility");
        switchPreference.setOnPreferenceChangeListener(this);
        switchPreference = (SwitchPreference) findPreference("image_visibility");
        switchPreference.setOnPreferenceChangeListener(this);

        listPreference = (ListPreference) findPreference("video_resolution");
        setEntriesAndValues(listPreference, characteristics.getSupportedVideoResolutions());
        setDefaultEntryIfNotPreviouslySet(listPreference);

        listPreference = (ListPreference) findPreference("picture_scene_mode");
        setEntriesAndValues(listPreference, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(listPreference);

        listPreference = (ListPreference) findPreference("video_scene_mode");
        setEntriesAndValues(listPreference, characteristics.getSupportedSceneModes());
        setDefaultEntryIfNotPreviouslySet(listPreference);

        listPreference = (ListPreference) findPreference("image_resolution");
        setEntriesAndValues(listPreference, characteristics.getSupportedPictureSizes());
        setDefaultEntryIfNotPreviouslySet(listPreference);

        listPreference = (ListPreference) findPreference("camera_zoom_control");
        listPreference.setEntryValues(characteristics.getSupportedZoomLevels());
        setDefaultEntryIfNotPreviouslySet(listPreference);
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
                if (!helpers.isVideoHiderOn()) {
                    helpers.hideVideoFiles();
                } else {
                    helpers.unhideFiles();
                    System.out.println("off");
                }
                break;
            case "image_visibility":
                if (!helpers.isImageHiderOn()) {
                    System.out.println("on");
                } else {
                    System.out.println("off");
                }
        }
        return true;
    }
}
