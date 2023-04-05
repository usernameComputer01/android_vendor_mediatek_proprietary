
package com.mediatek.engineermode.touchscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.mediatek.engineermode.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TouchScreen test modules
 * 
 * @author mtk54040
 * 
 */
public class TouchScreenList extends Activity implements OnItemClickListener {

    private static final String TAG = "EM/TouchScreen";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchscreen);

        ListView listView = (ListView) findViewById(R.id.ListView_TouchScreen);
//        if (listView == null) {
//            Log.w("@M_" + TAG, "clocwork worked...");
//            // not return and let exception happened.
//        }

        ArrayList<String> items = new ArrayList<String>();
        // items.add("HandWriting");
        // items.add("Verification");
        // items.add("Settings");
        // items.add("Rate Report");

        items.add(getString(R.string.TouchScreen_HandWriting));
        items.add(getString(R.string.TouchScreen_Verification));
        items.add(getString(R.string.TouchScreen_Settings));
        items.add(getString(R.string.TouchScreen_RateReport));
        try {
            String[] cmd = { "/system/bin/sh", "-c",
                    "cat /sys/module/tpd_setting/parameters/tpd_type_cap" };
            int ret = TouchScreenShellExe.execCommand(cmd);
            if (0 == ret) {
                Log.i("@M_" + TAG, TouchScreenShellExe.getOutput());
                if (TouchScreenShellExe.getOutput().equalsIgnoreCase("1")) {
                    // items.add("MultiTouch");
                    items.add(getString(R.string.TouchScreen_MultiTouch));
                }
            }

        } catch (IOException e) {
            Log.i("@M_" + TAG, e.toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        switch (arg2) {
        case 0:
            intent.setClass(this, TsHandWriting.class);
            break;
        case 1:
            intent.setClass(this, TsVerifyList.class);
            break;
        case 2:
            intent.setClass(this, TouchScreenSettings.class);
            break;
        case 3:
            intent.setClass(this, TsRateReport.class);
            break;
        case 4:
            intent.setClass(this, TsMultiTouch.class);
            break;
        default:
            break;
        }

        this.startActivity(intent);
    }

}
