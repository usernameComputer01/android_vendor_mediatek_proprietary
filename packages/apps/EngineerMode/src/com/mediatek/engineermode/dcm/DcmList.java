
package com.mediatek.engineermode.dcm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.mediatek.engineermode.R;

import java.util.ArrayList;
import java.util.List;

public class DcmList extends Activity implements OnItemClickListener {

    private static final String TAG = "DcmList";
    private ListView mListView;
    private List<String> mDcmItems;
    private List<Class<? extends Activity>> mDcmTargets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dcm_list);
        mListView = (ListView) findViewById(R.id.dcm_listview);
        mListView.setOnItemClickListener(this);
        mDcmItems = new ArrayList<String>();
        mDcmTargets = new ArrayList<Class<? extends Activity>>();
        mDcmItems.add(getString(R.string.dcm_apdcm));
        mDcmTargets.add(ApdcmActivity.class);
        mDcmItems.add(getString(R.string.dcm_lpm));
        mDcmTargets.add(LpmActivity.class);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mDcmItems);
        mListView.setAdapter(adapter);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position < 0 || position >= mDcmTargets.size()) {
            Log.w("@M_" + TAG, "onItemClick() Invalid postion:" + position);
            return;
        }
        Intent intent = new Intent(this, mDcmTargets.get(position));
        startActivity(intent);
    }
}
