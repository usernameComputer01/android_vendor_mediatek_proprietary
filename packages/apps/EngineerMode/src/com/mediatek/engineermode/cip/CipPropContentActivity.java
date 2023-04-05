
package com.mediatek.engineermode.cip;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.mediatek.engineermode.R;

public class CipPropContentActivity extends Activity {
    TextView mTvPropContent;
    String mPropContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cip_prop_content);

        mTvPropContent = (TextView) findViewById(R.id.cip_prop_content_tv);
        mTvPropContent.setText("Loading ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPropContent = CipUtil.getFileAllContent(new File(CipUtil.CIP_PROP_FILE));
                if (TextUtils.isEmpty(mPropContent)) {
                    mPropContent = "No content found";
                }
                CipPropContentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvPropContent.setText(mPropContent);
                    }
                });
            }
        }).start();
    }
}
