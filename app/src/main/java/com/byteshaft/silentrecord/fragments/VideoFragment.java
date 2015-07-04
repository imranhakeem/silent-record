package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.Helpers;

public class VideoFragment extends Fragment {

    private Helpers mHelpers;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.video_fragment, container, false);
       mHelpers = new Helpers(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.video_list);
        ArrayList<String> arrayList = mHelpers.getNameFromFolder();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.
                simple_list_item_1, arrayList);
        System.out.println(arrayList.toString());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        return rootView;
    }
}
