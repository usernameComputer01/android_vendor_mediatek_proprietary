


package com.mediatek.engineermode.simswitch;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.engineermode.R;

public class SimSwitch extends Activity implements OnClickListener {

    private static final String TAG = "EM/SimSwitch";
    private static final String BUTTON_FLAG = "flag";
    private static final String PROPERTY = "persist.radio.simswitch.emmode";
    private Button mSetButton;
    private static boolean mSimSwitch = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_switch);
        mSetButton = (Button) findViewById(R.id.sim_switch);
        mSetButton.setOnClickListener(this);
        String property = SystemProperties.get(PROPERTY);
        if (property == null) {
            mSimSwitch = true;
        } else {
            mSimSwitch = property.equals("0") ? false : true;
        }

        setButtonText(mSimSwitch);
    }

    @Override
    public void onClick(final View arg0) {
        SystemProperties.set(PROPERTY,
                (getString(R.string.sim_switch_on)).equals(mSetButton.getText()) ? "1" : "0");
        mSimSwitch = SystemProperties.get(PROPERTY).equals("0") ? false : true;
        setButtonText(mSimSwitch);
    }

    private void setButtonText(boolean mSimSwitch) {
        if (mSimSwitch) {
            mSetButton.setText(R.string.sim_switch_off);
        } else {
            mSetButton.setText(R.string.sim_switch_on);
        }
    }
}
