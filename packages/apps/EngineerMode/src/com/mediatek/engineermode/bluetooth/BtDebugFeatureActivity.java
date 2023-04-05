

package com.mediatek.engineermode.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.engineermode.R;

/**
 * Do BT debug feature test.
 * @author mtk80137
 *
 */
public class BtDebugFeatureActivity extends Activity implements OnClickListener {

    private static final String TAG = "BtDebugFeature";
    private static final String KEY_DEBUG_FEATURE = "persist.bt.fwdump";
    private BtTest mBtTest = null;

    // UI component
    private CheckBox mChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bt_debug_feature);

        TextView tv = (TextView) findViewById(R.id.BTDebugFeaturetxv);
        tv.setText(Html.fromHtml(getString(R.string.BtDebugFeature)));

        mChecked = (CheckBox) findViewById(R.id.BTDebugFeatureCb);
        mChecked.setOnClickListener(this);
        if (0xFFFFFFFFL == SystemProperties.getLong(KEY_DEBUG_FEATURE, 0)) {
            mChecked.setChecked(true);
        } else {
            mChecked.setChecked(false);
        }
    }

    public void onClick(View v) {
        if (v.equals(mChecked)) {
            long value = mChecked.isChecked() ? 0xFFFFFFFFL : 0;
            mBtTest = new BtTest();
            mBtTest.setFWDump(value);
            //SystemProperties.set(KEY_DEBUG_FEATURE, Long.toString(value));
            Toast.makeText(this, "The change will be valid after you restart phone",
                    Toast.LENGTH_LONG).show();
            Log.v("@M_" + TAG, "DebugFeature isChecked--" + mChecked.isChecked());
        }
    }
}
