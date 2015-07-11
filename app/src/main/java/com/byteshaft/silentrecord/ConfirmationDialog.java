package com.byteshaft.silentrecord;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.byteshaft.silentrecord.services.RecordService;


public class ConfirmationDialog extends Activity {
    AlertDialog.Builder alertDialog;
    private static boolean dialogPresent = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (dialogPresent) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            dialogPresent = false;
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_dialog);
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Stop Recording");
        alertDialog.setMessage("Do you really want to stop the recording ?");
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogPresent = true;
                Intent intent = new Intent(getApplicationContext(), RecordService.class);
                if (RecordService.isRecording()) {
                    AppGlobals.getContext().stopService(intent);
                    dialogInterface.dismiss();
                    finish();
                }
            }
        });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogPresent = true;
                    dialogInterface.dismiss();
                    finish();
                }
            });
            alertDialog.show();
        }
}
