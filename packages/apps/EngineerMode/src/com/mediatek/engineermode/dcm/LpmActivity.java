
package com.mediatek.engineermode.dcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mediatek.engineermode.FeatureSupport;
import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.File;
import java.io.IOException;

public class LpmActivity extends Activity implements OnClickListener {

    private static final String TAG = "LCM";
    private static final int RESULT_NUM = 5;
    private static final String LPM_PREF_NAME = "DCM_LPM";
    private static final String PREF_KEY_REF_CLK = "REF_CLK";
    private static final String PREF_KEY_SIGNAL = "SIGNAL";
    private static final String PREF_KEY_STARTED = "STARTED";
    private static final String PREF_KEY_GOOD_DUR_EDIT = "GOOD_DUR_EDIT";
    private static final String PREF_KEY_TOTAL_TIME = "TOTAL_TIME";
    private static final String PREF_KEY_LOW2HIGH = "LOW2HIGH";
    private static final String PREF_KEY_HIGH_DUR = "HIGH_DUR";
    private static final String PREF_KEY_LONGEST_HIGH = "LONGEST_HIGH";
    private static final String PREF_KEY_GOOD_DUR = "GOOD_DUR";
    private static final String FS_HELP = "/proc/lpm/help";
    private static final String FS_DBG = "/proc/lpm/dbg";
    private static final String FS_STA = "/proc/lpm/sta";
    private static final String CAT = "cat ";
    private static final String CMD_START =
            "echo 1 1 %1$d %2$d %3$s > " + FS_DBG;
    private static final String CMD_STOP =
            "echo 1 0 0 0 0 > " + FS_DBG;

    private Spinner mSpnClocks;
    private Spinner mSpnSignals;

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnHelp;

    private EditText mEditGoodDurInput;
    private EditText mEditTotalTime;
    private EditText mEditLow2high;
    private EditText mEditHighDur;
    private EditText mEditLongestHigh;
    private EditText mEditGoodDur;

    private EditText[] mEditResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FeatureSupport.isSupported(FeatureSupport.FK_MTK_WEARABLE_PLATFORM)) {
            setContentView(R.layout.dcm_lpm_w);
        } else {
            setContentView(R.layout.dcm_lpm);
        }

        mSpnClocks = (Spinner) findViewById(R.id.dcm_lpm_ref_clk);
        mSpnSignals = (Spinner) findViewById(R.id.dcm_lpm_signals);
        mBtnStart = (Button) findViewById(R.id.dcm_lpm_start_btn);
        mBtnStart.setOnClickListener(this);
        mBtnStop = (Button) findViewById(R.id.dcm_lpm_stop_btn);
        mBtnStop.setOnClickListener(this);
        mBtnHelp = (Button) findViewById(R.id.dcm_lpm_help_btn);
        mBtnHelp.setOnClickListener(this);

        mEditGoodDurInput = (EditText) findViewById(R.id.dcm_lpm_good_dur_input);
        mEditTotalTime = (EditText) findViewById(R.id.dcm_lpm_total_time_edit);
        mEditLow2high = (EditText) findViewById(R.id.dcm_lpm_low2high_edit);
        mEditHighDur = (EditText) findViewById(R.id.dcm_lpm_high_dur_edit);
        mEditLongestHigh = (EditText) findViewById(R.id.dcm_lpm_longest_high_edit);
        mEditGoodDur = (EditText) findViewById(R.id.dcm_lpm_good_dur_edit);

        mEditResults = new EditText[RESULT_NUM];
        mEditResults[0] = mEditTotalTime;
        mEditResults[1] = mEditLow2high;
        mEditResults[2] = mEditHighDur;
        mEditResults[3] = mEditLongestHigh;
        mEditResults[4] = mEditGoodDur;

        if (!new File(FS_DBG).exists()) {
            Toast.makeText(this, "Don't Support This Feature!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        restoreUIState();
    }

    private void handleClickStartBtn() {
        int refClk;
        int signal;
        String msg;
        String goodDur;
        long goodDurVal;
        if (TextUtils.isEmpty(mEditGoodDurInput.getText())) {
            msg = getString(R.string.dcm_lpm_invalid_good_dur);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        goodDur = mEditGoodDurInput.getText().toString();
        goodDurVal = parseLongStr(goodDur, 16);
        if (goodDurVal < 0 || goodDurVal > 0xffff) {
            msg = getString(R.string.dcm_lpm_invalid_good_dur);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        refClk = mSpnClocks.getSelectedItemPosition();
        signal = mSpnSignals.getSelectedItemPosition();
        String cmd;
        cmd = String.format(CMD_START, refClk, signal, goodDur);
        execCommand(cmd);
        checkStatus(true);
    }

    private void restoreUIState() {
        SharedPreferences pref = getSharedPreferences(LPM_PREF_NAME, MODE_PRIVATE);
        mSpnClocks.setSelection(pref.getInt(PREF_KEY_REF_CLK, 0));
        mSpnSignals.setSelection(pref.getInt(PREF_KEY_SIGNAL, 0));
        if (pref.getBoolean(PREF_KEY_STARTED, true)) {
            mBtnStart.setEnabled(true);
            mBtnStop.setEnabled(false);
        } else {
            mBtnStart.setEnabled(false);
            mBtnStop.setEnabled(true);
        }
        mEditGoodDurInput.setText(pref.getString(PREF_KEY_GOOD_DUR_EDIT, ""));
        mEditTotalTime.setText(pref.getString(PREF_KEY_TOTAL_TIME, ""));
        mEditLow2high.setText(pref.getString(PREF_KEY_LOW2HIGH, ""));
        mEditHighDur.setText(pref.getString(PREF_KEY_HIGH_DUR, ""));
        mEditLongestHigh.setText(pref.getString(PREF_KEY_LONGEST_HIGH, ""));
        mEditGoodDur.setText(pref.getString(PREF_KEY_GOOD_DUR, ""));
    }

    private void backupUIState() {
        SharedPreferences pref = getSharedPreferences(LPM_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_KEY_REF_CLK, mSpnClocks.getSelectedItemPosition());
        editor.putInt(PREF_KEY_SIGNAL, mSpnSignals.getSelectedItemPosition());
        if (mBtnStart.isEnabled()) {
            editor.putBoolean(PREF_KEY_STARTED, true);
        } else {
            editor.putBoolean(PREF_KEY_STARTED, false);
        }
        editor.putString(PREF_KEY_GOOD_DUR_EDIT, mEditGoodDurInput.getText().toString());
        editor.putString(PREF_KEY_TOTAL_TIME, mEditTotalTime.getText().toString());
        editor.putString(PREF_KEY_LOW2HIGH, mEditLow2high.getText().toString());
        editor.putString(PREF_KEY_HIGH_DUR, mEditHighDur.getText().toString());
        editor.putString(PREF_KEY_LONGEST_HIGH, mEditLongestHigh.getText().toString());
        editor.putString(PREF_KEY_GOOD_DUR, mEditGoodDur.getText().toString());
        editor.commit();
    }

    private boolean checkStatus(boolean isStart) {
        String msg;
        String cmd;
        String output;
        cmd = CAT + FS_STA;
        output = execCommand(cmd);
        // temp code
        //output = "status =0 ";
        output = output.trim();
        String status;
        status = output.split(" *= *")[1];
        if ("0".equals(status)) {
            if (isStart) {
                mBtnStart.setEnabled(false);
                mBtnStop.setEnabled(true);
                msg = getString(R.string.dcm_lpm_start);
            } else {
                mBtnStart.setEnabled(true);
                mBtnStop.setEnabled(false);
                msg = getString(R.string.dcm_lpm_stop);
            }
            msg = msg + " " + getString(R.string.dcm_operate_success);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            msg = getString(R.string.dcm_lpm_operate_error_code) + status;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private long parseLongStr(String longStr, int radix) {
        long longVal = -1;
        try {
            longVal = Long.parseLong(longStr, radix);
        } catch (NumberFormatException e) {
            return -1;
        }
        return longVal;
    }

    private void handleClickStopBtn() {
        String cmd;
        cmd = CMD_STOP;
        execCommand(cmd);
        if (checkStatus(false)) {
            String output;
            cmd = CAT + FS_DBG;
            output = execCommand(cmd);
            // temp code
            //output = "0x123e , 0x12345678 , 0x66ac , Overflow , 0x12de,";
            output = output.trim();
            String[] results = output.split(" *, *");
            for (int i = 0;  i < RESULT_NUM; i++) {
                mEditResults[i].setText(results[i]);
            }
        }
    }

    @Override
    public void onClick(View view) {
        String cmd;
        String output;

        int idx = view.getId();
        switch(idx) {
        case R.id.dcm_lpm_start_btn:
            handleClickStartBtn();
            break;
        case R.id.dcm_lpm_stop_btn:
            handleClickStopBtn();
            break;
        case R.id.dcm_lpm_help_btn:
            cmd = CAT + FS_HELP;
            output = execCommand(cmd);
            //Toast.makeText(this, R.string.dcm_text_help, Toast.LENGTH_LONG).show();
            showDialog(getString(R.string.dcm_text_help), output);
            break;
        default:
            Log.w("@M_" + TAG, "onClick() Unknown view id: " + idx);
            break;
        }
    }

    @Override
    protected void onDestroy() {
        backupUIState();
        super.onDestroy();
    }

    private String execCommand(String cmd) {
         int ret = -1;
         Log.d("@M_" + TAG, "[cmd]:" + cmd);
         //Toast.makeText(this, cmd, Toast.LENGTH_LONG).show();
         try {
             ret = ShellExe.execCommand(cmd);
         } catch (IOException e) {
             Log.e("@M_" + TAG, "IOException: " + e.getMessage());
         }
         if (ret == 0) {
             String outStr = ShellExe.getOutput();
             Log.d("@M_" + TAG, "[output]: " + outStr);
             return outStr;
         }
         return null;
     }

    private void showDialog(String title, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this).setCancelable(
               false).setTitle(title).setMessage(msg).
               setPositiveButton(android.R.string.ok, null).create();
        dialog.show();
    }

}
