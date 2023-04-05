
package com.mediatek.engineermode.memory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.mediatek.engineermode.R;

public class EmiRegister extends Activity {

    private static final String TAG = "EM/Memory_EMI";
    private static final int DIALOG_ID_NOTICE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog(DIALOG_ID_NOTICE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        if (DIALOG_ID_NOTICE == id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.memory_dialog_title);
            builder.setMessage(getString(R.string.memory_dialog_message));
            builder.setPositiveButton(R.string.memory_dialog_ok,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            EmiRegister.this.finish();
                        }
                    });
            dialog = builder.create();
        } else {
            Log.w("@M_" + TAG, "unknown dialog ID");
        }
        return dialog;
    }

}
