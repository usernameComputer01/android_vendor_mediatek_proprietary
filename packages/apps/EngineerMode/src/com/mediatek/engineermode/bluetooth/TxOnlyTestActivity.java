

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mediatek.engineermode.R;

/**
 * Do BT tx mode test.
 *
 * @author mtk54040
 *
 */
public class TxOnlyTestActivity extends Activity implements
        DialogInterface.OnCancelListener {

    private BluetoothAdapter mAdapter;

    private Spinner mPattern = null;
    private Spinner mChannels = null;
    private Spinner mPktTypes = null;

    private BtTest mBtTest = null;

    private static final int BT_TEST_0 = 0;
    private static final int BT_TEST_3 = 3;
    private static final int RETURN_FAIL = -1;

    // dialog ID and MSG ID
    private static final int CHECK_BT_STATE = 1;
//    private static final int CHECK_BT_DEVEICE = 2;
    private static final int TEST_TX = 3;

    private static final int DIALOG_BT_STOP = 4;


    private boolean mHasInit = false;
    private boolean mIniting = false;
    private boolean mNonModulate = false;
    private boolean mPocketType = false;

    private static final int MAP_TO_PATTERN = 0;
    private static final int MAP_TO_CHANNELS = 1;
    private static final int MAP_TO_POCKET_TYPE = 2;
    private static final int MAP_TO_FREQ = 3;
    private static final int MAP_TO_POCKET_TYPE_LEN = 4;

    private static final String TAG = "TxOnlyTestLOG";

    private int mStateBt;
    // added by chaozhong @2010-10-10
    private Handler mWorkHandler = null; // used to handle the
    private HandlerThread mWorkThread = null;
    // "done"
    // button clicked action
    private boolean mDoneTest = true; // used to record weather the

    private boolean mDumpStart = false;
    // "done" button clicked
    // event has been finished
    // private HandlerThread mHandlerThread = null;
    public static final int DLGID_OP_IN_PROCESS = 1;
    public static final int OP_IN_PROCESS = 2;
    public static final int OP_FINISH = 0;
    public static final int OP_TX_FAIL = 4;

    public static final int UI_BT_CLOSE = 5;
    public static final int UI_BT_CLOSE_FINISHED = 6;

    private static final int OP_BT_SEND = 11;
    private static final int OP_BT_STOP = 12;

    // private static Handler mUiHandler = null;
    // ProgressDialog mDialogSearchProgress = null;

    // end added
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.v("@M_" + TAG, "-->onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_only_test);
        // Initialize the UI component
        setValuesSpinner();

        mWorkThread = new HandlerThread(TAG);
        mWorkThread.start();
        Looper looper = mWorkThread.getLooper();
        mWorkHandler = new WorkHandler(looper);

    }

    private Handler mUiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case OP_IN_PROCESS:
                // {
                Log.w("@M_" + TAG, "OP_IN_PROCESS");
                showDialog(TEST_TX);
                break;
            case OP_FINISH:
                Log.i("@M_" + TAG, "OP_FINISH");
                removeDialog(TEST_TX);
                break;
            case OP_TX_FAIL:
                Log.i("@M_" + TAG, "OP_TX_FAIL");
                // showDialog(DIALOG_CHECK_BT_DEVEICE);
                removeDialog(TEST_TX);
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
    private final class WorkHandler extends Handler {
        private WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OP_BT_SEND) {
                mUiHandler.sendEmptyMessage(OP_IN_PROCESS);
                mDoneTest = false;
                // do stop
                if (mDumpStart == true) {
                    if (mBtTest != null) {
                        Log.i("@M_" + TAG, "pollingStop");
                        mBtTest.pollingStop();
                        if (RETURN_FAIL == mBtTest.doBtTest(BT_TEST_3)) { // bt deinit
                            Log.i("@M_" + TAG, "stop failed.");
                        }
                    }
                }
                doSendCommandAction();
                // do start
                if (mBtTest != null && mHasInit) {
                    Log.i("@M_" + TAG, "pollingStart");
                    mBtTest.pollingStart();
                    mDumpStart = true;
                }
                mDoneTest = true;
                mUiHandler.sendEmptyMessage(OP_FINISH);
            } else if (msg.what == OP_BT_STOP) {
                mUiHandler.sendEmptyMessage(UI_BT_CLOSE);
                // do stop
                if (mDumpStart == true) {
                    if (mBtTest != null) {
                        Log.i("@M_" + TAG, "pollingStop");
                        mBtTest.pollingStop();
                    }
                }
                if (RETURN_FAIL == mBtTest.doBtTest(BT_TEST_3)) {
                    Log.i("@M_" + TAG, "stop failed.");
                }
                mBtTest = null;
                mUiHandler.sendEmptyMessage(UI_BT_CLOSE_FINISHED);
            }
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem doneItem = menu.getItem(Menu.FIRST - 1);
        if (null != doneItem) {
            if (!mDoneTest) {
                doneItem.setEnabled(false);
                menu.close();
            } else {
                doneItem.setEnabled(true);
            }
        } else {
            Log.i("@M_" + TAG, "menu_done is not found.");
        }
        return true;
    }

    /**
     * Initialize the UI component
     *
     *
     */
    protected void setValuesSpinner() {
        // for TX pattern
        mPattern = (Spinner) findViewById(R.id.PatternSpinner);
        ArrayAdapter<CharSequence> adapterPattern = ArrayAdapter
                .createFromResource(this, R.array.tx_pattern,
                        android.R.layout.simple_spinner_item);
        adapterPattern
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPattern.setAdapter(adapterPattern);

        // for TX channels
        mChannels = (Spinner) findViewById(R.id.ChannelsSpinner);
        ArrayAdapter<CharSequence> adapterChannels = ArrayAdapter
                .createFromResource(this, R.array.tx_channels,
                        android.R.layout.simple_spinner_item);
        adapterChannels
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChannels.setAdapter(adapterChannels);

        // for TX pocket type
        mPktTypes = (Spinner) findViewById(R.id.PocketTypeSpinner);
        ArrayAdapter<CharSequence> adapterPocketType = ArrayAdapter
                .createFromResource(this, R.array.tx_Pocket_type,
                        android.R.layout.simple_spinner_item);
        adapterPocketType
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPktTypes.setAdapter(adapterPocketType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_done:
            // edited by chaozhogn @ 2010-10-10
            // return doSendCommandAction();
            Log.i("@M_" + TAG, "menu_done is clicked.");
            if (mDoneTest) {
                // if the last click action has been handled, send another event
                // request
               // mWorkHandler.post(new WorkRunnable());
                mWorkHandler.sendEmptyMessage(OP_BT_SEND);
            } else {
                Log.i("@M_" + TAG, "last click is not finished yet.");
            }
            Log.i("@M_" + TAG, "menu_done is handled.");
            return true;
            // edit end

        case R.id.menu_discard:
            return doRevertAction();
        default:
            break;
        }
        return false;
    }

    /**
     * Send command the user has made, and finish the activity.
     */
    private boolean doSendCommandAction() {
        getBtState();
        enableBluetooth(false);
        getValuesAndSend();
        return true;
    }

    // implemented for DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialog) {
        // request that the service stop the query with this callback
        // mBtTestect.
        Log.v("@M_" + TAG, "-->onCancel");
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("@M_" + TAG, "-->onStart");
        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mAdapter.getState() != BluetoothAdapter.STATE_OFF) {
            showDialog(CHECK_BT_STATE);
        }
//        if (mAdapter != null) {
//            if (mAdapter.getState() != BluetoothAdapter.STATE_OFF) {
//                showDialog(CHECK_BT_STATE);
//            }
//        } else {
//            showDialog(CHECK_BT_DEVEICE);
//        }
    }


    @Override
    public void onBackPressed() {
        Log.v("@M_" + TAG, "-->onBackPressed ");
        removeDialog(TEST_TX);
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

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.i("@M_" + TAG, "-->onCreateDialog");
        if (id == TEST_TX) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_init_dev));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            Log.i("@M_" + TAG, "new ProgressDialog succeed");
            return dialog;
        } else if (id == CHECK_BT_STATE) {
            AlertDialog dialog = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.BT_turn_bt_off);
            builder.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog = builder.create();
            return dialog;
        } else if (id == DIALOG_BT_STOP) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.BT_deinit));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            // dialog.show();
            return dialog;
        }
        return null;
    }

    private void getBtState() {
        Log.v("@M_" + TAG, "Enter GetBtState().");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == btAdapter) {
            Log.i("@M_" + TAG, "we can not find a bluetooth adapter.");
            // Toast.makeText(getApplicationContext(),
            // "We can not find a bluetooth adapter.", Toast.LENGTH_SHORT)
            // .show();
            mUiHandler.sendEmptyMessage(OP_TX_FAIL);
            return;
        }
        mStateBt = btAdapter.getState();
        Log.i("@M_" + TAG, "Leave GetBtState().");
    }

    private void enableBluetooth(boolean enable) {
        Log.v("@M_" + TAG, "Enter EnableBluetooth().");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == btAdapter) {
            Log.i("@M_" + TAG, "we can not find a bluetooth adapter.");
            return;
        }
        // need to enable
        if (enable) {
            Log.i("@M_" + TAG, "Bluetooth is enabled");
            btAdapter.enable();
        } else {
            // need to disable
            Log.i("@M_" + TAG, "Bluetooth is disabled");
            btAdapter.disable();
        }
        Log.i("@M_" + TAG, "Leave EnableBluetooth().");
    }

    /**
     * Revert any changes the user has made, and finish the activity.
     */
    private boolean doRevertAction() {
        //finish();
        Log.i("@M_" + TAG, "doRevertAction");
        onBackPressed();
        return true;
    }

    // private BtTest mBtTest = null;

    public void getValuesAndSend() {
        Log.i("@M_" + TAG, "Enter GetValuesAndSend().");
        mBtTest = new BtTest();

        getSpinnerValue(mPattern, MAP_TO_PATTERN);
        getSpinnerValue(mChannels, MAP_TO_CHANNELS);
        getSpinnerValue(mPktTypes, MAP_TO_POCKET_TYPE);

        getEditBoxValue(R.id.edtFrequency, MAP_TO_FREQ);
        getEditBoxValue(R.id.edtPocketLength, MAP_TO_POCKET_TYPE_LEN);

        // send command to....
        // new issue added by mtk54040 Shuaiqiang @2011-10-12
        Log.i("@M_" + TAG, "PocketType().+" + mBtTest.getPocketType());
        Log.i("@M_" + TAG, "edtFrequency+" + mBtTest.getFreq());
        if (27 == mBtTest.getPocketType()) {
            Log.i("@M_" + TAG, "enter handleNonModulated(mBtTest)");
            Log.i("@M_" + TAG, "mbIsNonModulate--" + mNonModulate
                    + "   mbIsPocketType--" + mPocketType);
            if (mPocketType) { // mbIsPocketType for avoid mBtTest is null
                runHCIResetCmd();
            }
            if (initBtTestOjbect()) {
                mNonModulate = true;
                mPocketType = false;
//                    handleNonModulated(mBtTest);
                handleNonModulated();
            }

        } else {
            if (mNonModulate) {
                runHCIResetCmd();
                mNonModulate = false;
            }
            mPocketType = true;
            if (RETURN_FAIL == mBtTest.doBtTest(BT_TEST_0)) {
                Log.i("@M_" + TAG, "transmit data failed.");
                if ((BluetoothAdapter.STATE_TURNING_ON == mStateBt)
                        || (BluetoothAdapter.STATE_ON == mStateBt)) {
                    enableBluetooth(true);
                }
                // Toast.makeText(getApplicationContext(),
                // "transmit data failed.", Toast.LENGTH_SHORT).show();
                mUiHandler.sendEmptyMessage(OP_TX_FAIL);
                mHasInit = false;
            } else {
                mHasInit = true;
            }
        }
        Log.i("@M_" + TAG, "Leave getValuesAndSend().");
    }

    private void handleNonModulated() {
        Log.i("@M_" + TAG, "-->handleNonModulated TX first");
        /*
         * If pressing "Stop" button Tx: 01 0C 20 02 00 PP 0xPP = Filter
         * Duplicate (00 = Disable Duplicate Filtering, 01 = Enable Duplicate
         * Filtering) Rx: 04 0E 04 01 0C 20 00
         */

        /*
         * TX: 01 15 FC 01 00 RX: 04 0E 04 01 15 FC 00 TX: 01 D5 FC 01 XX (XX =
         * Channel) RX: 04 0E 04 01 D5 FC 00
         */
        int cmdLen = 5;
        char[] cmd = new char[cmdLen];
        char[] response = null;
        int i = 0;
        cmd[0] = 0x01;
        cmd[1] = 0x15;
        cmd[2] = 0xFC;
        cmd[3] = 0x01;
        cmd[4] = 0x00;
        response = mBtTest.hciCommandRun(cmd, cmdLen);
        if (response != null) {
            String s = null;
            for (i = 0; i < response.length; i++) {
                s = String.format("response[%d] = 0x%x", i, (long) response[i]);
                Log.i("@M_" + TAG, s);
            }
        } else {
            Log.i("@M_" + TAG, "HCICommandRun failed");
        }
        response = null;

        Log.i("@M_" + TAG, "-->handleNonModulated TX second");
        cmdLen = 5;
        cmd[0] = 0x01;
        cmd[1] = 0xD5;
        cmd[2] = 0xFC;
        cmd[3] = 0x01;
        cmd[4] = (char) mBtTest.getFreq();
        response = mBtTest.hciCommandRun(cmd, cmdLen);
        if (response != null) {
            String s = null;
            for (i = 0; i < response.length; i++) {
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
        if (mIniting) {
            return false;
        }
        if (mHasInit) {
            return mHasInit;
        }
        if (mBtTest == null) {
            mBtTest = new BtTest();
        }
        if (mBtTest != null && !mHasInit) {
            mIniting = true;
            if (mBtTest.init() != 0) {
                mHasInit = false;
                Log.i("@M_" + TAG, "mBtTest initialization failed");
            } else {
                runHCIResetCmd();
                mHasInit = true;
            }
        }
        mIniting = false;
        return mHasInit;
    }

    // clear BtTest mBtTestect -call deInit function of BtTest
    // private boolean uninitBtTestOjbect() {
    // Log.i("@M_" + TAG, "-->uninitBtTestOjbect");
    // if (mBtTest != null && mbIsInit) {
    // runHCIResetCmd();
    // if (mBtTest.unInit() != 0) {
    // Log.i("@M_" + TAG, "mBtTest un-initialization failed");
    // }
    // }
    // mBtTest = null;
    // mbIsInit = false;
    // return true;
    // }

    // run "HCI Reset" command
    private void runHCIResetCmd() {
        /*
         * If pressing "HCI Reset" button Tx: 01 03 0C 00 Rx: 04 0E 04 01 03 0C
         * 00 After pressing "HCI Reset" button, all state will also be reset
         */
        int cmdLen = 4;
        char[] cmd = new char[cmdLen];

        char[] response = null;
        int i = 0;
        Log.i("@M_" + TAG, "-->runHCIResetCmd");
        cmd[0] = 0x01;
        cmd[1] = 0x03;
        cmd[2] = 0x0C;
        cmd[3] = 0x00;
        if (mBtTest == null) {
            mBtTest = new BtTest();
        }
        response = mBtTest.hciCommandRun(cmd, cmdLen);
        if (response != null) {
            String s = null;
            for (i = 0; i < response.length; i++) {
                s = String.format("response[%d] = 0x%x", i, (long) response[i]);
                Log.i("@M_" + TAG, s);
            }
        } else {
            Log.i("@M_" + TAG, "HCICommandRun failed");
        }
        response = null;
    }

    private boolean getSpinnerValue(Spinner sSpinner, int flag) {
        boolean bSuccess = false;
        int index = sSpinner.getSelectedItemPosition();
        if (0 > index) {
            return bSuccess;
        }

        switch (flag) {
        case MAP_TO_PATTERN: // Pattern
            mBtTest.setPatter(index);
            break;
        case MAP_TO_CHANNELS: // Channels
            mBtTest.setChannels(index);
            break;
        case MAP_TO_POCKET_TYPE: // Pocket type
            mBtTest.setPocketType(index);
            break;
        default:
            bSuccess = false;
            break;
        }
        bSuccess = true;
        return bSuccess;
    }

    private boolean getEditBoxValue(int id, int flag) {
        boolean bSuccess = false;

        TextView text = (TextView) findViewById(id);
        String str = null;
        if (null != text) {
            str = text.getText().toString();
        }
        if ((null == str) || str.equals("")) {
            return bSuccess;
        }
        int iLen = 0;
        try {
            iLen = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.i("@M_" + TAG, "parseInt failed--invalid number!");
            return bSuccess;
        }
        // frequency
        if (MAP_TO_FREQ == flag) {
            mBtTest.setFreq(iLen);
            bSuccess = true;
        } else if (MAP_TO_POCKET_TYPE_LEN == flag) {
            // pocket type length
            mBtTest.setPocketTypeLen(iLen);
            bSuccess = true;
        }
        return bSuccess;
    }
}
