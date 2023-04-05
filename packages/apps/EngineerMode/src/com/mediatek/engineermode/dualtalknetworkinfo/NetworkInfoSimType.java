

package com.mediatek.engineermode.dualtalknetworkinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.internal.telephony.PhoneConstants;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class NetworkInfoSimType extends Activity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dualtalk_networkinfo);
        ListView simTypeListView = (ListView) findViewById(R.id.ListView_dualtalk_networkinfo);

        ArrayList<String> items = new ArrayList<String>();
        items.add(getString(R.string.networkinfo_sim1));
        items.add(getString(R.string.networkinfo_sim2));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        simTypeListView.setAdapter(adapter);
        simTypeListView.setOnItemClickListener(this);
    }

    /**
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     *      android.view.View, int, long)
     * @param arg0
     *            the Adapter for parent
     * @param arg1
     *            the View to display
     * @param position
     *            the integer of item position
     * @param arg3
     *            the long of ignore
     */
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent();
        int simType;
        switch (position) {
        case 0:
            intent.setClassName(this,
                    "com.mediatek.engineermode.networkinfo.NetworkInfo");
            simType = PhoneConstants.SIM_ID_1;
            intent.putExtra("mSimType", simType);

            break;
        case 1:
            intent.setClassName(this,
                    "com.mediatek.engineermode.networkinfo.NetworkInfo");
            simType = PhoneConstants.SIM_ID_2;
            intent.putExtra("mSimType", simType);
            break;
        default:
            break;
        }

        this.startActivity(intent);
    }
}
