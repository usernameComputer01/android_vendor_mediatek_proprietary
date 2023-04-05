
package com.mediatek.engineermode.video;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

/**
 *  Video main actvity.
 * @author mtk81238
 *
 */
public class VideoActivity extends Activity implements OnItemSelectedListener {

    private static final String TAG = "EM/VideoActivity";
    private static final String SP_KEY_MJC_CUSTOMER = "sys.display.mjc.customer";
    private static final String SP_KEY_MJC_DEMO = "sys.display.mjc.demo";

    private Spinner mSpCustomize = null;
    private Spinner mSpDemo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_entry);
        mSpCustomize = (Spinner) findViewById(R.id.video_customize_spn);
        mSpDemo = (Spinner) findViewById(R.id.video_demo_spn);
        fillSpinnerIntItems(mSpCustomize, new int[] {0, 1});
        fillSpinnerIntItems(mSpDemo, new int[] {0, 1, 2});
        mSpCustomize.setSelection(getIntSystemProp(SP_KEY_MJC_CUSTOMER, 0));
        mSpDemo.setSelection(getIntSystemProp(SP_KEY_MJC_DEMO, 0));
        mSpCustomize.setOnItemSelectedListener(this);
        mSpDemo.setOnItemSelectedListener(this);
    }

    private void fillSpinnerIntItems(Spinner spinner, int[] itemVals) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < itemVals.length; i++) {
            adapter.add(String.valueOf(itemVals[i]));
        }
        spinner.setAdapter(adapter);
    }

    private void setIntSystemProp(String key, int val) {
        String strVal = String.valueOf(val);
        SystemProperties.set(key, strVal);
    }

    private int getIntSystemProp(String key, int defVal) {
        String strVal = SystemProperties.get(key);
        int value = 0;
        try {
            value = Integer.parseInt(strVal);
        } catch (NumberFormatException e) {
            value = defVal;
        }
        return value;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (parent == mSpCustomize) {
            setIntSystemProp(SP_KEY_MJC_CUSTOMER, position);
        } else if (parent == mSpDemo) {
            setIntSystemProp(SP_KEY_MJC_DEMO, position);
        } else {
            Elog.d(TAG, "Unhandled adapterView:" + parent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // just do nothing
    }
}
