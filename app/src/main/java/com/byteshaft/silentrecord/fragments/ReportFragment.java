package com.byteshaft.silentrecord.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;

public class ReportFragment extends Fragment {

    EditText subjectEditText;
    EditText descriptionEditText;
    Button attachButton;
    Button sendButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View reportView = inflater.inflate(R.layout.report_fragment, container, false);
        subjectEditText = (EditText) reportView.findViewById(R.id.editText_report_subject);
        descriptionEditText = (EditText) reportView.findViewById(R.id.editText_report_description);
        attachButton = (Button) reportView.findViewById(R.id.button_attach_report);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sendButton = (Button) reportView.findViewById(R.id.button_send_report);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subjectEditText.getText().toString().trim().length() == 0 ||
                        descriptionEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(AppGlobals.getContext(), "One or more fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppGlobals.getContext(), "Issue Reported", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return reportView;
    }

    @Override
    public void onPause() {
        super.onPause();
        AppGlobals.setIsUnlocked(true);
    }
}
