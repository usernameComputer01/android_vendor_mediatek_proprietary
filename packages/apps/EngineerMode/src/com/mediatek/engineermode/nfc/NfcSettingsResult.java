


package com.mediatek.engineermode.nfc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

/** Just show settings rsp result, just can read. */
public class NfcSettingsResult extends Activity {

    public static final String TAG = "EM/nfc";
    private static final int NUMER_3 = 3;
    private static final int NUMER_4 = 4;
    private static final int NUMER_5 = 5;
    private static final int NUMER_6 = 6;
    private static final int NUMER_7 = 7;
    private TextView mTextFWVersion = null;
    private TextView mTextHWVersion = null;
    private TextView mTextSWVersion = null;

    private ArrayList<ModeMap> mReaderModeArray = new ArrayList<ModeMap>();
    private ArrayList<ModeMap> mCardModeArray = new ArrayList<ModeMap>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.nfc_settings_result);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        Elog.d(TAG, "NfcSettingsResult onCreate");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDisplayUI();
    }

    private void setDisplayUI() {
        NfcNativeCallClass.nfc_setting_response resp;
        resp =
            (NfcNativeCallClass.nfc_setting_response) NfcRespMap.getInst()
                .take(NfcRespMap.KEY_SETTINGS);
        if (resp == null) {
            Elog.e(TAG, "Take NfcRespMap.KEY_SETTINGS is null");
            // assert
            return;
        }

        mTextFWVersion.setText(String.format("0x%X", resp.fw_ver));
        mTextHWVersion.setText(String.format("0x%X", resp.hw_ver));
        mTextSWVersion.setText(String.format("0x%X", resp.sw_ver));

        setCurrentMode(resp.reader_mode, resp.card_mode);
    }

    private void initUI() {
        mTextFWVersion =
            (TextView) findViewById(R.id.NFC_Settings_Result_Version_FW);
        mTextHWVersion =
            (TextView) findViewById(R.id.NFC_Settings_Result_Version_HW);
        mTextSWVersion =
            (TextView) findViewById(R.id.NFC_Settings_Result_Version_SW);

        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_MifareUL),
                0));
        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_MifareStd),
                1));
        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_ISO14443_4A),
                2));
        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_ISO14443_4B),
                NUMER_3));
        mReaderModeArray.add(new ModeMap(
            (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_Jewel),
            NUMER_4));
        mReaderModeArray.add(new ModeMap(
            (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_NFC),
            NUMER_5));
        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_Felica),
                NUMER_6));
        mReaderModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_RM_ISO15693),
                NUMER_7));
        // =========================
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_MifareUL),
                0));
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_MifareStd),
                1));
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_ISO14443_4A),
                2));
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_ISO14443_4B),
                NUMER_3));
        mCardModeArray.add(new ModeMap(
            (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_Jewel),
            NUMER_4));
        mCardModeArray.add(new ModeMap(
            (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_NFC),
            NUMER_5));
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_Felica),
                NUMER_6));
        mCardModeArray
            .add(new ModeMap(
                (CheckBox) findViewById(R.id.NFC_Settings_Result_Support_CM_ISO15693),
                NUMER_7));

        OnClickListenerSpecial specListener = new OnClickListenerSpecial();
        for (ModeMap m : mReaderModeArray) {
            m.mChkBox.setOnClickListener(specListener);
        }
        for (ModeMap m : mCardModeArray) {
            m.mChkBox.setOnClickListener(specListener);
        }

    }

    private void setCurrentMode(int readerModeVal, int cardModeVal) {
        for (ModeMap m : mReaderModeArray) {
            if ((readerModeVal & (1 << m.mBit)) == 0) {
                m.mChkBox.setChecked(false);
            } else {
                if (m.mChkBox.isEnabled()) {
                    m.mChkBox.setChecked(true);
                }
            }
        }
        for (ModeMap m : mCardModeArray) {
            if ((cardModeVal & (1 << m.mBit)) == 0) {
                m.mChkBox.setChecked(false);
            } else {
                if (m.mChkBox.isEnabled()) {
                    m.mChkBox.setChecked(true);
                }
            }
        }
    }

    private class OnClickListenerSpecial implements OnClickListener {
        // this function's purpose is making checkBox only display the status,
        // and can not be set.
        public void onClick(View arg0) {
            if (arg0 instanceof CheckBox) {
                CheckBox chk = (CheckBox) arg0;
                if (chk.isChecked()) {
                    chk.setChecked(false);
                } else {
                    chk.setChecked(true);
                }
            }
        }
    }

}
