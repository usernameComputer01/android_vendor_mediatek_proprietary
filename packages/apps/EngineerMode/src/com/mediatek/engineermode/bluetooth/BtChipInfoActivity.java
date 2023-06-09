

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mediatek.engineermode.R;

/**
 * Show bluetooth chip infomation.
 * 
 * @author mtk54040
 * 
 */
public class BtChipInfoActivity extends Activity {
    private static final String TAG = "EM/BT/ChipInfo";

    // flags
    // private boolean mbIsDialogShowed = false; // flag style
    // private boolean mbIsBLEFeatureDetected = false;
    // dialog ID and MSG ID
    private static final int CHECK_BT_STATE = 1;
    private static final int GET_INFO = 2;
    // private static final int CHECK_BLE_FINISHED = 3;

    private static final int RESULT_SUCCESS = 0;

    private BluetoothAdapter mBtAdapter = null;
    // private Handler mUiHandler = null;

    private BtTest mBtTest = null;

    // string
    private String mChipId = "";
    private String mChipEco = "";
    private String mChipPatchId = "";
    private String mChipPatchLen = "";
    // TextView
    private TextView mChipIdTextView = null;
    private TextView mEcoVerTextView = null;
    private TextView mPatchSizeView = null;
    private TextView mPatchDateView = null;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
//        Log.v("@M_" + TAG, "onCreate"); // event log
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.bt_chip_info);
        
        // Initialize UI component
        mChipIdTextView = (TextView) findViewById(R.id.chipId);
        mEcoVerTextView = (TextView) findViewById(R.id.ecoVersion);
        mPatchSizeView = (TextView) findViewById(R.id.patchSize);
        mPatchDateView = (TextView) findViewById(R.id.patchDate);

        if (mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // Get chip info when bt is close
        if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            showDialog(GET_INFO);
            FunctionTask functionTask = new FunctionTask();
            functionTask.execute();
        } else {
            showDialog(CHECK_BT_STATE);
        }
        
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == GET_INFO) {
            ProgressDialog dialog = new ProgressDialog(BtChipInfoActivity.this);
            dialog.setMessage(getString(R.string.BT_init_dev));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            // dialog.show();
            Log.i("@M_" + TAG, "new ProgressDialog succeed");
            return dialog;
        } else if (id == CHECK_BT_STATE) {
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
        }

        return null;
    }

    public class FunctionTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            mBtTest = new BtTest();
            // Message msg = mUiHandler.obtainMessage(what);
            // check if BLE is supported or not
            if (mBtTest != null) {
                if (mBtTest.init() == RESULT_SUCCESS) { // get infomation from
                    // onCreate
                    // try {
                    mChipId = "";
                    String[] chipList = getResources().getStringArray(
                            R.array.bt_chip_id);
                    // Log.v("@M_" + TAG, "chipList.length" + "___" + chipList.length);
                    // Log.v("@M_" + TAG, "mBT.GetChipIdInt()" + "__" +
                    // mBT.GetChipIdInt());
                    int tmpId = mBtTest.getChipIdInt();
                    if (tmpId <= chipList.length) {
                        mChipId = chipList[tmpId];
                    }
                    Log.v("@M_" + TAG, "chipId@" + "___" + mChipId);

                    mChipEco = "";
                    String[] ecoList = getResources().getStringArray(
                            R.array.bt_chip_eco);
                    // Log.v("@M_" + TAG, "ecoList.length" + "___" + ecoList.length);
                    // Log.v("@M_" + TAG, "mBT.GetChipEcoNum()" + "__" +
                    // mBT.GetChipEcoNum());
                    int ecoIndex = mBtTest.getChipEcoNum();
                    if (ecoIndex <= ecoList.length) {
                        mChipEco = ecoList[ecoIndex];
                    }

                    Log.v("@M_" + TAG, "chipEco = " + mChipEco);
                    char[] patchIdArray = mBtTest.getPatchId();

                    mChipPatchId = new String(patchIdArray);
                    Log.v("@M_" + TAG, "chipPatchId@" + mChipPatchId.length() + "___"
                            + mChipPatchId);
                    mChipPatchLen = "" + mBtTest.getPatchLen(); // remove ""
                    Log.v("@M_" + TAG, "GetPatchLen=" + mChipPatchLen);

                    mBtTest.unInit();
                } else {
                    Log.i("@M_" + TAG, "new BtTest failed");
                }
                // mbIsBLEFeatureDetected = true;
                // removeDialog(CHECK_BLE);
            }

            return RESULT_SUCCESS;
        }
        
        /**
         * Display the bt chip information
         * 
         */
        @Override
        protected void onPostExecute(Integer result) {
            mChipIdTextView.setText(mChipId);
            mEcoVerTextView.setText(mChipEco);
            mPatchSizeView.setText(mChipPatchLen);
            mPatchDateView.setText(mChipPatchId);

            // mbIsBLEFeatureDetected = true;
            removeDialog(GET_INFO);
//            super.onPostExecute(result);
        }

    }

}
