

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import com.mediatek.engineermode.R;

/**
 * Set uart info and test relayer mode.
 *
 * @author mtk54040
 *
 */
public class BtRelayerModeActivity extends Activity implements OnClickListener {
    private static final String TAG = "EM/BT/RelayerMode";
    // dialog ID and MSG ID
    private static final int START_TEST = 1;
    private static final int END_TEST = 2;

    private static final int RENTURN_SUCCESS = 0;
//    private static final int RENTURN_FAIL = -1;

    private static final int EXIT_SUCCESS = 10;
    private static final int PARA_INDEX = 0;
    // UI
    private Spinner mBauSpinner;
    private Spinner mUartSpinner;
    private Button mStartBtn;

//    private ArrayAdapter<String[]> mBaudrateAdpt;
//    private ArrayAdapter<String[]> mUartAdapter;
    private BtTest mBtTest = null; // null
    private boolean mStartFlag = false;
    private int mBaudrate = 9600;
    private int mPortNumber = 3;



    @Override
    public void onCreate(Bundle savedInstanceState) {

//        Log.v("@M_" + TAG, "-->onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_relayer_mode);
        // Init UI component
        mBauSpinner = (Spinner) findViewById(R.id.spinner1);
        mUartSpinner = (Spinner) findViewById(R.id.spinner2);
        mUartSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        if (arg2 == 4) {  // select usb
                            mBauSpinner.setEnabled(false);
                        } else {
                            mBauSpinner.setEnabled(true);
                        }
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                        Log.v("@M_" + TAG, "onNothingSelected");
                    }
                });
        mStartBtn = (Button) findViewById(R.id.button1);
/*
        // Fill baudrate content
        ArrayAdapter<String[]> mBaudrateAdpt = new ArrayAdapter<String[]>(this,
                android.R.layout.simple_spinner_item);
        mBaudrateAdpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBaudrateAdpt
                .add(getResources().getStringArray(R.array.bt_baudrate));

        // Fill uart content
        ArrayAdapter<String[]> mUartAdapter = new ArrayAdapter<String[]>(this,
                android.R.layout.simple_spinner_item);
        mUartAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mUartAdapter.add(getResources().getStringArray(R.array.bt_uart));
*/
        mStartBtn.setOnClickListener(this);

    }

    public void onClick(View v) {
        Log.v("@M_" + TAG, "-->onClick");
        Log.v("@M_" + TAG, "mStartFlag--" + mStartFlag);
        if (v.getId() == mStartBtn.getId()) {

            Log.i("@M_" + TAG, "mBtRelayerModeSpinner.getSelectedItem()--"
                    + mBauSpinner.getSelectedItem());
            try {
                mBaudrate = Integer.parseInt(mBauSpinner
                        .getSelectedItem().toString().trim());
                Log.v("@M_" + TAG, "mBaudrate--" + mBaudrate);

            } catch (NumberFormatException e) { // detail info
                Log.v("@M_" + TAG, e.getMessage());
            }

            // Log.i("@M_" + TAG, "mSerialPortSpinner()--"
            // + mSerialPortSpinner.getSelectedItem());
            // Log.i("@M_" + TAG, "id--" + mSerialPortSpinner.getSelectedItemId());
            // mPortNumber = (int) mSerialPortSpinner.getSelectedItemId(); //
            // use method to convert int
            Long tmpLong = mUartSpinner.getSelectedItemId();
            mPortNumber = tmpLong.intValue();
            Log.i("@M_" + TAG, "mPortNumber--" + mPortNumber);
            FunctionTask functionTask = new FunctionTask();

            // Disable button to avoid multiple click
            mStartBtn.setEnabled(false);
            if (mStartFlag) {
//                mStartFlag = false;
                functionTask.execute(END_TEST);
                mStartBtn.setText("Start");
            } else {
//                mStartFlag = true; // add violate
                showDialog(START_TEST);
                functionTask.execute(START_TEST);
            }
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.v("@M_" + TAG, "-->onCreateDialog");
        if (id == START_TEST) {
            ProgressDialog dialog = new ProgressDialog(
                    BtRelayerModeActivity.this);
            dialog.setMessage(getString(R.string.BT_relayer_start));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);

            return dialog;
        }
        return null;
    }

    /**
     * Deal with function request.
     *
     * @author mtk54040
     *
     */
    public class FunctionTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = RENTURN_SUCCESS;
            mBtTest = new BtTest();
            int paraValue = params[PARA_INDEX];
            if (paraValue == START_TEST) {
                result = mBtTest.relayerStart(mPortNumber, mBaudrate);
//                mStartFlag = true;
                Log.v("@M_" + TAG, "-->relayerStart-" + mBaudrate + " uart "
                        + mPortNumber + "result 0 success,-1 fail: result= "
                        + result);
            } else if (paraValue == END_TEST) {
                mBtTest.relayerExit();
                mStartFlag = false;
                result = EXIT_SUCCESS;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == RENTURN_SUCCESS) {       // START TEST OK
                mStartBtn.setText("END Test");
                mStartFlag = true;
            }
            // remove dialog
            removeDialog(START_TEST);

            // Enable next click operation
            mStartBtn.setEnabled(true);
//            super.onPostExecute(result);
        }

    }
}
