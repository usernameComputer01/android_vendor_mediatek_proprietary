

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mediatek.engineermode.R;

/**
 * Do bluetooth no singal rx test.
 *
 * @author mtk54040
 *
 */
public class NoSigRxTestActivity extends Activity implements
        DialogInterface.OnCancelListener, OnClickListener {
    private static final String TAG = "NoSigRxLOG";

    public static final int OP_BT_SEND = 0;
    public static final int OP_BT_STOP = 1;

    public static final int UI_BT_CLOSE = 5;
    public static final int UI_BT_CLOSE_FINISHED = 6;

    public static final int OP_IN_PROCESS = 8;
    public static final int OP_FINISH = 9;
    public static final int OP_RX_FAIL = 10;
    public static final int OP_ADDR_DEFAULT = 11;
    public static final int OP_TEST_OK_STEP1 = 12;
    public static final int OP_TEST_OK_STEP2 = 13;

    private static final int CHECK_BT_STATE = 20;
    private static final int DIALOG_RX_FAIL = 21;
    private static final int DIALOG_RX_TEST = 22;
//    private static final int CHECK_BT_DEVEICE = 23;
    private static final int DIALOG_BT_STOP = 23;

    private static final int TEST_STATUS_BEGIN = 100;
    private static final int TEST_STATUS_RESULT = 101;

    private BluetoothAdapter mBtAdapter;

    private Spinner mPattern;
    private Spinner mPocketType;
    private EditText mEdFreq;
    private EditText mEdAddr;

    private Button mBtnStartTest;

    private TextView mPackCnt;
    private TextView mPackErrRate;
    private TextView mRxByteCnt;
    private TextView mBitErrRate;

    private int[] mResult = null; //
    private int mTestStatus = TEST_STATUS_BEGIN;
    private int mStateBt;

    // used to record weather the button clicked action
    private boolean mDoneFinished = true;
    private boolean mDumpStart = false;
    private Handler mWorkHandler = null; // used to handle the
    private HandlerThread mWorkThread = null;

//    private static Handler mUiHandler = null;
    private BtTest mBtTest = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx_nosig_test);
        setValuesSpinner();
        mEdFreq = (EditText) findViewById(R.id.NSRX_editFrequency);
        mEdAddr = (EditText) findViewById(R.id.NSRX_editTesterAddr);
        mBtnStartTest = (Button) findViewById(R.id.NSRX_StartTest);
        mPackCnt = (TextView) findViewById(R.id.NSRX_StrPackCnt);
        mPackErrRate = (TextView) findViewById(R.id.NSRX_StrPackErrRate);
        mRxByteCnt = (TextView) findViewById(R.id.NSRX_StrPackByteCnt);
        mBitErrRate = (TextView) findViewById(R.id.NSRX_StrBitErrRate);

        mBtnStartTest.setOnClickListener(this);

        mWorkThread = new HandlerThread(TAG);
        mWorkThread.start();

        Looper looper = mWorkThread.getLooper();
        mWorkHandler = new WorkHandler(looper);

    }

    private Handler mUiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case OP_IN_PROCESS:
                Log.w("@M_" + TAG, "OP_IN_PROCESS");
                showDialog(DIALOG_RX_TEST);
                break;
            case OP_FINISH:
                Log.w("@M_" + TAG, "OP_FINISH");
                dismissDialog(DIALOG_RX_TEST);
                break;
            case OP_RX_FAIL:
                Log.w("@M_" + TAG, "OP_RX_FAIL");
                showDialog(DIALOG_RX_FAIL);
                break;
            case OP_ADDR_DEFAULT:
                mEdAddr.setText("A5F0C3");
                break;
            case OP_TEST_OK_STEP1:
                mTestStatus = TEST_STATUS_RESULT;
                mBtnStartTest.setText("End Test");
                break;
            case OP_TEST_OK_STEP2:
                mPackCnt.setText(String.valueOf(mResult[0]));
                mPackErrRate.setText(String.format("%.2f",
                        mResult[1] / 100.0)
                        + "%");
                mRxByteCnt.setText(String.valueOf(mResult[2]));
                mBitErrRate.setText(String.format("%.2f",
                        mResult[3] / 100.0)
                        + "%");
                mTestStatus = TEST_STATUS_BEGIN;
                mBtnStartTest.setText("Start");
                break;
            case UI_BT_CLOSE:
                Log.i("@M_" + TAG, "UI_BT_CLOSE");
                showDialog(DIALOG_BT_STOP);
                break;
            case UI_BT_CLOSE_FINISHED:
                Log.i("@M_" + TAG, "UI_BT_CLOSE_FINISHED");
                removeDialog(DIALOG_BT_STOP);
                finish();
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == CHECK_BT_STATE) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.Error)
                .setMessage(R.string.BT_turn_bt_off) // put in strings.xml
                .setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int which) {
                            finish();
                        }
                    }).create();
            return dialog;
        } else if (id == DIALOG_RX_FAIL) {
            // "Error").setMessage("Can't find any bluetooth device") // put in
            // strings.xml
            AlertDialog dialog = new AlertDialog.Builder(this).setCancelable(
                    false).setTitle(R.string.Error)
                    .setMessage(R.string.BT_no_sig_rx_fail) // put in strings.xml
                    .setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // finish();
                                }
                            }).create();
            return dialog;
        } else if (id == DIALOG_RX_TEST) {
            ProgressDialog dialog = new ProgressDialog(NoSigRxTestActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                String text = getString(R.string.BT_tx_test);
            dialog.setMessage(getString(R.string.BT_tx_test));
            dialog.setTitle(R.string.BTRxTitle);
            dialog.setCancelable(false);

            return dialog;
        } else if (id == DIALOG_BT_STOP) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_deinit));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            return dialog;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Log.v("@M_" + TAG, "-->onBackPressed ");
        if (mBtTest != null) {
            mWorkHandler.sendEmptyMessage(OP_BT_STOP);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        Log.v("@M_" + TAG, "-->onDestroy");
        if (mWorkThread != null) {
            mWorkThread.quit();
        }
        super.onDestroy();
    }

    private final class WorkHandler extends Handler {
        private WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OP_BT_SEND) {
                mUiHandler.sendEmptyMessage(OP_IN_PROCESS);
                mDoneFinished = false;
                doSendCommandAction();
                mDoneFinished = true;
                mUiHandler.sendEmptyMessage(OP_FINISH);
            } else if (msg.what == OP_BT_STOP) {
                mUiHandler.sendEmptyMessage(UI_BT_CLOSE);
                // do stop
                if (mDumpStart == true) {
                    if (mBtTest != null) {
                        Log.i("@M_" + TAG, "pollingStop");
                        mDumpStart = false;
                        mBtTest.pollingStop();
                    }
                }
                mBtTest = null;
                mUiHandler.sendEmptyMessage(UI_BT_CLOSE_FINISHED);
            }
        }
    }

    protected void setValuesSpinner() {
        // for TX pattern
        mPattern = (Spinner) findViewById(R.id.NSRX_PatternSpinner);
        ArrayAdapter<CharSequence> adapterPattern = ArrayAdapter
                .createFromResource(this, R.array.nsrx_pattern,
                        android.R.layout.simple_spinner_item);
        adapterPattern
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPattern.setAdapter(adapterPattern);

//        if (null != mPattern) {
//            mPattern.setAdapter(adapterPattern);
//        } else {
//            Log.w("@M_" + TAG, "findViewById(R.id.PatternSpinner) failed");
//        }

        // for TX pocket type
        mPocketType = (Spinner) findViewById(R.id.NSRX_PocketTypeSpinner);
        ArrayAdapter<CharSequence> adapterPocketType = ArrayAdapter
                .createFromResource(this, R.array.nsrx_Pocket_type,
                        android.R.layout.simple_spinner_item);
        adapterPocketType
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPocketType.setAdapter(adapterPocketType);
//        if (null != mPocketType) {
//            mPocketType.setAdapter(adapterPocketType);
//        } else {
//            Log.w("@M_" + TAG, "findViewById(R.id.PocketTypeSpinner) failed");
//        }
    }

    public void onClick(View arg0) {
        if (mDoneFinished) {
            // if the last click action has been handled, send another event
            // request
//            mPackCnt.setText(".");
//            mPackErrRate.setText(".");
//             mRxByteCnt.setText(".");
//             mBitErrRate.setText(".");
           // mWorkHandler.post(new WorkRunnable());
           mWorkHandler.sendEmptyMessage(OP_BT_SEND);

        } else {
            Log.w("@M_" + TAG, "last click is not finished yet."); // log
        }
    }

    /**
     * Send command the user has made, and finish the activity.
     */
    private boolean doSendCommandAction() {
        if (mDumpStart == true) {
            if (mBtTest != null) {
                Log.i("@M_" + TAG, "pollingStop");
                mDumpStart = false;
                mBtTest.pollingStop();
            }
        }
        if (mTestStatus == TEST_STATUS_BEGIN) {
            getBtState();
            enableBluetooth(false);
            getValuesAndSend();
            if (mBtTest != null) {
                Log.i("@M_" + TAG, "pollingStart");
                mBtTest.pollingStart();
                mDumpStart = true;
            }
        } else if (mTestStatus == TEST_STATUS_RESULT) {
            getResult();
        }

        return true;
    }

    // implemented for DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialog) {
        // request that the service stop the query with this callback object.
        Log.v("@M_" + TAG, "-->onCancel");
        finish();
    }

    @Override
    protected void onStart() {
//        Log.v("@M_" + TAG, "-->onStart");
        super.onStart();
        if (mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBtAdapter.getState() != BluetoothAdapter.STATE_OFF) {
            showDialog(CHECK_BT_STATE);
        }
    }

    private void getBtState() {
        Log.v("@M_" + TAG, "Enter GetBtState().");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == btAdapter) {
            Log.v("@M_" + TAG, "we can not find a bluetooth adapter.");
            // Toast.makeText(getApplicationContext(),
            // "We can not find a bluetooth adapter.", Toast.LENGTH_SHORT)
            // .show();
            mUiHandler.sendEmptyMessage(OP_RX_FAIL);
            return;
        }
        mStateBt = btAdapter.getState();
        Log.v("@M_" + TAG, "Leave GetBtState().");
    }

    private void enableBluetooth(boolean enable) {
        Log.v("@M_" + TAG, "Enter EnableBluetooth().");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == btAdapter) {
            Log.v("@M_" + TAG, "we can not find a bluetooth adapter.");
            return;
        }
        // need to enable
        if (enable) {
            Log.v("@M_" + TAG, "Bluetooth is enabled");
            btAdapter.enable();
        } else {
            // need to disable
            Log.v("@M_" + TAG, "Bluetooth is disabled");
            btAdapter.disable();
        }
        Log.v("@M_" + TAG, "Leave EnableBluetooth().");
    }

    public void getValuesAndSend() {
        Log.v("@M_" + TAG, "Enter GetValuesAndSend().");
        mBtTest = null;
//        if (mBtTest == null) {
//            Log.v("@M_" + TAG, "We cannot find BtTest object.");
//            return;
//        }
        int nPatternIdx = mPattern.getSelectedItemPosition();
        int nPocketTypeIdx = mPocketType.getSelectedItemPosition();
        int nFreq = 0;
        int nAddress = 0;
        try {
            nFreq = Integer.valueOf(mEdFreq.getText().toString());
            long longAdd = Long.valueOf(mEdAddr.getText().toString(), 16);
            nAddress = (int) longAdd;
            if (nFreq < 0 || nFreq > 78) {
                // Toast.makeText(getApplicationContext(),
                // "Error: Frequency error, must be 0-78.",
                // Toast.LENGTH_SHORT).show();
                mUiHandler.sendEmptyMessage(OP_RX_FAIL);
                return;
            }
            if (nAddress == 0) {
                nAddress = 0xA5F0C3;
                mUiHandler.sendEmptyMessage(OP_ADDR_DEFAULT);
            }
        } catch (NumberFormatException e) {
            Log.i("@M_" + TAG, "input number error!");
            return;
        }

        mBtTest = new BtTest();
        // send command to....
        boolean rc = mBtTest.noSigRxTestStart(nPatternIdx, nPocketTypeIdx,
                nFreq, nAddress);
        if (rc) {
            mUiHandler.sendEmptyMessage(OP_TEST_OK_STEP1);
        } else {
            Log.i("@M_" + TAG, "no signal rx test failed.");
            if ((BluetoothAdapter.STATE_TURNING_ON == mStateBt)
                    || (BluetoothAdapter.STATE_ON == mStateBt)) {
                enableBluetooth(true);
            }
            mUiHandler.sendEmptyMessage(OP_RX_FAIL);
        }

        Log.i("@M_" + TAG, "Leave GetValuesAndSend().");
    }

    private void getResult() {
        if (mBtTest == null) {
            return;
        }

        mResult = mBtTest.noSigRxTestResult();
        if (mResult == null) {
            Log.i("@M_" + TAG, "no signal rx test failed.");
            if ((BluetoothAdapter.STATE_TURNING_ON == mStateBt)
                    || (BluetoothAdapter.STATE_ON == mStateBt)) {
                enableBluetooth(true);
            }
            // Toast.makeText(getApplicationContext(),
            // "no signal rx test failed.",
            // Toast.LENGTH_SHORT).show();
            // showDialog(DIALOG_RX_FAIL);
            mUiHandler.sendEmptyMessage(OP_RX_FAIL);

        } else {
            mUiHandler.sendEmptyMessage(OP_TEST_OK_STEP2);

        }

        Log.i("@M_" + TAG, "Leave getresult().");
    }

}
