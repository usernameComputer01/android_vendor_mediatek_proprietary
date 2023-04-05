


package com.mediatek.engineermode.nfc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

public class NfcSoftwareStackP2PInitDect extends Activity
        implements
        OnClickListener {

    private static final String TAG = "EM/nfc";
    private Button mBtnSend = null;
    private Button mBtnRecv = null;
    private EditText mEditSend = null;
    private EditText mEditRecv = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_software_stack_p2p_init_dect);

        mBtnSend =
            (Button) findViewById(R.id.NFC_SoftwareStack_P2P_InitDect_Send);
        mBtnRecv =
            (Button) findViewById(R.id.NFC_SoftwareStack_P2P_InitDect_Recv);
        mEditSend =
            (EditText) findViewById(R.id.NFC_SoftwareStack_P2P_InitDect_In);
        mEditRecv =
            (EditText) findViewById(R.id.NFC_SoftwareStack_P2P_InitDect_Out);
        mBtnSend.setOnClickListener(this);
        mBtnRecv.setOnClickListener(this);

        mEditRecv
            .setText("ddd\nddd\nddd\nddd\nddd\nddd\nddd\nddd\nddd\n\nddd\nddd\nddd\nddd\n");
        mEditSend.setText(">");
    }

    /**
     * on click the button set
     * 
     * @param arg0
     *            : clicked which view
     */
    public void onClick(View arg0) {
        if (arg0 == mBtnSend) {
            Elog.e(TAG, "NfcSoftwareStackP2PInitDect mBtnSend");
        } else if (arg0 == mBtnRecv) {
            Elog.e(TAG, "NfcSoftwareStackP2PInitDect mBtnRecv");
        }
    }

}
