
package com.mediatek.engineermode.deepidle;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

public class DeepIdleSettingActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {

    private static final String TAG = "DeepIdle";
    private static final String FS_DPIDLE_MODE = "/proc/spm_fs/dpidle_mode";
    private static final String FS_DPIDLE_LEVEL = "/proc/spm_fs/dpidle_level";
    private static final String FS_DPIDLE_TIMERVAL = "/proc/spm_fs/dpidle_timer";
    private static final String FS_DPIDLE = "/proc/spm_fs/dpidle";
    private static final String CAT = "cat ";
    private static final String CMD_CPU_PDN =
            "echo \"%1$d cpu_pdn\" > " + FS_DPIDLE;
    private static final String CMD_POWER_LEVEL =
            "echo \"%1$d pwrlevel\" > " + FS_DPIDLE;
    private static final String CMD_TIMER_VAL =
            "echo \"%1$s timer_val_ms\" > " + FS_DPIDLE;

    private RadioButton mRBDisableDpIdl;
    private RadioButton mRBLegacySleep;
    private RadioButton mRBDormantMode;
    private RadioButton mRBPowerLevel0;
    private RadioButton mRBPowerLevel1;
    private RadioButton mRBDisableTimer;
    private RadioButton mRBTimerValSet;
    private RadioButton[] mRBModes = new RadioButton[3];
    private RadioButton[] mRBLevels = new RadioButton[2];
    private RadioButton[] mRBTimerVals = new RadioButton[2];

    private EditText mEditTimerVal;
    private Button mBtnStartTimer;
    private Button mBtnGetSetting;
    private boolean mInitDone;
    private LinearLayout mLevelControler;
    private LinearLayout mSetTimerControler;
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_idle_setting);

        mRBDisableDpIdl = (RadioButton) findViewById(R.id.deep_idle_disable);
        mRBDisableDpIdl.setOnCheckedChangeListener(this);
        mRBLegacySleep = (RadioButton) findViewById(R.id.deep_idle_legacy_sleep);
        mRBLegacySleep.setOnCheckedChangeListener(this);
        mRBDormantMode = (RadioButton) findViewById(R.id.deep_idle_dormant_mode);
        mRBDormantMode.setOnCheckedChangeListener(this);
        mRBModes[0] = mRBDisableDpIdl;
        mRBModes[1] = mRBLegacySleep;
        mRBModes[2] = mRBDormantMode;

        mRBPowerLevel0 = (RadioButton) findViewById(R.id.deep_idle_power_level0);
        mRBPowerLevel0.setOnCheckedChangeListener(this);
        mRBPowerLevel1 = (RadioButton) findViewById(R.id.deep_idle_power_level1);
        mRBPowerLevel1.setOnCheckedChangeListener(this);
        mRBLevels[0] = mRBPowerLevel0;
        mRBLevels[1] = mRBPowerLevel1;

        mRBDisableTimer = (RadioButton) findViewById(R.id.deep_idle_timer_disable);
        mRBDisableTimer.setOnCheckedChangeListener(this);
        mRBTimerValSet = (RadioButton) findViewById(R.id.mcdi_timer_val_set);
        mRBTimerValSet.setOnCheckedChangeListener(this);
        mRBTimerVals[0] = mRBDisableTimer;
        mRBTimerVals[1] = mRBTimerValSet;

        mEditTimerVal = (EditText) findViewById(R.id.deep_idle_timer_val);
        mBtnStartTimer = (Button) findViewById(R.id.deep_idle_start_timer);
        mBtnStartTimer.setOnClickListener(this);
        mBtnGetSetting = (Button) findViewById(R.id.deep_idle_get_setting);
        mBtnGetSetting.setOnClickListener(this);
        mLevelControler = (LinearLayout) findViewById(R.id.deep_idle_power_level_contrl);
        mSetTimerControler = (LinearLayout) findViewById(R.id.deep_idle_wake_timer_contrl);

        mInitDone = false;
        initUIByData();
        mInitDone = true;
    }

    private void initUIByData() {
        String cmd;
        String output;

        cmd = CAT + FS_DPIDLE_MODE;
        output = execCommand(cmd);
        //output = "1 \n";
        if (output == null) {
            Toast.makeText(this, "Feature Fail or Don't Support!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        output = output.trim();
        int modeIdx = -1;
        try {
            modeIdx = Integer.parseInt(output);
        } catch (NumberFormatException e) {
            Log.e("@M_" + TAG, "NumberFormatException invalid output:" + output);
        }
        try {
            mRBModes[modeIdx].setChecked(true);
        } catch (IndexOutOfBoundsException e) {
            Log.e("@M_" + TAG, "Fail to set Default Mode; IndexOutOfBoundsException: " + e.getMessage());
        }

        cmd = CAT + FS_DPIDLE_LEVEL;
        output = execCommand(cmd);
        //output = "0 ";
        if (output == null) {
            Toast.makeText(this, "Feature Fail or Don't Support!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        output = output.trim();
        int levelIdx = -1;
        try {
            levelIdx = Integer.parseInt(output);
        } catch (NumberFormatException e) {
            Log.e("@M_" + TAG, "NumberFormatException invalid output:" + output);
        }
        try {
            mRBLevels[levelIdx].setChecked(true);
        } catch (IndexOutOfBoundsException e) {
            Log.e("@M_" + TAG, "Fail to set Default Level; IndexOutOfBoundsException: " + e.getMessage());
        }

        cmd = CAT + FS_DPIDLE_TIMERVAL;
        output = execCommand(cmd);
        //output = " 100 ";
        if (output == null) {
            Toast.makeText(this, "Feature Fail or Don't Support!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        output = output.trim();
        Log.d("@M_" + TAG, "timer val output: " + output);
        int timerVal = -1;
        try {
            timerVal = Integer.parseInt(output);
        } catch (NumberFormatException e) {
            Log.e("@M_" + TAG, "NumberFormatException invalid output:" + output);
        }
        if (timerVal == 0) {
            mRBDisableTimer.setChecked(true);
        } else if (timerVal > 15 && timerVal < 1000000) {
            mRBTimerValSet.setChecked(true);
            mEditTimerVal.setText(output);
        } else {
            Log.e("@M_" + TAG, "Invalid Timer Value:" + timerVal);
        }
    }

    private void enableTimerValUI(boolean enabled) {
        mEditTimerVal.setEnabled(enabled);
        mBtnStartTimer.setEnabled(enabled);
    }

    private void setCpuPdn(int input) {
        String cmd;
        cmd = String.format(CMD_CPU_PDN, input);
        execCommand(cmd);
    }

    private void setPowerLevel(int input) {
        String cmd;
        cmd = String.format(CMD_POWER_LEVEL, input);
        execCommand(cmd);
    }

    private void setTimerVal(String input) {
        String cmd;
        cmd = String.format(CMD_TIMER_VAL, input);
        execCommand(cmd);
    }

    private String execCommand(String cmd) {
         int ret = -1;
         Log.d("@M_" + TAG, "[cmd]:" + cmd);
         //Toast.makeText(this, cmd, Toast.LENGTH_SHORT).show();
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
               false).setTitle(title).setMessage(msg).setCancelable(true).
               setPositiveButton(android.R.string.ok, null).create();
        dialog.show();
    }

    private void checkOneRadio(RadioButton[] array, RadioButton target, boolean checked) {
         for (int i = 0; i < array.length; i++) {
             if (target == array[i]) {
                 array[i].setChecked(checked);
             } else {
                 array[i].setChecked(!checked);
             }
         }
     }

//  private void enableLevelSetTimerUI(boolean enabled) {
//      int i;
//      for (i = 0; i < mRBLevels.length; i++) {
//          mRBLevels[i].setEnabled(enabled);
//      }
//      for (i = 0; i < mRBTimerVals.length; i++) {
//          mRBTimerVals[i].setEnabled(enabled);
//      }
//  }

    private void visibleLevelSetTimerUI(boolean visible) {
        int visibility;
        if (visible) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        mLevelControler.setVisibility(visibility);
        mSetTimerControler.setVisibility(visibility);
        mBtnStartTimer.setVisibility(visibility);
    }

    private boolean validateInputData() {
        String msg;
        if (TextUtils.isEmpty(mEditTimerVal.getText())) {
            msg = getString(R.string.deep_idle_invalid_timer_val);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        int timerVal = -1;
        String timerValStr = mEditTimerVal.getText().toString();
        try {
            timerVal = Integer.parseInt(timerValStr);
        } catch (NumberFormatException e) {
            msg = getString(R.string.deep_idle_invalid_timer_val);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (timerVal < 15 || timerVal > 1000000) {
            msg = getString(R.string.deep_idle_invalid_timer_val);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String cmd;
        String output;
        switch(id) {
        case R.id.deep_idle_start_timer:
            if (validateInputData()) {
                setTimerVal(mEditTimerVal.getText().toString());
            }
            break;
        case R.id.deep_idle_get_setting:
            cmd = CAT + FS_DPIDLE;
            output = execCommand(cmd);
            showDialog(getString(R.string.deep_idle_setting), output);
            break;
        default:
            Log.w("@M_" + TAG, "unknown view id: " + id);
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton btnView, boolean isChecked) {
        int id = btnView.getId();
        switch(id) {
        case R.id.deep_idle_disable:
            if (isChecked) {
                //enableLevelSetTimerUI(false);
                visibleLevelSetTimerUI(false);
                setCpuPdn(0);
            }
            break;
        case R.id.deep_idle_legacy_sleep:
            if (isChecked) {
                //enableLevelSetTimerUI(true);
                visibleLevelSetTimerUI(true);
                setCpuPdn(1);
            }
            break;
        case R.id.deep_idle_dormant_mode:
            if (isChecked) {
                //enableLevelSetTimerUI(true);
                visibleLevelSetTimerUI(true);
                setCpuPdn(2);
            }
            break;
        case R.id.deep_idle_power_level0:
            if (isChecked) {
                setPowerLevel(0);
            }
            break;
        case R.id.deep_idle_power_level1:
            if (isChecked) {
                setPowerLevel(1);
            }
            break;
        case R.id.deep_idle_timer_disable:
            if (isChecked) {
                Log.d("@M_" + TAG, "[debug]onCheckedChanged: deepIdle timer disable");
                enableTimerValUI(false);
                checkOneRadio(mRBTimerVals, mRBDisableTimer, true);
                setTimerVal("0");
            }
            break;
        case R.id.mcdi_timer_val_set:
            if (isChecked) {
                enableTimerValUI(true);
                checkOneRadio(mRBTimerVals, mRBTimerValSet, true);
            }
            break;
        default:
            Log.w("@M_" + TAG, "Unknown CompoundButton id: " + id);
            break;
        }
    }
}
