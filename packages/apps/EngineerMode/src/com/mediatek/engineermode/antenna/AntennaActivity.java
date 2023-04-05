
package com.mediatek.engineermode.antenna;

import android.app.Activity;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.mediatek.engineermode.ChipSupport;
import com.mediatek.engineermode.FeatureSupport;
import com.mediatek.engineermode.R;

import java.util.HashSet;

public class AntennaActivity extends Activity {
    private static final String TAG = "AntennaTest";
    private static final int MSG_QUERY_SUPPORT_ANTENNA_MODE = 0;
    private static final int MSG_QUERY_ANTENNA_MODE = 1;
    private static final int MSG_SET_ANTENNA_MODE = 2;
    private static final int MODE_INDEX_BASE_3G = 10;

    private HashSet<Integer> mSupportModes = new HashSet<Integer>();
    private Phone mPhone = null;
    private Spinner mSpinner4G = null;
    private Spinner mSpinner3G = null;
    private Toast mToast = null;
    private int mCurrentPos = 0;

    private final OnItemSelectedListener mItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
            if (arg0 == mSpinner4G) {
                if (mCurrentPos == arg0.getSelectedItemPosition()) {
                    return;
                }
                mCurrentPos = arg0.getSelectedItemPosition();

                if (mSupportModes.contains(pos)) {
                    setMode(pos);
                } else {
                    showToast("Not supported.");
                    queryCurrentMode();
                }
            } else if (arg0 == mSpinner3G) {
                if (pos > 0) {
                    setMode(MODE_INDEX_BASE_3G + pos - 1);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Do nothing
        }
    };

    private final Handler mCommandHander = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            AsyncResult asyncResult;
            switch (msg.what) {
            case MSG_QUERY_SUPPORT_ANTENNA_MODE:
                asyncResult = (AsyncResult) msg.obj;
                if (asyncResult != null && asyncResult.exception == null
                        && asyncResult.result != null) {
                    final String[] result = (String[]) asyncResult.result;
                    Log.i("@M_" + TAG, "Get support mode " + result[0]);
                    parseSupportMode(result[0]);
                    queryCurrentMode();
                } else {
                    showToast("Query antenna mode failed.");
                }
                break;
            case MSG_QUERY_ANTENNA_MODE:
                asyncResult = (AsyncResult) msg.obj;
                if (asyncResult != null && asyncResult.exception == null
                        && asyncResult.result != null) {
                    final String[] result = (String[]) asyncResult.result;
                    Log.i("@M_" + TAG, "Get mode " + result[0]);
                    parseCurrentMode(result[0]);
                } else {
                    showToast("Query antenna mode failed.");
                }
                break;
            case MSG_SET_ANTENNA_MODE:
                asyncResult = (AsyncResult) msg.obj;
                if (asyncResult.exception == null) {
                    showToast("Set successful.");
                } else {
                    showToast("Set failed.");
                }
                break;
            default:
                break;
            }
        }
    };

    private void parseSupportMode(String data) {
        // query result is like: "+ERXPATH: (0-2)" (support mode 0, 1 and 2)
        // or like "+ERXPATH: (0,2)" (support mode 0 and 2)
        int start = data.indexOf('(', 0);
        int end = data.indexOf(')', 0);
        if (start >= 0 && start + 1 < end) {
            String[] modes = data.substring(start + 1, end).split(",");
            for (int i = 0; i < modes.length; i++) {
                String[] range = modes[i].split("-");
                try {
                    int min = Integer.parseInt(range[0].trim());
                    int max = Integer.parseInt(range[range.length - 1].trim());
                    for (int mode = min; mode <= max; mode++) {
                        mSupportModes.add(mode);
                    }
                } catch (NumberFormatException e) {
                    Log.e("@M_" + TAG, "Wrong supported mode format: " + data);
                }
            }
        }
    }

    private void parseCurrentMode(String data) {
        // query result is like: +ERXPATH: 1
        int mode = -1;
        try {
            mode = Integer.valueOf(data.substring("+ERXPATH:".length()).trim());
        } catch (NumberFormatException e) {
            Log.e("@M_" + TAG, "Wrong current mode format: " + data);
        }

        if (mode < 0 || mode >= mSpinner4G.getCount()) {
            showToast("Modem returned invalid mode: " + data);
            return;
        }

        mCurrentPos = mode;
        mSpinner4G.setSelection(mode);
        mSpinner4G.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.antena_test);

        mSpinner4G = (Spinner) findViewById(R.id.spinner_antenna_4g);
        mSpinner4G.setOnItemSelectedListener(mItemSelectedListener);
        mSpinner4G.setEnabled(false);

        mSpinner3G = (Spinner) findViewById(R.id.spinner_antenna_3g);
        mSpinner3G.setOnItemSelectedListener(mItemSelectedListener);

        if (!FeatureSupport.isSupported(FeatureSupport.FK_LTE_SUPPORT)) {
            findViewById(R.id.antenna_title_4g).setVisibility(View.GONE);
            findViewById(R.id.antenna_hint).setVisibility(View.GONE);
            mSpinner4G.setVisibility(View.GONE);
        }


        if (TelephonyManager.getDefault().getPhoneCount() > 1) {
            mPhone = PhoneFactory.getPhone(PhoneConstants.SIM_ID_1);
        } else {
            mPhone = PhoneFactory.getDefaultPhone();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FeatureSupport.isSupported(FeatureSupport.FK_LTE_SUPPORT)) {
            querySupportMode();
        }
    }

    private void queryCurrentMode() {
        sendCommand(new String[] {"AT+ERXPATH?", "+ERXPATH:"}, MSG_QUERY_ANTENNA_MODE);
    }

    private void querySupportMode() {
        sendCommand(new String[] {"AT+ERXPATH=?", "+ERXPATH:"}, MSG_QUERY_SUPPORT_ANTENNA_MODE);
    }

    private void setMode(int mode) {
        Log.i("@M_" + TAG, "Set mode " + mode);
        sendCommand(new String[] {"AT+ERXPATH=" + mode, ""}, MSG_SET_ANTENNA_MODE);
    }

    private void sendCommand(String[] command, int msg) {
        if (mPhone != null) {
            mPhone.invokeOemRilRequestStrings(command, mCommandHander.obtainMessage(msg));
        }
    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
