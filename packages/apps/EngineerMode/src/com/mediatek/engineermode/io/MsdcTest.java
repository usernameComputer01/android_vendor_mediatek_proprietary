

package com.mediatek.engineermode.io;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.mediatek.engineermode.R;

public class MsdcTest extends Activity {

    static final int EVENT_SET_OK_ID = 100;
    static final int EVENT_SET_FAIL_ID = 101;
    static final int EVENT_GET_FAIL_ID = 110;

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (id == EVENT_SET_OK_ID) {
            builder.setTitle(R.string.msdc_set_ok_title);
            builder.setMessage(R.string.msdc_set_ok_message);
            builder.setPositiveButton(R.string.msdc_btn_text, null);
        } else if (id == EVENT_SET_FAIL_ID) {
            builder.setTitle(R.string.msdc_set_fail_title);
            builder.setMessage(R.string.msdc_set_fail_message);
            builder.setPositiveButton(R.string.msdc_btn_text, null);
        } else {
            builder.setTitle(R.string.msdc_get_fail_title);
            builder.setMessage(R.string.msdc_get_fail_message);
            builder.setPositiveButton(R.string.msdc_btn_text, null);
        }
        return builder.create();
    }
}
