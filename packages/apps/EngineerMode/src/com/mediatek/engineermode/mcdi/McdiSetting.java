
package com.mediatek.engineermode.mcdi;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
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

public class McdiSetting extends Activity implements OnCheckedChangeListener, OnClickListener {

    private static final String TAG = "McdiSetting";
    private static final String MODE_FS = "/proc/spm_fs/mcdi_mode";
    private static final String TIMER_FS = "/proc/spm_fs/mcdi_timer";
    private static final String SETTING_FS = "/proc/spm_fs/mcdi";
    private static final String TIMER_VAL_TAG = "timer_val_ms";
    private static final String MCDI_MODE_TAG = "mcdi_mode";
    private static final String CAT = "cat";
    private static final String ECHO = "echo";
    private RadioButton mRBDisableMcdi;
    private RadioButton mRBMcdiOnly;
    private RadioButton mRBMcdiSodi;
    private RadioButton mRBDisableTimer;
    private RadioButton mRBSetTimer;
    private RadioButton[] mRBModeArray;
    private RadioButton[] mRBTimerArray;
    private Button mBtnStartTimer;
    private Button mBtnGetMcdiSetting;
    private EditText mEditTimerVal;
    private LinearLayout mSetTimerControler;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private int mMcdiMode;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.mcdi_setting);

         mRBDisableMcdi = (RadioButton) findViewById(R.id.mcdi_disable);
         mRBDisableMcdi.setOnCheckedChangeListener(this);
         mRBMcdiOnly = (RadioButton) findViewById(R.id.mcdi_only);
         mRBMcdiOnly.setOnCheckedChangeListener(this);
         mRBMcdiSodi = (RadioButton) findViewById(R.id.mcdi_sodi);
         mRBMcdiSodi.setOnCheckedChangeListener(this);
         mRBDisableTimer = (RadioButton) findViewById(R.id.mcdi_timer_disable);
         mRBDisableTimer.setOnCheckedChangeListener(this);
         mRBSetTimer = (RadioButton) findViewById(R.id.mcdi_timer_val_set);
         mRBSetTimer.setOnCheckedChangeListener(this);
         mBtnStartTimer = (Button) findViewById(R.id.mcdi_start_timer);
         mBtnStartTimer.setOnClickListener(this);
         mBtnGetMcdiSetting = (Button) findViewById(R.id.get_mcdi_setting);
         mBtnGetMcdiSetting.setOnClickListener(this);
         mEditTimerVal = (EditText) findViewById(R.id.mcdi_timer_val);
         mSetTimerControler = (LinearLayout) findViewById(R.id.mcdi_wake_timer_contrl);
         mRBModeArray = new RadioButton[3];
         mRBModeArray[0] = mRBDisableMcdi;
         mRBModeArray[1] = mRBMcdiOnly;
         mRBModeArray[2] = mRBMcdiSodi;
         mRBTimerArray = new RadioButton[2];
         mRBTimerArray[0] = mRBDisableTimer;
         mRBTimerArray[1] = mRBSetTimer;
         mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
         mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
         initUIByData();
     }

     private void initUIByData() {
         String cmd;
         String output;

         Log.v("@M_" + TAG, "initUIByData()");
         // init mode
         cmd = CAT + " " +  MODE_FS;
         output = execCommand(cmd);
         //output = "1";
         if (output == null) {
             Toast.makeText(this, "Feature Fail or Don't Support!", Toast.LENGTH_SHORT).show();
             finish();
             return;
         }
         output = output.trim();
         mMcdiMode = -1;
         try {
             mMcdiMode = Integer.parseInt(output);
         } catch (NumberFormatException e) {
             Log.e("@M_" + TAG, "NumberFormatException: " + e.getMessage());
         }
         try {
             mRBModeArray[mMcdiMode].setChecked(true);
         } catch (IndexOutOfBoundsException e) {
             Log.e("@M_" + TAG, "mMcdiMode:" + mMcdiMode + " IndexOutOfBoundsException: " + e.getMessage());
         }
         // init timer value
         cmd = CAT + " " + TIMER_FS;
         output = execCommand(cmd);
         //output = "0";
         if (output == null) {
             Toast.makeText(this, "Feature Fail or Don't Support!", Toast.LENGTH_SHORT).show();
             finish();
             return;
         }
         if ("0".equals(output.trim())) {
             handleSetTimer(0);
         } else {
             handleSetTimer(1);
             mEditTimerVal.setText(output);
         }

     }

//   private void enableSetTimerUI(boolean enabled) {
//       int i;
//       for (i = 0; i < mRBTimerArray.length; i++) {
//           mRBTimerArray[i].setEnabled(enabled);
//       }
//       mEditTimerVal.setEnabled(enabled);
//       mBtnStartTimer.setEnabled(enabled);
//   }

     private void visibleSetTimerUI(boolean visible) {
         int visibility;
         if (visible) {
             visibility = View.VISIBLE;
         } else {
             visibility = View.GONE;
         }
         mSetTimerControler.setVisibility(visibility);
         mBtnStartTimer.setVisibility(visibility);
     }

     private void handleSetTimer(int index) {
         if (0 == index) {
             mEditTimerVal.setEnabled(false);
             checkOneRadio(mRBTimerArray, mRBDisableTimer, true);
             mBtnStartTimer.setEnabled(false);
         } else if (1 == index) {
             mEditTimerVal.setEnabled(true);
             checkOneRadio(mRBTimerArray, mRBSetTimer, true);
             mBtnStartTimer.setEnabled(true);
         } else {
             Log.w("@M_" + TAG, "unknown index: " + index);
         }
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

     private void checkOneRadio(RadioButton[] array, RadioButton target, boolean checked) {
         for (int i = 0; i < array.length; i++) {
             if (target == array[i]) {
                 array[i].setChecked(checked);
             } else {
                 array[i].setChecked(!checked);
             }
         }
     }

     private boolean validateSetting() {
         String msg;
         if (mRBDisableMcdi.isChecked()) {
             msg = getString(R.string.mcdi_disable);
             Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
             return false;
         }
         int timerVal;
         try {
             timerVal = Integer.parseInt(mEditTimerVal.getText().toString());
         } catch (NumberFormatException e) {
             msg = getString(R.string.mcdi_invalid_timer_val);
             Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
             return false;
         }
         if (timerVal >= 15 && timerVal <= 1000000) {
            return true;
         }
         msg = getString(R.string.mcdi_invalid_timer_val);
         Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
         return false;
     }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String cmd;
        String output;
        switch(id) {
        case R.id.mcdi_start_timer:
            if (validateSetting()) {
                setTimerVal(mEditTimerVal.getText().toString());
                String msg = getString(R.string.mcdi_start_timer_success);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.get_mcdi_setting:
            cmd = CAT + " " + SETTING_FS;
            output = execCommand(cmd);
            showDialog("MCDI Setting", output);
            break;
        default:
            Log.w("@M_" + TAG, "unknown view id: " + id);
            break;
        }
    }

    private void showDialog(String title, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this).setCancelable(
                false).setTitle(title).setMessage(msg).
                setPositiveButton(android.R.string.ok, null).create();
        dialog.show();
    }

    private void setMcdiMode(int index) {
        String cmd;

        cmd = ECHO + " \"" + index + " " + MCDI_MODE_TAG + "\" > " + SETTING_FS;
        execCommand(cmd);
    }

    private void setTimerVal(String val) {
        String cmd;

        cmd = ECHO + " \"" + val + " " + TIMER_VAL_TAG + "\" > " + SETTING_FS;
        execCommand(cmd);
    }

    @Override
    protected void onDestroy() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton btnView, boolean isChecked) {
        int id = btnView.getId();
        switch(id) {
        case R.id.mcdi_disable:
            if (isChecked) {
                checkOneRadio(mRBModeArray, mRBDisableMcdi, true);
                //enableSetTimerUI(false);
                visibleSetTimerUI(false);
                setMcdiMode(0);
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
            break;
        case R.id.mcdi_only:
            if (isChecked) {
                checkOneRadio(mRBModeArray, mRBMcdiOnly, true);
                //enableSetTimerUI(true);
                visibleSetTimerUI(true);
                setMcdiMode(1);
                if (!mWakeLock.isHeld()) {
                    mWakeLock.acquire();
                }
            }
            break;
        case R.id.mcdi_sodi:
            if (isChecked) {
                checkOneRadio(mRBModeArray, mRBMcdiSodi, true);
                //enableSetTimerUI(true);
                visibleSetTimerUI(true);
                setMcdiMode(2);
                if (!mWakeLock.isHeld()) {
                    mWakeLock.acquire();
                }
            }
            break;
        case R.id.mcdi_timer_disable:
            if (isChecked) {
                setTimerVal("0");
                handleSetTimer(0);
            }
            break;
        case R.id.mcdi_timer_val_set:
            if (isChecked) {
                handleSetTimer(1);
            }
            break;
        default:
            Log.w("@M_" + TAG, "unknown view id: " + id);
            break;
        }
    }
}
