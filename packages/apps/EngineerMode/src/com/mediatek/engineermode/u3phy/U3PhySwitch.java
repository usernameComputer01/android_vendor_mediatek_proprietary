

package com.mediatek.engineermode.u3phy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.File;
import java.io.IOException;

/**
 * Menu to set U3 PHY Switch.
 */
public class U3PhySwitch extends Activity implements View.OnClickListener {
    private static final String TAG = "EM/U3Phy";
    private static final String USB_CONNECT_STATE = "/sys/class/android_usb/android0/state";
    private static final String USB_CONNECT = "CONNECT";
    private static final String USB_CONFIGURED = "CONFIGURED";
    private static final String CONFIG_FILE_PATH = "/sys/devices/platform/mt_usb/sib_enable";

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonAp;
    private RadioButton mRadioButtonMd;
    private Button mSetButton;
    private TextView mHintText;
    private Toast mToast;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.u3_phy_switch_set) {
            int checkedId = mRadioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.u3_phy_switch_ap) {
                setConfig(0);
            } else if (checkedId == R.id.u3_phy_switch_md) {
                setConfig(1);
            } else {
                showToast("Please select one option");
                return;
            }
            toggleFlightMode();
            showToast("Set done");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u3_phy_switch);
        mRadioGroup = (RadioGroup) findViewById(R.id.u3_phy_switch_group);
        mRadioButtonAp = (RadioButton) findViewById(R.id.u3_phy_switch_ap);
        mRadioButtonMd = (RadioButton) findViewById(R.id.u3_phy_switch_md);
        mSetButton = (Button) findViewById(R.id.u3_phy_switch_set);
        mSetButton.setOnClickListener(this);
        mHintText = (TextView) findViewById(R.id.u3_phy_switch_hint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private int getConfig() {
        int ret = ShellExe.RESULT_FAIL;
        String cmd = "cat " + CONFIG_FILE_PATH;
        try {
            ret = ShellExe.execCommand(cmd, true);
            Elog.d(TAG, cmd + " return " + ret);
            if (ShellExe.RESULT_SUCCESS == ret) {
                int value = Integer.parseInt(ShellExe.getOutput());
                Elog.d(TAG, CONFIG_FILE_PATH + ": " + ShellExe.getOutput());
                return value;
            } else {
                showToast("Get value failed");
            }
        } catch (IOException e) {
            Elog.d(TAG, cmd.toString() + e.getMessage());
            showToast("Get value failed");
        } catch (NumberFormatException e) {
            Elog.d(TAG, cmd.toString() + e.getMessage());
            showToast("Get value failed");
        }
        return -1;
    }

    private void setConfig(int value) {
        int ret = ShellExe.RESULT_FAIL;
        String cmd = "echo " + value + " > " + CONFIG_FILE_PATH;
        try {
            ret = ShellExe.execCommand(cmd, true);
            Elog.d(TAG, cmd + " return " + ret);
            if (ShellExe.RESULT_SUCCESS != ret) {
                showToast("Set failed");
            }
        } catch (IOException e) {
            Elog.d(TAG, cmd.toString() + e.getMessage());
            showToast("Set failed");
        }
    }

    private boolean isUsbConnected() {
        String result = null;
        boolean isConnected = false;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("cat ");
        strBuilder.append(USB_CONNECT_STATE);
        Elog.v(TAG, "isUsbConnected cmd: " + strBuilder.toString());
        try {
            if (ShellExe.RESULT_SUCCESS == ShellExe.execCommand(strBuilder.toString(), true)) {
                result = ShellExe.getOutput();
                if (result.equals(USB_CONFIGURED) || result.equals(USB_CONNECT)) {
                    isConnected = true;
                }
            }
        } catch (IOException e) {
            Elog.w(TAG, "get current dramc IOException: " + e.getMessage());
        }
        return isConnected;
    }

    private void toggleFlightMode() {
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", true);
        sendBroadcast(intent);

        intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", false);
        sendBroadcast(intent);
    }

    private void updateUI() {
        if (!(new File(CONFIG_FILE_PATH).exists())) {
            mSetButton.setEnabled(false);
            mRadioButtonAp.setEnabled(false);
            mRadioButtonMd.setEnabled(false);
            mHintText.setEnabled(false);
            return;
        }

        int config = getConfig();
        if (config == 0) {
            mRadioGroup.check(R.id.u3_phy_switch_ap);
        } else if (config == 1) {
            mRadioGroup.check(R.id.u3_phy_switch_md);
        }
        mSetButton.setEnabled(!isUsbConnected());
    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
