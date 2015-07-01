package com.byteshaft.silentrecord;

import android.os.Bundle;
import android.preference.ListPreference;


import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.github.machinarius.preferencefragment.PreferenceFragment;

public class SettingFragment extends PreferenceFragment {

    ListPreference listPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        CameraCharacteristics characteristics = new CameraCharacteristics(
                getActivity().getApplicationContext());

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
}
