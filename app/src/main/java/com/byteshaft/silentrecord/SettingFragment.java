package com.byteshaft.silentrecord;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;


public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
