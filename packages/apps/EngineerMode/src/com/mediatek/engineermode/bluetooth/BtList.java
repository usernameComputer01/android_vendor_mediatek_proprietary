

package com.mediatek.engineermode.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediatek.engineermode.R;

import java.util.ArrayList;

/**
 * Show Bluetooth test modules.
 *
 * @author mtk54040
 *
 */
public class BtList extends ListActivity {
    private static final String TAG = "EM/BTList";

    // dialog ID and MSG ID
    private static final int CHECK_BT_STATE = 1;
    // private static final int CHECK_BLE_FINISHED = 4;
    private static final int CHECK_BLE = 2;
//    private static final int CHECK_BT_DEVEICE = 3;

    private static final int NUM_RADIX = 16;
    private static final int RENTURN_SUCCESS = 0;

    private static final int SLEEP_TIME = 300;

    private BluetoothAdapter mBtAdapter;

    // flags
    // private boolean mBtStateErr = false; // bt opened or not found
    private boolean mBleSupport = false; // mHasBleSupport
    private boolean mDoubleFlag = false; // for quick back CR
    private boolean mComboSupport = false;

    // private Handler mHandler = null;
//    private int mChipId = 0x6620;
    private int mChipId = 0;
    private BtTest mBtTest = null;
    private ArrayList<String> mModuleList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btlist);
        // mThisActivity = this; // no sense

        mModuleList = new ArrayList<String>();
        // mModuleList.add("TX Only test"); // put items in array.xml

        // for tingting's request remove this "bt chip info" item
        // mModuleList.add(getString(R.string.BT_chipTitle));
        mModuleList.add(getString(R.string.BT_tx_only_Title));

        // comments
        // add for ble test 2010-11.30
        // before add the following 5 feature into the listview,
        // we need to read
        // the "BLE feature bit" to confirm chip support BLE feature
        //
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mModuleList);
        setListAdapter(moduleAdapter);
        mModuleList.clear();
        if (mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
//        if (mBtAdapter == null) {
//            showDialog(CHECK_BT_DEVEICE);
//        } else {
        if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            showDialog(CHECK_BLE);
            FunctionTask functionTask = new FunctionTask();
            functionTask.execute();
        } else {
            showDialog(CHECK_BT_STATE);
        }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDoubleFlag = false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d("@M_" + TAG, "-->onCreateDialog");
        if (id == CHECK_BLE) {
            ProgressDialog dialog = new ProgressDialog(this);

            // dialog.setTitle("Progress");
            // dialog.setMessage("Please wait for device to initialize ...");
            dialog.setMessage(getString(R.string.BT_init_dev));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            // dialog.show();
            return dialog;
        } else if (id == CHECK_BT_STATE) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.BluetoothTitle)
                .setMessage(R.string.BT_turn_bt_off)
                .setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int which) {
                            finish();
                        }
                    }).create();
            return dialog;

//        } else if (id == CHECK_BT_DEVEICE) {
//            // "Error").setMessage("Can't find any bluetooth device")
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                .setCancelable(false)
//                .setTitle(R.string.Error)
//                .setMessage(R.string.BT_no_dev)
//                .setPositiveButton(R.string.OK,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,
//                                int which) {
//                            finish();
//                        }
//                    }).create();
//            return dialog;
        }

        return null;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!mDoubleFlag) {
            if (mModuleList.get(position).equals(
                    getString(R.string.BT_tx_only_Title))) {
                startActivity(new Intent(BtList.this, TxOnlyTestActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BTNSRXTitle))) {
                startActivity(new Intent(BtList.this, NoSigRxTestActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BTTMTitle))) {
                startActivity(new Intent(BtList.this, TestModeActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BTSSPDMTitle))) {
                startActivity(new Intent(BtList.this,
                        SspDebugModeActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BT_chipTitle))) {
                startActivity(new Intent(BtList.this, BtChipInfoActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BT_RelayerModeTitle))) {
                startActivity(new Intent(BtList.this,
                        BtRelayerModeActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.BtDebugFeatureTitle))) {
                startActivity(new Intent(BtList.this,
                        BtDebugFeatureActivity.class));
            } else if (mModuleList.get(position).equals(
                    getString(R.string.ClockSelectionTitle))) {
                startActivity(new Intent(BtList.this,
                        BtClockSelectionActivity.class));
            }

            if (mBleSupport && mModuleList.get(position)
                    .equals(getString(R.string.BT_ble_test_mode_Title))) {
                Log.v("@M_" + TAG, "BLE_Test_Mode item is selected");
                startActivity(new Intent(BtList.this, BleTestMode.class));
            }
            mDoubleFlag = true;
        }
    }

    public class FunctionTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            mBtTest = new BtTest();
            if (mBtAdapter == null) {
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                // check if BLE is supported or not
                if (mBtTest.isBLESupport() == 1) {
                    mBleSupport = true;
                } else {
                    mBleSupport = false;
                }

                if (mBtTest.isComboSupport() == 1) {
                    mComboSupport = true;
                } else {
                    mComboSupport = false;
                }
                Log.i("@M_" + TAG, "BLE supported ? " + mBleSupport);
                mChipId = mBtTest.getChipId();
            } else {
                // if Bluetooth is in ON state, just sleep for 500
                // ms and send the message to main thread
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    Log.d("@M_" + TAG, e.getMessage());
                }
            }

            // mHandler.sendEmptyMessage(CHECK_BLE_FINISHED);
            // mbIsDialogShowed = true;

            return RENTURN_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // mHasBleFeatureDetected = true;
            mModuleList = new ArrayList<String>();

            // mModuleList.add("Chip Information");
            // mModuleList.add("TX Only test");
// hide chip info
//            mModuleList.add(getString(R.string.BT_chipTitle));
            mModuleList.add(getString(R.string.BT_tx_only_Title));

//            int chipId = mBtTest.getChipId();
            Log.v("@M_" + TAG, "chipId@" + mChipId);
            Log.v("@M_" + TAG, "6620@" + getString(R.string.BT_0x6620));
            Log.v("@M_" + TAG, "0x6620@"
                    + Integer.parseInt(getString(R.string.BT_0x6620),
                            NUM_RADIX));

            if (Integer.parseInt(getString(R.string.BT_0x6622), NUM_RADIX) != mChipId) {
                // if (0x6622 != chipId) {
                // mModuleList.add("Non-signaling RX Test");
                mModuleList.add(getString(R.string.BTNSRXTitle));
            }

            // mModuleList.add("Test Mode");
            // mModuleList.add("SSP Debug Mode");
            mModuleList.add(getString(R.string.BTTMTitle));
            mModuleList.add(getString(R.string.BTSSPDMTitle));

            if (mBleSupport) {
                // mModuleList.add("BLE Test Mode");
                mModuleList.add(getString(R.string.BT_ble_test_mode_Title));
            }

            if (mComboSupport) {
                mModuleList.add(getString(R.string.BT_RelayerModeTitle));
            }
            mModuleList.add(getString(R.string.BtDebugFeatureTitle));
            mModuleList.add(getString(R.string.ClockSelectionTitle));
            ArrayAdapter<String> moduleAdapter = new ArrayAdapter<String>(
                    BtList.this, android.R.layout.simple_list_item_1,
                    mModuleList);
            BtList.this.setListAdapter(moduleAdapter);


            removeDialog(CHECK_BLE);

            super.onPostExecute(result);
        }

    }
}
