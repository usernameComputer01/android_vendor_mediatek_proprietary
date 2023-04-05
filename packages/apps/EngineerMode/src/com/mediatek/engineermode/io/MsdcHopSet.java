

package com.mediatek.engineermode.io;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mediatek.engineermode.ChipSupport;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class MsdcHopSet extends MsdcTest implements OnClickListener {

    private static final String TAG = "MSDC_HOPSET_IOCTL";
    private static final int DATA_BIT = 0xF;
    private static final int OFFSET_HOP_BIT = 24;
    private static final int OFFSET_TIME_BIT = 28;

    private Spinner mHostSpinner;
    private int mHostIndex = 0;
    private Spinner mHoppingBitSpinner;
    private int mHoppingBitIndex = 0;
    private Spinner mHoppingTimeSpinner;
    private int mHoppingTimeIndex = 0;
    private Button mBtnGet;
    private Button mBtnSet;
    private boolean mIsNewChip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msdc_hopset);
        mIsNewChip = ChipSupport.MTK_6589_SUPPORT <= ChipSupport.getChip();
        mBtnGet = (Button) findViewById(R.id.MSDC_HopSet_Get);
        mBtnSet = (Button) findViewById(R.id.MSDC_HopSet_Set);
        mHostSpinner = (Spinner) findViewById(R.id.MSDC_HopSet_HOST_sppiner);
        mHoppingBitSpinner = (Spinner) findViewById(R.id.MSDC_hopping_bit_spinner);
        mHoppingTimeSpinner = (Spinner) findViewById(R.id.MSDC_hopping_time_spinner);
        mBtnGet.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);
        Resources res = getResources();
        String[] itemArray = res.getStringArray(R.array.host_type);
        ArrayList<String> itemList = new ArrayList<String>();
        for (int i = 0; i < itemArray.length - 1; i++) {
            itemList.add(itemArray[i]);
        }
        if (mIsNewChip) {
            itemList.add(itemArray[itemArray.length - 1]);
        }
        ArrayAdapter<String> hostAdaprer = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemList);
        hostAdaprer
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mHostSpinner.setAdapter(hostAdaprer);

        mHostSpinner.setOnItemSelectedListener(mSpinnerListener);

        ArrayAdapter<String> hopBitAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.hopping_bit));
        hopBitAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mHoppingBitSpinner.setAdapter(hopBitAdapter);

        ArrayAdapter<String> hopTimeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.hopping_time));
        hopTimeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mHoppingBitSpinner.setAdapter(hopBitAdapter);
        mHoppingBitSpinner
                .setOnItemSelectedListener(mSpinnerListener);
        mHoppingTimeSpinner.setAdapter(hopTimeAdapter);
        mHoppingTimeSpinner
                .setOnItemSelectedListener(mSpinnerListener);

        mHostSpinner.setSelection(0);
    }

    public void onClick(View arg0) {
        if (arg0.getId() == mBtnGet.getId()) {

            int idx = EmGpio.newGetCurrent(mHostIndex, 1);
            Log.i("@M_" + TAG, "get Data: " + idx);
            if (idx != -1) {
                int mHopbitIdx = (idx >> OFFSET_HOP_BIT) & DATA_BIT;
                int mHoptimeIdx = (idx >> OFFSET_TIME_BIT) & DATA_BIT;
                mHoppingBitSpinner.setSelection(mHopbitIdx);
                mHoppingTimeSpinner.setSelection(mHoptimeIdx);
            } else {
                showDialog(EVENT_GET_FAIL_ID);
            }

        } else if (arg0.getId() == mBtnSet.getId()) {

            boolean ret = EmGpio.newSetCurrent(mHostIndex, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    mHoppingBitIndex, mHoppingTimeIndex, 1);
            if (ret) {
                showDialog(EVENT_SET_OK_ID);
            } else {
                showDialog(EVENT_SET_FAIL_ID);
            }
        }
    }

    private final OnItemSelectedListener mSpinnerListener = new OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            switch (arg0.getId()) {
            case R.id.MSDC_HopSet_HOST_sppiner:
                mHostIndex = arg2;
                break;
            case R.id.MSDC_hopping_bit_spinner:
                mHoppingBitIndex = arg2;
                break;
            case R.id.MSDC_hopping_time_spinner:
                mHoppingTimeIndex = arg2;
                break;
            default:
                break;
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            Log.v("@M_" + TAG, "Spinner nothing selected");
        }

    };

}
