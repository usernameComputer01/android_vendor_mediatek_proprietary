

package com.mediatek.engineermode.vcmd;

import java.io.IOException;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.ShellExe;
import com.mediatek.engineermode.vcmd.ViewCommand.Adapter;

public class TextViewCmdAdapter implements Adapter {
    private static final String TAG = "TextViewCmdAdapter";
    @Override
    public boolean doViewAction(Activity host, ViewCommand vc) {
        if (!(vc.adapter instanceof TextViewCmdAdapter)) {
            Elog.d(TAG, "Unsupported ViewCommand.Adapter:" + vc.adapter.toString());
            return false;
        }
        Elog.d(TAG, "Only Do Nothing");
        return true;
    }

    @Override
    public boolean setViewByCmdVal(Activity host, ViewCommand vc) {
        if (!(vc.adapter instanceof TextViewCmdAdapter)) {
            Elog.d(TAG, "Unsupported ViewCommand.Adapter:" + vc.adapter.toString());
            return false;
        }
        TextView tv = (TextView) host.findViewById(vc.rid);
        String cmd = "cat " + vc.cmdPath;
        Elog.d(TAG, "cmd:" + cmd);
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
        tv.setText(val);
        return true;
    }

    @Override
    public Class<? extends View> getViewType() {
        return TextView.class;
    }

}
