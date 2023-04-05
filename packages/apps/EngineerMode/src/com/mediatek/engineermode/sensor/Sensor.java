
package com.mediatek.engineermode.sensor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediatek.engineermode.FeatureSupport;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class Sensor extends Activity implements OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_calibration_select);

        ListView list = (ListView) findViewById(R.id.list_view_calibration);

        ArrayList<String> items = new ArrayList<String>();
        items.add(getString(R.string.sensor_data));
        if (!FeatureSupport.isSupported(FeatureSupport.FK_MTK_WEARABLE_PLATFORM)) {
            items.add(getString(R.string.sensor_ps));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        if (0 == arg2) {
            intent.setClass(this, MSensor.class);
            this.startActivity(intent);
        } else if (1 == arg2) {
            intent.setClass(this, PSensor.class);
            this.startActivity(intent);
        }
    }
}
