

package com.mediatek.engineermode.io;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.mediatek.engineermode.ChipSupport;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class MsdcSelect extends Activity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msdc_select);
        ListView msdcTypeSelect = (ListView) findViewById(R.id.ListView_msdcSelect);

        ArrayList<String> items = new ArrayList<String>();
        items.add(getString(R.string.msdc_drive_set));
        if (ChipSupport.MTK_6589_SUPPORT <= ChipSupport.getChip()) {
            items.add(getString(R.string.msdc_sd3_test));
        } else {
            items.add(getString(R.string.msdc_hop_set));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        msdcTypeSelect.setAdapter(adapter);
        msdcTypeSelect.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        switch (arg2) {
        case 0:
            intent.setClass(this, MsdcDrivSet.class);
            break;
        case 1:
            if (ChipSupport.MTK_6589_SUPPORT <= ChipSupport.getChip()) {
                intent.setClass(this, MsdcSd3Test.class);
            } else {
                intent.setClass(this, MsdcHopSet.class);
            }
            break;
        default:
            break;
        }
        this.startActivity(intent);

    }

}
