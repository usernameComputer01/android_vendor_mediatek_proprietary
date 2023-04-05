
package com.mediatek.engineermode.testcard;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

/**
 * Set CT test card setting.
 */
public class TestCardActivity extends PreferenceActivity {
    private static final String TAG = "EM/TestCard";
    private static final String KEY_TEST_CARD = "key_test_card";
    private static final int VALUE_OFF = 0;
    private static final int VALUE_ON = 1;
    private static final String PROP_TEST_CARD = "persist.sys.forcttestcard";
    private CheckBoxPreference mTestCardPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.test_card);

        PreferenceScreen prefSet = getPreferenceScreen();
        if (prefSet != null) {
            mTestCardPref = (CheckBoxPreference) prefSet.findPreference(KEY_TEST_CARD);
        }
   }

    @Override
    protected void onResume() {
        super.onResume();
        int value = SystemProperties.getInt(PROP_TEST_CARD, VALUE_OFF);
        Elog.v(TAG, "Get " + PROP_TEST_CARD + " = " + value);
        mTestCardPref.setChecked(value == 0 ? false : true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (mTestCardPref.equals(preference)) {
            int value = mTestCardPref.isChecked() ? VALUE_ON : VALUE_OFF;
            Elog.v(TAG, "Set " + PROP_TEST_CARD + " = " + value);
            SystemProperties.set(PROP_TEST_CARD, String.valueOf(value));
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
