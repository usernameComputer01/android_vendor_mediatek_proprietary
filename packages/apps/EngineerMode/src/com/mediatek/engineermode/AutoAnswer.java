


package com.mediatek.engineermode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.SystemProperties;


public class AutoAnswer extends Activity implements OnClickListener {

    private static final String TAG = "EM-AutoAnswer";
    private static final String SHREDPRE_NAME = "AutoAnswer";
    private static final String BUTTON_FLAG = "flag";
    private static final String AUTO_ANSWER_PROPERTY = "persist.auto_answer";
    private Button mSetButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_answer);
        mSetButton = (Button) findViewById(R.id.autoanswer_switch);

        mSetButton.setOnClickListener(this);

        final SharedPreferences autoAnswerSh = getSharedPreferences(SHREDPRE_NAME,
                MODE_PRIVATE);
        final boolean buttonFlag = autoAnswerSh.getBoolean(BUTTON_FLAG, false);
        final String sFlag = SystemProperties.get(AUTO_ANSWER_PROPERTY, "0");
        Log.v("@M_" + TAG, "onCreate flag is :" + buttonFlag + "sFlag is:" + sFlag);

        if (buttonFlag) {
            mSetButton.setText(R.string.autoanswer_disable);
        } else {
            mSetButton.setText(R.string.autoanswer_enable);
        }
    }

    @Override
    public void onClick(final View arg0) {
        if (arg0 == mSetButton) {
            if ((getString(R.string.autoanswer_enable))
                    .equals(mSetButton.getText())) {
                mSetButton.setText(R.string.autoanswer_disable);
                writeSharedPreferences(true);
                SystemProperties.set(AUTO_ANSWER_PROPERTY, "1");
            } else {
                mSetButton.setText(R.string.autoanswer_enable);
                writeSharedPreferences(false);
                SystemProperties.set(AUTO_ANSWER_PROPERTY, "0");
            }
        }
    }

    /**
     * Set flag value when on click button.
     *
     * @param flag
     *            the final boolean of the button status to set
     * */
    private void writeSharedPreferences(final boolean flag) {
        final SharedPreferences autoAnswerSh = getSharedPreferences(SHREDPRE_NAME,
                MODE_PRIVATE);
        final SharedPreferences.Editor editor = autoAnswerSh.edit();
        editor.putBoolean(BUTTON_FLAG, flag);
        editor.commit();
    }
}
