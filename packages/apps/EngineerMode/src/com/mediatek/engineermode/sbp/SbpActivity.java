
package com.mediatek.engineermode.sbp;

import android.app.Activity;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.FeatureSupport;
import com.mediatek.engineermode.R;

/**
 * Display SBP info.
 */
public class SbpActivity extends Activity {
    private static final String TAG = "SbpActivity";
    private static final int TEXT_COUNT = 3;
    private static final int MSG_SBP_INFO = 1;
    private static final String QUERY_CMD = "AT+ESBP?";
    private static final String RESPONSE_CMD = "+ESBP: ";

    private Phone mPhone = null;
    private TextView[] mTextViews = new TextView[TEXT_COUNT];

    private Handler mATCmdHander = new Handler() {
        public void handleMessage(Message msg) {
            AsyncResult ar;
            switch (msg.what) {
            case MSG_SBP_INFO:
                ar = (AsyncResult) msg.obj;
                if (ar != null && ar.exception == null) {
                    handleQuery((String[]) ar.result);
                } else {
                    Toast.makeText(SbpActivity.this, "Send AT command failed", Toast.LENGTH_SHORT);
                }
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sbp);
        mTextViews[0] = (TextView) findViewById(R.id.sbp_id);
        mTextViews[1] = (TextView) findViewById(R.id.sbp_feature);
        mTextViews[2] = (TextView) findViewById(R.id.sbp_data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] cmd = new String[2];
        cmd[0] = QUERY_CMD;
        cmd[1] = RESPONSE_CMD;
        if (TelephonyManager.getDefault().getPhoneCount() > 1) {
            mPhone = PhoneFactory.getPhone(PhoneConstants.SIM_ID_1);
        } else {
            mPhone = PhoneFactory.getDefaultPhone();
        }



        if (mPhone != null) {
            mPhone.invokeOemRilRequestStrings(cmd, mATCmdHander.obtainMessage(MSG_SBP_INFO));
        }
    }

    private void handleQuery(String[] result) {
        if (result != null && result.length > 0) {
            Elog.v(TAG, "Modem return: " + result[0]);
            String[] values = result[0]
                    .substring(RESPONSE_CMD.length(), result[0].length()).trim().split(",");
            if (values != null && values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    mTextViews[i].setText(values[i]);
                }
            }
        } else {
            Elog.e(TAG, "Modem return null");
        }
    }
}
