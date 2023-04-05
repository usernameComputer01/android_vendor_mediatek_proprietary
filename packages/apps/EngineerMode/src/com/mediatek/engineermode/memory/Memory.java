
package com.mediatek.engineermode.memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mediatek.engineermode.FeatureHelpPage;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

public class Memory extends Activity implements OnItemClickListener {

    private static final String TAG = "EM/Memory";
    protected static final String FLASH_TYPE = "HAVE_EMMC";
    private static final String EMMC_PROC_FILE = "/proc/emmc";
    private boolean mHaveEmmc = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory);
        ListView itemList = (ListView) findViewById(R.id.list_memory_item);
        ArrayList<String> items = new ArrayList<String>();
        items.add(getString(R.string.memory_title_flash));
        items.add(getString(R.string.help));
        // items.add(getString(R.string.memory_item_emi));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        itemList.setAdapter(adapter);
        itemList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = null;
        switch (arg2) {
        case 0:
            intent = new Intent(this, Flash.class);
            break;
        case 1:
            //intent = new Intent(this, EmiRegister.class);
            intent = new Intent(this, FeatureHelpPage.class);
            intent.putExtra(FeatureHelpPage.HELP_TITLE_KEY, R.string.help);
            intent.putExtra(FeatureHelpPage.HELP_MESSAGE_KEY, R.string.memory_help_msg);
            break;
        default:
            break;
        }
        if (null == intent) {
            Toast.makeText(this, R.string.memory_select_error,
                    Toast.LENGTH_LONG).show();
            Log.d("@M_" + TAG, "Select error");
        } else {
            this.startActivity(intent);
        }
    }
}
