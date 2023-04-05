

package com.mediatek.engineermode.vcmd;

import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.ShellExe;
import com.mediatek.engineermode.vcmd.ViewCommand.Adapter;

public class CheckBoxCmdAdapter implements Adapter {

    private static final String TAG = "CheckBoxCmdAdapter";
    private static final String CHECKED = "1";
    private static final String UNCHECKED = "0";
    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disable";

    @Override
    public boolean doViewAction(Activity host, ViewCommand vc) {
        if (!(vc.adapter instanceof CheckBoxCmdAdapter)) {
            Elog.d(TAG, "Unsupported ViewCommand.Adapter:" + vc.adapter.toString());
            return false;
        }
        CheckBox cb = (CheckBox) host.findViewById(vc.rid);
        StringBuilder builder = new StringBuilder().append("echo ");
        if (cb.isChecked()) {
            builder.append(CHECKED);
        } else {
            builder.append(UNCHECKED);
        }
        builder.append(" > ").append(vc.cmdPath);
        String cmd = builder.toString();
        Elog.d(TAG, "cmd: " + cmd);
        int result = -1;
        try {
            result = ShellExe.execCommand(cmd);
        } catch (IOException e) {
            Elog.d(TAG, "IOException:" + e.getMessage());
            return false;
        }
        if (result != ShellExe.RESULT_SUCCESS) {
            Elog.d(TAG, "fail to exec write cmd");
            return false;
        }
        return true;
    }

    @Override
    public boolean setViewByCmdVal(Activity host, ViewCommand vc) {
        if (!(vc.adapter instanceof CheckBoxCmdAdapter)) {
            Elog.d(TAG, "Unsupported ViewCommand.Adapter:" + vc.adapter.toString());
            return false;
        }
        CheckBox cb = (CheckBox) host.findViewById(vc.rid);
        String cmd = "cat " + vc.cmdPath;
        Elog.d(TAG, "CMD:" + cmd);
        int ret;
        try {
            ret = ShellExe.execCommand(cmd);
        } catch (IOException e) {
            Elog.d(TAG, "IOException:" + e.getMessage());
            return false;
        }
        String val = ShellExe.getOutput();
        if (ShellExe.RESULT_SUCCESS != ret) {
            Elog.d(TAG, "Exec cmd fail:" + ret + " output:" + val);
           return false;
        }
        if (val == null) {
            Elog.d(TAG, "output was null");
            return false;
        }
        String targetVal = val.trim();
        if (isCheckedStr(true, targetVal)) {
            cb.setChecked(true);
        } else if (isCheckedStr(false, targetVal)) {
            cb.setChecked(false);
        } else {
            Elog.d(TAG, "invalid Cmd result:" + targetVal);
            return false;
        }
        return true;
    }

    @Override
    public Class<? extends View> getViewType() {
        return CheckBox.class;
    }

    protected boolean isCheckedStr(boolean checked, String str) {
        if (checked) {
            if (str.contains(ENABLED)) {
                return true;
            } else if (CHECKED.equals(str) || findRegex(str, "= *" + CHECKED)) {
                return true;
            }
        } else {
            if (str.contains(DISABLED)) {
                return true;
            } else if (UNCHECKED.equals(str) || findRegex(str, "= *" + UNCHECKED)) {
                return true;
            }
        }
        return false;
    }

    private boolean findRegex(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(str).find();
    }

}
