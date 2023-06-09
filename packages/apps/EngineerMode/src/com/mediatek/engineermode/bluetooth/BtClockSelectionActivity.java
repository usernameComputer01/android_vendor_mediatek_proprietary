

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;

import com.mediatek.engineermode.R;

/**
 * Do BT RX ADC Clock selection
 * @author mtk80137
 *
 */
public class BtClockSelectionActivity extends Activity implements OnClickListener {

    private static final String TAG = "BtClockSelectionFeature";

    private BtTest mBtTest = null;
    private boolean mStateOn = false;  // Default the feature is off.

    // UI component
    private Button mSetButton;

    private boolean mHasInit = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.bt_clock_selection);

        TextView tv = (TextView) findViewById(R.id.ClockSelectionDesTxv);
        tv.setText(Html.fromHtml(getString(R.string.ClockSelectionDes)));
        mSetButton = (Button) findViewById(R.id.clock_select_btn);
        if (mStateOn) {
            mSetButton.setText(R.string.clock_select_turn_off);
        } else {
            mSetButton.setText(R.string.clock_select_turn_on);
        }
        mSetButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        Log.v("@M_" + TAG, "-->onResume");
        super.onResume();

       // showDialog(OPEN_BT);
      //  mWorkHandler.sendEmptyMessage(OP_OPEN_BT);
    }
    @Override
    protected void onStop() {
        Log.v("@M_" + TAG, "-->onStop");
        uninitBtTestOjbect();
        super.onStop();
    }

    @Override
    public void onClick(final View arg0) {

        if (arg0 == mSetButton) {
            if (mStateOn) {
                mStateOn = false;
                mSetButton.setText(R.string.clock_select_turn_on);
            } else {
                mStateOn = true;
                mSetButton.setText(R.string.clock_select_turn_off);
            }
            runHCICommand(mStateOn);
        }
    }

    private void runHCICommand(boolean state) {
        initBtTestOjbect();
        int cmdLen = 12;
        char[] cmd = new char[cmdLen];
        char[] response = null;

        Log.i("@M_" + TAG, "-->runHCICommand");
        cmd[0] = 0x01;
        cmd[1] = 0x20;
        cmd[2] = 0xFC;
        cmd[3] = 0x08;
        cmd[4] = 0x0;
        cmd[5] = 0x0;
        cmd[6] = 0x0;
        cmd[7] = 0x0;
        cmd[8] = 0x0;
        cmd[9] = 0x0;
        cmd[10] = 0x0;
        cmd[11] = (char) (state ? 0x01 : 0x0);

        if (mBtTest == null) {
            mBtTest = new BtTest();
        }
        response = mBtTest.hciCommandRun(cmd, cmdLen);
        if (response != null) {
            String s = null;
            for (int i = 0; i < response.length; i++) {
                s = String.format("response[%d] = 0x%x", i, (long) response[i]);
                Log.i("@M_" + TAG, s);
            }
        } else {
            Log.i("@M_" + TAG, "HCICommandRun failed");
        }
        response = null;

    }

    // init BtTest -call init function of BtTest
    private boolean initBtTestOjbect() {
        Log.i("@M_" + TAG, "-->initBtTestOjbect");

        if (mHasInit) {
            return mHasInit;
        }
        if (mBtTest == null) {
            mBtTest = new BtTest();
        }
        if (mBtTest != null) {
            if (mBtTest.init() != 0) {
                mHasInit = false;
                Log.i("@M_" + TAG, "mBtTest initialization failed");
            } else {
                mHasInit = true;
            }
        }
        return mHasInit;
    }

    private boolean uninitBtTestOjbect() {
        Log.i("@M_" + TAG, "-->uninitBtTestOjbect");
        if (mBtTest != null && mHasInit) {
            //runHCIResetCmd();
            if (mBtTest.unInit() != 0) {
                Log.i("@M_" + TAG, "mBtTest un-initialization failed");
            }
        }
        mBtTest = null;
        mHasInit = false;
        return true;
    }
}
