


package com.mediatek.engineermode.nfc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

public class NfcSoftwareStackScanWriteTag extends Activity
        implements
        OnClickListener {

    public static final String TAG = "EM/nfc";

    private Button mBtnOK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.nfc_software_stack_scan_write_ndef);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        Elog.e(TAG, "NfcSoftwareStackScanWriteNDEF onCreate");
        mBtnOK = (Button) findViewById(R.id.NFC_SoftwareStack_Scan_Write_BtnOK);
    }

    /**
     * on click the button ok
     * 
     * @param arg0
     *            : clicked which view
     */
    public void onClick(View arg0) {
        if (arg0 == mBtnOK) {
            Elog.e(TAG, "NfcSoftwareStackScanReadNDEF onClick");
            // Intent intent = new Intent();
            // intent.setClassName(this,
            // "com.mediatek.engineermode.nfc.NfcSoftwareStackScanReadNDEF");
            // this.startActivity(intent);
        }
    }

}
