package com.byteshaft.silentrecord.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;

public class ReportFragment extends Fragment {

    EditText subjectEditText;
    EditText descriptionEditText;
    Button attachButton;
    Button sendButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppGlobals.setIsMainActivityShown(false);
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
        Button attachButton = (Button) reportView.findViewById(R.id.button_attach_report);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("video/*, images/*");
                startActivityForResult(i, 0);
            }
        });
        return reportView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            Uri contactUri = data.getData();
            String file = getRealPathFromURI(getActivity(), contactUri);
            Toast.makeText(getActivity(), "Selected " + new File(file).getName(), Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppGlobals.setIsUnlocked(true);
    }
}
