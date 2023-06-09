
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

public class MsdcSd3Test extends MsdcTest implements OnClickListener {

    private static final String TAG = "EM/MSDC_SD30_TEST";


    private Spinner mSpinnerHost = null;
    private Spinner mSpinnerMode = null;
    private Spinner mSpinnerMaxCurrent = null;
    private Spinner mSpinnerDrive = null;
    private Spinner mSpinnerPowerControl = null;
    private Button mBtnGet;
    private Button mBtnSet;

    private int mIndexHost = 0;
    private int mIndexMode = 4;
    private int mIndexMaxCurrent = 0;
    private int mIndexDrive = 1;
    private int mIndexPowerControl = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msdc_sd3test);
        mBtnGet = (Button) findViewById(R.id.msdc_sd3test_get);
        mBtnSet = (Button) findViewById(R.id.msdc_sd3test_set);
        mSpinnerHost = (Spinner) findViewById(R.id.msdc_sd3test_host_spinner);
        mSpinnerMode = (Spinner) findViewById(R.id.msdc_sd3test_mode_spinner);
        mSpinnerMaxCurrent = (Spinner) findViewById(R.id.msdc_sd3test_max_current_spinner);
        mSpinnerDrive = (Spinner) findViewById(R.id.msdc_sd3test_drive_spinner);
        mSpinnerPowerControl = (Spinner) findViewById(R.id.msdc_sd3test_power_control_spinner);
        mBtnGet.setOnClickListener(this);
        mBtnGet.setVisibility(View.GONE);
        mBtnSet.setOnClickListener(this);
        final Resources res = getResources();
        String[] itemArray = res.getStringArray(R.array.host_type);
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(itemArray[0]);
        itemList.add(itemArray[1]);
        // 6572 Chip only have host 0 & 1
        if (!ChipSupport.isCurrentChipEquals(ChipSupport.MTK_6572_SUPPORT)) {
            itemList.add(itemArray[2]);
            itemList.add(itemArray[4]);
        }
        ArrayAdapter<String> hostAdaprer = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemList);
        hostAdaprer
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHost.setAdapter(hostAdaprer);
        mSpinnerHost.setOnItemSelectedListener(mSpinnerListener);

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.msdc_sd3_mode));
        modeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> maxCurrentAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.msdc_sd3_max_current));
        maxCurrentAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> driveAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.msdc_sd3_drive));
        driveAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> powerControlAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, res
                        .getStringArray(R.array.msdc_sd3_power_control));
        powerControlAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMode.setAdapter(modeAdapter);
        mSpinnerMode.setOnItemSelectedListener(mSpinnerListener);
        mSpinnerMaxCurrent.setAdapter(maxCurrentAdapter);
        mSpinnerMaxCurrent.setOnItemSelectedListener(mSpinnerListener);
        mSpinnerDrive.setAdapter(driveAdapter);
        mSpinnerDrive.setOnItemSelectedListener(mSpinnerListener);
        mSpinnerPowerControl.setAdapter(powerControlAdapter);
        mSpinnerPowerControl.setOnItemSelectedListener(mSpinnerListener);
        mSpinnerMode.setSelection(mIndexMode);
        mSpinnerMaxCurrent.setSelection(mIndexMaxCurrent);
        mSpinnerDrive.setSelection(mIndexDrive);
        mSpinnerPowerControl.setSelection(mIndexPowerControl);
    }

    public void onClick(View arg0) {
        if (arg0.getId() == mBtnGet.getId()) {
            showDialog(EVENT_GET_FAIL_ID);
        } else if (arg0.getId() == mBtnSet.getId()) {
            boolean result = EmGpio.setSd30Mode(mIndexHost, mIndexMode,
                    mIndexMaxCurrent, mIndexDrive, mIndexPowerControl);
            if (result) {
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
            case R.id.msdc_sd3test_host_spinner:
                mIndexHost = arg2;
                if (3 == mIndexHost) {
                    mIndexHost++;
                }
                break;
            case R.id.msdc_sd3test_mode_spinner:
                mIndexMode = arg2;
                break;
            case R.id.msdc_sd3test_max_current_spinner:
                mIndexMaxCurrent = arg2;
                break;
            case R.id.msdc_sd3test_drive_spinner:
                mIndexDrive = arg2;
                break;
            case R.id.msdc_sd3test_power_control_spinner:
                mIndexPowerControl = arg2;
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
