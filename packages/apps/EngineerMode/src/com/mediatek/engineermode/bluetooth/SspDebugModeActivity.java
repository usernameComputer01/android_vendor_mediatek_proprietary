

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

//import com.mediatek.bluetooth.BluetoothAdapterEx;
import com.mediatek.engineermode.EngineerMode;
import com.mediatek.engineermode.R;

/**
 * Do BT SSP debug mode test.
 * Only click the checkbox when the bluetooth is on
 * @author mtk54040
 *
 */
public class SspDebugModeActivity extends Activity implements OnClickListener {

    private static final String TAG = "SSPDebugMode";
    private static final String KEY_PROP_SSP = "mediatek.btem.ssp";
//    private Handler mUiHandler = null;

    // Message ID
    private static final int OP_OPEN_BT = 1;
    private static final int OP_CLOSE_BT = 2;
    private static final int OPEN_BT_FINISHED = 3;
    private static final int CLOSE_BT_FINISHED = 4;
    private static final int SET_SSP = 5;
    private static final int SET_SSP_FINISHED = 6;

    // Dialog ID
    private static final int OPEN_BT = 11;
    private static final int CLOSE_BT = 12;
    private static final int EXIT_EM_BT = 13;

    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

    private static final int SLEEP_TIME = 300;

    // UI component
    private CheckBox mChecked;
    private BluetoothAdapter mBtAdapter = null;
   // private BluetoothAdapterEx mBtAdapterEx = null;

    private boolean mSspModeOn = false;
    // private boolean mDialogShowed = false;

//    private HandlerThread mWorkThread = null;
    // private Looper mWorkLooper = null;
    private WorkHandler mWorkHandler = null;
    private BtTest mBtTest = null;
    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OPEN_BT_FINISHED) {
//                if (mBtAdapter.getState() == BluetoothAdapter.STATE_ON) {
                mChecked.setEnabled(true);
                mChecked.setChecked(mSspModeOn);
                removeDialog(OPEN_BT);
            } else if (msg.what == CLOSE_BT_FINISHED) {
                removeDialog(CLOSE_BT);
            } else if (msg.what == SET_SSP_FINISHED) {
                mChecked.setEnabled(true);
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ssp_debug_mode);

//        CharSequence str1 = getString(R.string.SSPDebugMode);
        TextView tv = (TextView) findViewById(R.id.SSPDebugModetxv);

         tv.setText(Html.fromHtml(getString(R.string.SSPDebugMode)));


        mChecked = (CheckBox) findViewById(R.id.SSPDebugModeCb);
        mChecked.setOnClickListener(this);
//        if (null != mChecked) {
//            mChecked.setOnCheckedChangeListener(mCheckedListener);
//        } else {
//            Log.w("@M_" + TAG, "findViewById(R.id.SSPDebugModeCb) failed");
//        }

        // Disable set Checkbox before open bluetooth
        mChecked.setEnabled(false);

//        mWorkThread = new HandlerThread(TAG);
//        mWorkThread.start();
//
//        // mWorkLooper = mWorkThread.getLooper();
//        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
//
        mBtTest = new BtTest();
        HandlerThread workThread = new HandlerThread(TAG);
        workThread.start();

        Looper looper = workThread.getLooper();
        mWorkHandler = new WorkHandler(looper);

    }
    @Override
    protected void onResume() {
//        Log.v("@M_" + TAG, "-->onResume");
        super.onResume();

        showDialog(OPEN_BT);
        // mDialogShowed = true;
        mWorkHandler.sendEmptyMessage(OP_OPEN_BT);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.v("@M_" + TAG, "-->onCreateDialog");
        if (id == OPEN_BT) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_open));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            // dialog.show();
            Log.v("@M_" + TAG, "OPEN_BT ProgressDialog succeed");
            return dialog;
        } else if (id == EXIT_EM_BT) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.Success)
                .setMessage(R.string.BT_exit_em)
                .setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            backToEMEntry();
                        }
                    }).create();
            return dialog;
        } else if (id == CLOSE_BT) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_close));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            // dialog.show();
            return dialog;
        }
        return null;
    }

    private void backToEMEntry() {
        Intent intent = new Intent(this, EngineerMode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Log.v("@M_" + TAG, "-->onBackPressed mSspModeOn=" + mSspModeOn);
        if (mSspModeOn) {
            // showDialog(EXIT_EM_BT);
            Toast.makeText(getApplicationContext(),
                    R.string.BT_exit_em, Toast.LENGTH_SHORT).show();
            backToEMEntry();
        } else {
            // Debug test not start, close bt
            showDialog(CLOSE_BT);
            mWorkHandler.sendEmptyMessage(OP_CLOSE_BT);
        }
        super.onBackPressed();
    }

    public void onClick(View v) {
        if (v.equals(mChecked)) {
            mSspModeOn = mChecked.isChecked();
            // Disable click the checkbox
            mChecked.setEnabled(false);
            mWorkHandler.sendEmptyMessage(SET_SSP);
            Log.v("@M_" + TAG, "SSPDebug isChecked--" + mChecked.isChecked());
            if (mSspModeOn) {
                Log.v("@M_" + TAG, " back EngineerMode");
                showDialog(EXIT_EM_BT);
            }
        }

    }

    /**
     * Deal with function request.
     *
     * @author mtk54040
     *
     */
    private final class WorkHandler extends Handler {
        private WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mBtAdapter == null) {
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            //if (mBtAdapterEx == null) {
            //    mBtAdapterEx = BluetoothAdapterEx.getDefaultAdapterEx();
            //}
            if (msg.what == OP_OPEN_BT) {
                if (mBtAdapter.getState() != BluetoothAdapter.STATE_ON) {
                    // Open Bluetooth through mAdapter
                    mBtAdapter.enable();
                    while (mBtAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {

                        Log.i("@M_" + TAG, "Bluetooth turning on ...");
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            Log.i("@M_" + TAG, e.getMessage());
                        }
                    }
                }
                // Get the Ssp debug mode state
                //mSspModeOn = mBtAdapterEx.getSSPDebugMode();
                mSspModeOn = VALUE_TRUE.equals(SystemProperties.get(
                KEY_PROP_SSP, VALUE_FALSE));

                Log.i("@M_" + TAG, "mSspModeOn =" + mSspModeOn);
                mUiHandler.sendEmptyMessage(OPEN_BT_FINISHED);
            } else if (msg.what == OP_CLOSE_BT) {
                if (mBtAdapter.getState() != BluetoothAdapter.STATE_OFF) {
                    // Cloese bluetooth
                    mBtAdapter.disable();
                    while (mBtAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                         Log.v("@M_" + TAG, "Bluetooth turning off ...");
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            Log.i("@M_" + TAG, e.getMessage());
                        }
                    }
                }

                mUiHandler.sendEmptyMessage(CLOSE_BT_FINISHED);
            } else if (msg.what == SET_SSP) {
                // Start or stop ssp debug mode
                //mBtAdapterEx.setSSPDebugMode(mSspModeOn);
                mBtTest.setSSPDebugMode(mSspModeOn);
               // SystemProperties.set(KEY_PROP_SSP, mSspModeOn ? VALUE_TRUE
               //         : VALUE_FALSE);
                mUiHandler.sendEmptyMessage(SET_SSP_FINISHED);
            }
        }
    }


}
