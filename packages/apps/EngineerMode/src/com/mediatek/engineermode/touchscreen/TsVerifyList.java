
package com.mediatek.engineermode.touchscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class TsVerifyList extends Activity implements OnItemClickListener {

    public static final String TS_POINT = "ts_point";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchscreen_verification);

        ListView listView = (ListView) findViewById(R.id.ListView_TP_Verification);

        ArrayList<String> items = new ArrayList<String>();
        items.add("PointVerification");
        items.add("LineVerification");
        items.add("ShakingVerification");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        switch (arg2) {
        case 0:
            intent.setClass(this, TsVerifyShakingPoint.class);
            intent.putExtra(TS_POINT, true);
            break;
        case 1:
            intent.setClass(this, TsVerifyLine.class);
            break;
        case 2:
            intent.setClass(this, TsVerifyShakingPoint.class);
            intent.putExtra(TS_POINT, false);
            break;
        default:
            break;
        }

        this.startActivity(intent);
    }
}
