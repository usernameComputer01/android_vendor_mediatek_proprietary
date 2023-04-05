
package com.mediatek.engineermode.settingsfontsize;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;


public class SettingsFontSize extends Activity {

    private static final String TAG = "EM_SetFontSize";

    private static final String SMALL = "settings_fontsize_small";
    private static final String LARGE = "settings_fontsize_large";
    private static final String EXTRALARGE = "settings_fontsize_extralarge";

    private EditText mEditSmall;
    private EditText mEditLarge;
    private EditText mEditExtraLarge;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fontsize);

        mEditSmall = (EditText) findViewById(R.id.settings_fs_small_edit);
        mEditLarge = (EditText) findViewById(R.id.settings_fs_large_edit);
        mEditExtraLarge = (EditText) findViewById(R.id.settings_fs_extralarge_edit);
        mButton = (Button) findViewById(R.id.settings_fs_ok);

        float readSmall = Settings.System.getFloat(getContentResolver(), SMALL,
                -1);
        float readLarge = Settings.System.getFloat(getContentResolver(), LARGE,
                -1);
        float readExtra = Settings.System.getFloat(getContentResolver(),
                EXTRALARGE, -1);

        if (readSmall != -1) {
            mEditSmall.setText(readSmall + "");
        }
        if (readLarge != -1) {
            mEditLarge.setText(readLarge + "");
        }
        if (readExtra != -1) {
            mEditExtraLarge.setText(readExtra + "");
        }

        mEditSmall.setSelection(mEditSmall.getText().length());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float smallValue = 1;
                float largeValue = 1;
                float extraLargeValue = 1;
                String small = mEditSmall.getText().toString().trim();
                String large = mEditLarge.getText().toString().trim();
                String extralarge = mEditExtraLarge.getText().toString().trim();

                if (small.equals("") && large.equals("")
                        && extralarge.equals("")) {
                    return;
                }

                if (!small.equals("")) {
                    try {
                        smallValue = Float.valueOf(small);
                        Elog.e(TAG, "small : " + small);
                    } catch (NumberFormatException e) {
                        // Dialog
                        new AlertDialog.Builder(SettingsFontSize.this)
                                .setTitle("Warning").setMessage(
                                        "Please input right value!")
                                .setPositiveButton("OK", null).create().show();
                        Elog.e(TAG, "--NumberFormatException");
                        return;
                    }
                }
                if (!large.equals("")) {
                    try {
                        largeValue = Float.valueOf(large);
                    } catch (NumberFormatException e) {
                        // Dialog
                        new AlertDialog.Builder(SettingsFontSize.this)
                                .setTitle("Warning").setMessage(
                                        "Please input right value!")
                                .setPositiveButton("OK", null).create().show();
                        Elog.e(TAG, "--NumberFormatException");
                        return;
                    }
                }
                if (!extralarge.equals("")) {
                    try {
                        extraLargeValue = Float.valueOf(extralarge);
                    } catch (NumberFormatException e) {
                        // Dialog
                        new AlertDialog.Builder(SettingsFontSize.this)
                                .setTitle("Warning").setMessage(
                                        "Please input right value!")
                                .setPositiveButton("OK", null).create().show();
                        Elog.e(TAG, "--NumberFormatException");
                        return;
                    }
                }

                if (smallValue <= 0 || largeValue <= 0 || extraLargeValue <= 0) {
                    new AlertDialog.Builder(SettingsFontSize.this).setTitle(
                            "Warning").setMessage("Please input right value!")
                            .setPositiveButton("OK", null).create().show();
                    return;
                }

                boolean isSuccessSm;
                boolean isSuccessLa;
                boolean isSuccessEx;
                if (!small.equals("")) {
                    isSuccessSm = Settings.System.putFloat(
                            getContentResolver(), SMALL, smallValue);
                } else {
                    isSuccessSm = true;
                }
                if (!large.equals("")) {
                    isSuccessLa = Settings.System.putFloat(
                            getContentResolver(), LARGE, largeValue);
                } else {
                    isSuccessLa = true;
                }
                if (!extralarge.equals("")) {
                    isSuccessEx = Settings.System.putFloat(
                            getContentResolver(), EXTRALARGE, extraLargeValue);
                } else {
                    isSuccessEx = true;
                }

                Elog.d(TAG, " Set font size -- SMALL :" + smallValue
                        + "LARGE :" + largeValue + "EXTRALARGE :"
                        + extraLargeValue);

                if (isSuccessSm && isSuccessLa && isSuccessEx) {
                    Toast.makeText(SettingsFontSize.this, "Success!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsFontSize.this, "Fail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
