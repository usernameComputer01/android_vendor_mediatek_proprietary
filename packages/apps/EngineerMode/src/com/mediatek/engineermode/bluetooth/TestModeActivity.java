

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
import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.engineermode.R;

import java.io.IOException;

/**
 * Do BT test mode.
 *
 * @author mtk54040
 *
 */
public class TestModeActivity extends Activity implements
        DialogInterface.OnCancelListener {


    private static final String TAG = "TestMode";

    private static final String RUN_SU = "su";
    // private static final int HANDLER_TEST_FAILED = 0;

    private static final int CHECK_BT_STATE = 1;
    private static final int BLUETOOTH_INIT = 2;
    private static final int DIALOG_BT_STOP = 3;
//    private static final int CHECK_BT_DEVEICE = 3;
    private static final int BT_TEST_1 = 1;
    private static final int BT_TEST_2 = 2;
    private static final int RETURN_FAIL = -1;
    private static final int DEFAULT_INT = 7;
    private static final String DEFAULT_STR = "7";

    private static final int BT_TEST_SUCCESS = 5;
    private static final int BT_TEST_FAIL = 6;
    private static final int BT_TEST_STOP_SUCCESS = 7;

    private static final int OP_BT_TEST_1 = 11;
    private static final int OP_DO_TEST_2 = 12;
    private static final int OP_DO_TEST_STOP = 13;

    private BluetoothAdapter mAdapter;
    private CheckBox mChecked;
    private EditText mTestModeEdit;

    private BtTest mBtTest;
//    private HandlerThread mWorkThread = null;
    private WorkHandler mWorkHandler = null;
    private HandlerThread mWorkThread = null;

    private final CheckBox.OnCheckedChangeListener mCheckedListener = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
            boolean ischecked = mChecked.isChecked();
            // is checked
            if (ischecked) {
                showDialog(BLUETOOTH_INIT);
                // String val = mTestModeEdit.getEditableText().toString();
                String val = mTestModeEdit.getText().toString();
                if (val == null || val.length() < 1) {
                    // mTestModeEdit.setText("7");
                    // val = "7";
                    mTestModeEdit.setText(DEFAULT_STR);
                    val = DEFAULT_STR;
                }
                // int v = 7;
                int v = DEFAULT_INT;
                try {
                    v = Integer.valueOf(val);
                } catch (NumberFormatException e) {
                    Log.i("@M_" + TAG, e.getMessage());
                }

                if (v > DEFAULT_INT) {
                    mTestModeEdit.setText(DEFAULT_STR);
                }
                mWorkHandler.sendEmptyMessage(OP_BT_TEST_1);

            } else {
                showDialog(BLUETOOTH_INIT);
                mWorkHandler.sendEmptyMessage(OP_DO_TEST_2);
            }
        }
    };

    private final Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == BT_TEST_SUCCESS) {
//
//            } else if (msg.what == BT_TEST_FAIL) {
            if (msg.what == BT_TEST_FAIL) {
                Toast.makeText(getApplicationContext(),
                        R.string.BT_data_fail,
                        Toast.LENGTH_SHORT).show();
            }
            if (msg.what == BT_TEST_STOP_SUCCESS) {
                finish();
            } else {
                removeDialog(BLUETOOTH_INIT);
                mChecked.setEnabled(true);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_mode);

        // CharSequence str1 = getString(R.string.strTestMode);
        TextView tv = (TextView) findViewById(R.id.TestModetxv);
        mTestModeEdit = (EditText) findViewById(R.id.BTTestMode_edit);

        // tv.setText(Html.fromHtml(str1.toString()));
        tv.setText(Html.fromHtml(getString(R.string.strTestMode)));

        mChecked = (CheckBox) findViewById(R.id.TestModeCb);
        mChecked.setOnCheckedChangeListener(mCheckedListener);

        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            mWorkThread = new HandlerThread(TAG);
            mWorkThread.start();

            Looper looper = mWorkThread.getLooper();
            mWorkHandler = new WorkHandler(looper);
//            mWorkHandler = new WorkHandler(mWorkThread.getLooper());
        } else {
            showDialog(CHECK_BT_STATE);
        }
    }

    /**
     *  implemented for DialogInterface.OnCancelListener.
     *
     *  @param dialog : dialog interface
     */
    public void onCancel(DialogInterface dialog) {
        // request that the service stop the query with this callback object.
        Log.v("@M_" + TAG, "onCancel");
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.v("@M_" + TAG, "-->onBackPressed ");

        if (mWorkHandler != null && mBtTest != null) {
            showDialog(DIALOG_BT_STOP);
            mWorkHandler.sendEmptyMessage(OP_DO_TEST_STOP);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        Log.v("@M_" + TAG, "-->onDestroy");
        // super.onDestroy();
        if (mWorkThread != null) {
            mWorkThread.quit();
        }
        // mBtTest.UnInit();
        super.onDestroy();
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog returnDialog = null;
        if (id == BLUETOOTH_INIT) {
            ProgressDialog dialog = new ProgressDialog(TestModeActivity.this);
            dialog.setMessage(getString(R.string.BT_init_dev));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.setOnCancelListener(this);
            returnDialog = dialog;
//            return dialog;
        } else if (id == CHECK_BT_STATE) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.Error)
                .setMessage(R.string.BT_turn_bt_off)
                .setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int which) {
                            finish();
                        }
                    }).create();
            returnDialog = dialog;
        } else if (id == DIALOG_BT_STOP) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_deinit));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            returnDialog = dialog;
        }

        return returnDialog;
    }

    private final class WorkHandler extends Handler {
        private WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == OP_BT_TEST_1) {
                final Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec(RUN_SU);
                    Log.v("@M_" + TAG, "excute su command.");
                } catch (IOException e) {
                    Log.v("@M_" + TAG, e.getMessage());
                }

                mBtTest = new BtTest();
                // String val = mTestModeEdit.getEditableText().toString();
                String val = mTestModeEdit.getText().toString();
                int tmpValue = 0;
                try {
                    tmpValue = Integer.valueOf(val);
                } catch (NumberFormatException e) {
                    Log.i("@M_" + TAG, e.getMessage());
                }

                mBtTest.setPower(tmpValue);
                Log.i("@M_" + TAG, "power set " + val);
                if (RETURN_FAIL == mBtTest.doBtTest(BT_TEST_1)) {
                    // if (-1 == mBtTest.doBtTest(1)) {
                    Log.v("@M_" + TAG, "transmit data failed.");
                    // removeDialog(DIALOG_BLUETOOTH_INIT);
                    mUiHandler.sendEmptyMessage(BT_TEST_FAIL);
                } else {
                    mUiHandler.sendEmptyMessage(BT_TEST_SUCCESS);
                }

                Log.i("@M_" + TAG, "pollingStart");
                mBtTest.pollingStart();

            } else if (msg.what == OP_DO_TEST_2 || msg.what == OP_DO_TEST_STOP) {
                final Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec(RUN_SU);
                    Log.i("@M_" + TAG, "excute su command.");
                } catch (IOException e) {
                    Log.v("@M_" + TAG, e.getMessage());
                }

                if (mBtTest != null) {
                    Log.i("@M_" + TAG, "pollingStop");
                    mBtTest.pollingStop();
                    if (RETURN_FAIL == mBtTest.doBtTest(BT_TEST_2)) {
                        // if (-1 == mBtTest.doBtTest(2)) {
                        Log.i("@M_" + TAG, "transmit data failed 1.");
                    }
                    mBtTest = null;
                }
                if (msg.what == OP_DO_TEST_2) {
                    mUiHandler.sendEmptyMessage(BT_TEST_SUCCESS);
                } else if (msg.what == OP_DO_TEST_STOP) {
                    mUiHandler.sendEmptyMessage(BT_TEST_STOP_SUCCESS);
                }
            }
        }
    }
}
