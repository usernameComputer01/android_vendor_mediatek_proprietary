
package com.mediatek.engineermode.moms;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mediatek.engineermode.R;


public class MoMs extends Activity {

    private static final String TAG = "EM/MOMS";
    private static final String PROPERTY_NAME = "persist.sys.mtk.disable.moms" ;
    private static final String DISABLE_MOMS = "1" ;
    private static final String REENABLE_MOMS = "0" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moms);
        Button mDisableMoMs = (Button) findViewById(R.id.disable_moms_for_cts);
        mDisableMoMs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mDisableMoMs is clicked, call setprop persist.sys.mtk.disable.moms 1") ;
                SystemProperties.set(PROPERTY_NAME, DISABLE_MOMS);
            }
        });

        Button mReenableMoMs = (Button) findViewById(R.id.reenable_moms);
        mReenableMoMs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mReenableMoMs is clicked, " +
                    "call setprop persist.sys.mtk.disable.moms 0") ;
                SystemProperties.set(PROPERTY_NAME, REENABLE_MOMS);
            }
        });

    }
}
