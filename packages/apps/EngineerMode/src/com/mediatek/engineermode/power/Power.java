

package com.mediatek.engineermode.power;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ChipSupport;
import java.util.ArrayList;
import java.util.List;

public class Power extends Activity implements OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power);

        ListView tpListView = (ListView) findViewById(R.id.ListView_Power);

        if (tpListView == null) {
            Elog.e("Power", "clocwork worked...");
            return;
        }
        List<String> items = new ArrayList<String>();
        items.add("Charge Battery");
        items.add("PMU");
        if (isSupportLPDebug()) {
            items.add("Low Power Protect Debug");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        tpListView.setAdapter(adapter);
        tpListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Intent intent = new Intent();
        switch (arg2) {
        case 0:
            intent.setClass(this, ChargeBattery.class);
            break;
        case 1:
            intent.setClass(this, PMU6575.class);
            break;
        case 2:
            intent.setClass(this, LowPowerDebug.class);
        default:
            break;
        }
        this.startActivity(intent);
    }
    private boolean isSupportLPDebug() {
        if (ChipSupport.getChip() >= ChipSupport.MTK_6592_SUPPORT ||
            ChipSupport.getChip() == ChipSupport.MTK_6582_SUPPORT) {
            return true;
        }
        return false;
    }
}
