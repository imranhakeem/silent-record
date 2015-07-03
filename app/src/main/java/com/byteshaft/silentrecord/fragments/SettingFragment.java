package com.byteshaft.silentrecord.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
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

        Preference reset = findPreference("reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Are you sure?");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences shared = PreferenceManager.
                                getDefaultSharedPreferences(getActivity());
                        shared.edit().putString("camera_zoom_control", "0").apply();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return false;
            }
        });

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
//                    hideDirectory();
                } else {
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

//    private void hideDirectory() {
//        File sdcard = Environment.getExternalStorageDirectory();
//        File from = new File(sdcard, "SpyVideos");
//        File to = new File(sdcard, ".SpyVideos");
//        boolean success = from.renameTo(to);
//        if (success) {
//            Toast.makeText(getActivity().getApplicationContext(), "Hidden", Toast.LENGTH_SHORT).show();
//        }
//    }
}
