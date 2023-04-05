
package com.mediatek.engineermode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;

public class MobileDataPreferred extends PreferenceActivity {
    private static final String TAG = "EM/CallDataPreferred";
    private static final String DATA_PREFER_KEY = "data_prefer_key";
    private static final int PCH_DATA_PREFER = 0;
    private static final int PCH_CALL_PREFER = 1;
    private static final int MOBILE_DATA_PREF_DIALOG = 10;

    private CheckBoxPreference mMobileDataPref;
    private ITelephony mTelephony = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gsm_umts_options);

        PreferenceScreen prefSet = getPreferenceScreen();
        mMobileDataPref = (CheckBoxPreference) prefSet.findPreference(DATA_PREFER_KEY);

        int pchFlag = SystemProperties.getInt("persist.radio.gprs.prefer", 0);
        Log.v("@M_" + TAG, "Orgin value persist.radio.gprs.prefer = " + pchFlag);
        mMobileDataPref.setChecked(pchFlag == 0 ? false : true);

        if (mTelephony == null) {
            mTelephony = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == MOBILE_DATA_PREF_DIALOG) {
            return new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.pch_data_prefer_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setGprsTransferType(PCH_DATA_PREFER);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mMobileDataPref.setChecked(false);
                        }
                    })
                    .create();
        }
        return super.onCreateDialog(id);
    }

    /**
     * Invoked on each preference click in this hierarchy, overrides
     * PreferenceActivity's implementation. Used to make sure we track the
     * preference click events.
     */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (mMobileDataPref.equals(preference)) {
            if (mMobileDataPref.isChecked()) {
                showDialog(MOBILE_DATA_PREF_DIALOG);
            } else {
                setGprsTransferType(PCH_CALL_PREFER);
            }
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void setGprsTransferType(int type) {
        String property = (type == PCH_DATA_PREFER ? "1" : "0");
        Log.v("@M_" + TAG, "Change persist.radio.gprs.prefer to " + property);
        SystemProperties.set("persist.radio.gprs.prefer", property);
        for (int i = 0 ; i < TelephonyManager.getDefault().getPhoneCount(); i++) {
            Phone phone = PhoneFactory.getPhone(i);



            phone.invokeOemRilRequestStrings(new String[] {"AT+EGTP=" + type, ""}, null);
            phone.invokeOemRilRequestStrings(new String[] {"AT+EMPPCH=" + type, ""}, null);
        }
    }
}
