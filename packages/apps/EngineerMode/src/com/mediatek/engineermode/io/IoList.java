
package com.mediatek.engineermode.io;

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

public class IoList extends Activity implements OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.io);

        ListView listView = (ListView) findViewById(R.id.ListView_Io);
        // if(TP_listView == null)
        // {
        // Log.w("@M_" + "IO", "clocwork worked...");
        // //not return and let exception happened.
        // }
        ArrayList<String> items = new ArrayList<String>();
        items.add("GPIO");
        items.add("EINT");
        items.add("MSDC");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Intent intent = new Intent();
        switch (arg2) {
        case 0:
            intent.setClass(this, Gpio.class);
            break;
        case 1:
            intent.setClass(this, Eint.class);
            break;
        case 2:
            intent.setClass(this, MsdcSelect.class);
            break;
        default:
            break;
        }

        this.startActivity(intent);
    }

}
