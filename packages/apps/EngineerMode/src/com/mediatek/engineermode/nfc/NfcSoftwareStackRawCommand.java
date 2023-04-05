


package com.mediatek.engineermode.nfc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

public class NfcSoftwareStackRawCommand extends Activity
        implements
        OnClickListener {

    public static final String TAG = "EM/nfc";

    private Button mBtnSend;
    private Button mBtnExample;
    private EditText mEditCommand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.nfc_software_stack_raw_command);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        Elog.e(TAG, "NfcSoftwareStackRawCommand onCreate");
        initUI();
    }

    private void initUI() {
        mBtnSend =
            (Button) findViewById(R.id.NFC_SoftwareStack_RawCommand_Send);
        mBtnExample =
            (Button) findViewById(R.id.NFC_SoftwareStack_RawCommand_Examp);
        mEditCommand =
            (EditText) findViewById(R.id.NFC_SoftwareStack_RawCommand_String);
        mBtnSend.setOnClickListener(this);
        mBtnExample.setOnClickListener(this);
    }

    /**
     * on click the button set
     * 
     * @param arg0
     *            : clicked which view
     */
    public void onClick(View arg0) {
        Elog.e(TAG, "NfcSoftwareStackRawCommand onClick");
        if (arg0 == mBtnSend) {
            onSend();
        } else if (arg0 == mBtnExample) {
            mEditCommand
                .setText("00 A4 04 00 0E A0 00 00 00 18 30 80 05 00 65 63 68 6F 00 00");
        }
    }

    private boolean onSend() {
        final String strCommand = mEditCommand.getText().toString();
        Elog.e(TAG, strCommand);
        return true;
    }

}
