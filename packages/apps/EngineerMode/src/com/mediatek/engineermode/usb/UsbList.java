
package com.mediatek.engineermode.usb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ChipSupport;
import java.util.ArrayList;

/**
 * Test USB OTG and ex function
 *
 * @author mtk54040
 *
 */
public class UsbList extends Activity implements OnItemClickListener {
    public static final String IF_TEST = "if_test";
    public static final String IF_OTG20_TEST = "if_otg20_test";

    private static final  int INDEX_0 = 0;
    private static final  int INDEX_1 = 1;
    private static final  int INDEX_2 = 2;
    private static final String TAG = "UsbList";
    private ArrayList<String> mItemList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb);

        ListView listView = (ListView) findViewById(R.id.ListView_USB);

        mItemList.add(getString(R.string.USB_IF_TEST));
        mItemList.add(getString(R.string.USB_EX_TEST));
        if (ChipSupport.getChip() >= ChipSupport.MTK_6595_SUPPORT) {
            mItemList.add(getString(R.string.USB_IF_OTG20_TEST));
        }

        if (UsbPhyTuning.isUsbPhyExist()) {
            mItemList.add(getString(R.string.usb_phy_tuning));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mItemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d("@M_" + TAG, "-->onItemClick + arg2 " + arg2);
        Intent intent = null;
        if (getString(R.string.usb_phy_tuning).equals(mItemList.get(arg2))) {
            intent = new Intent(this, UsbPhyTuning.class);
        } else {
            intent = new Intent(UsbList.this, UsbTest.class);
            switch (arg2) {
            case INDEX_0:
                intent.putExtra(UsbList.IF_TEST, true);
                intent.putExtra(UsbList.IF_OTG20_TEST, false);
                break;
            case INDEX_1:
                intent.putExtra(UsbList.IF_TEST, false);
                intent.putExtra(UsbList.IF_OTG20_TEST, false);
                break;
            case INDEX_2:
                intent.putExtra(UsbList.IF_TEST, false);
                intent.putExtra(UsbList.IF_OTG20_TEST, true);
                break;
            default:
                break;
            }
        }
        if (intent == null) {
            Log.d("@M_" + TAG, "Invalid intent: null");
            return;
        }
        startActivity(intent);
    }

}
