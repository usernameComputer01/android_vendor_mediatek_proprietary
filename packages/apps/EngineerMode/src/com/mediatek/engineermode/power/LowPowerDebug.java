

package com.mediatek.engineermode.power;

import android.app.Activity;
//import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.FeatureSupport;
import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.IOException;

public class LowPowerDebug extends Activity {
    private static final String TAG = "EM/LowPowerDebug";
    private static final String[] STOP_STRS_ARRAY = {"0", "1"};
    private static final String[] UT_STRS_ARRAY = {"0", "1", "2"};

    private static final String LP_PROTECT_STOP = "/sys/devices/platform/mt-pmic/low_battery_protect_stop";
    private static final String LP_PROTECT_UT = "/sys/devices/platform/mt-pmic/low_battery_protect_ut";

    private Spinner mStopSpinner;
    private Spinner mUtSpinner;

    private final OnItemSelectedListener mSpinnerListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            String cmd = null;
            switch (arg0.getId()) {
                case R.id.low_power_stop_select :
                    cmd = "echo " + arg2 + " > " + LP_PROTECT_STOP;
                    break;
                case R.id.low_power_ut_select :
                    cmd = "echo " + arg2 + " > " + LP_PROTECT_UT;
                    break;
                default:
                    break;
            }
            if (cmd != null) {
                if (setCommand(cmd) != 0) {
                    Toast.makeText(LowPowerDebug.this, "Set failed!", Toast.LENGTH_LONG)
                        .show();
                }
            }
        }
        public void onNothingSelected(AdapterView<?> arg0) {
            Elog.v(TAG, "Spinner nothing selected");
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.low_power_debug);
        if (!FeatureSupport.isSupportedEmSrv()) {
            Toast.makeText(this, R.string.notice_wo_emsvr,
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mStopSpinner = (Spinner) findViewById(R.id.low_power_stop_select);
        mUtSpinner = (Spinner) findViewById(R.id.low_power_ut_select);
        mStopSpinner.setOnItemSelectedListener(mSpinnerListener);
        mUtSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSpinner();
    }

    private void initSpinner() {
        String cmd = "cat " + LP_PROTECT_STOP;
        String result = getCommand(cmd);
        boolean getResult = true;
        if (result != null) {
            int index = Integer.valueOf(result);
            mStopSpinner.setSelection(index);
        } else {
            getResult = false;
        }
        cmd = "cat " + LP_PROTECT_UT;
        result = getCommand(cmd);
        if (result != null) {
            int index = Integer.valueOf(result);
            mUtSpinner.setSelection(index);
        } else {
            getResult = false;
        }
        if (getResult == false) {
            Toast.makeText(this, "Get status failed!", Toast.LENGTH_LONG)
                        .show();
        }
    }

    private String getCommand(String cmd) {
         int ret = -1;
         Elog.v(TAG, "[cmd]:" + cmd);
         try {
             ret = ShellExe.execCommand(cmd);
         } catch (IOException e) {
             Elog.e(TAG, "IOException: " + e.getMessage());
         }
         if (ret == 0) {
             String outStr = ShellExe.getOutput();
             Elog.d(TAG, "[output]: " + outStr);
             return outStr;
         }
         return null;
     }

    private int setCommand(String cmd) {
        int ret = -1;
        Elog.v(TAG, "[cmd]:" + cmd);
        try {
            ret = ShellExe.execCommand(cmd);
        } catch (IOException e) {
            Elog.e(TAG, "IOException: " + e.getMessage());
        }
        return ret;
    }
}
