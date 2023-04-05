
package com.mediatek.engineermode.bandselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.internal.telephony.PhoneConstants;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

/**
 * The class for select phone1 or phone2.
 */
public class BandModeSimSelect extends Activity implements OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bandmodesimselect);
        ListView simTypeListView = (ListView) findViewById(R.id.listview_bandmode_sim_select);
        ArrayAdapter<String> adapter;
        if (TelephonyManager.getDefault().getSimCount() > 1) {
            ArrayList<String> array = new ArrayList<String>();
            array.add(getString(R.string.bandmode_sim1));
            array.add(getString(R.string.bandmode_sim2));
            if (TelephonyManager.getDefault().getSimCount() >= 3) {
                array.add(getString(R.string.bandmode_sim3));
            }
            if (TelephonyManager.getDefault().getSimCount() == 4) {
                array.add(getString(R.string.bandmode_sim4));
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        } else {
            ArrayList<String> array = new ArrayList<String>();
            array.add(getString(R.string.bandmode_sim1));
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        }

        simTypeListView.setAdapter(adapter);
        simTypeListView.setOnItemClickListener(this);
    }

    /**
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     *      android.view.View, int, long)
     * @param parent the Adapter for  parent
     * @param view the View to display
     * @param position the integer of item position
     * @param id the long of ignore
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent();
        int simType;
        switch (position) {
        case 0:
            intent.setClass(this, BandSelect.class);
            simType = PhoneConstants.SIM_ID_1;
            intent.putExtra("mSimType", simType);
            break;
        case 1:
            intent.setClass(this, BandSelect.class);
            simType = PhoneConstants.SIM_ID_2;
            intent.putExtra("mSimType", simType);
            break;
        case 2:
            intent.setClass(this, BandSelect.class);
            simType = PhoneConstants.SIM_ID_3;
            intent.putExtra("mSimType", simType);
            break;
        case 3:
            intent.setClass(this, BandSelect.class);
            simType = PhoneConstants.SIM_ID_4;
            intent.putExtra("mSimType", simType);
            break;
        default:
            break;
        }
        this.startActivity(intent);

    }
}
