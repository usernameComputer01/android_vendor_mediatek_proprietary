
package com.mediatek.ygps.tests;



import android.app.Activity;
import android.app.Instrumentation;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.provider.Settings;
import android.view.KeyEvent;
import android.location.LocationManager;

import com.jayway.android.robotium.solo.Solo;
import com.mediatek.ygps.R;
import com.mediatek.ygps.YgpsActivity;


public class YgpsAutoTest extends
        ActivityInstrumentationTestCase2<YgpsActivity> {

    private static final String TAG = "YgpsAutoTestTag";
    private static final int TIME_SHORT = 500;
    private static final int TIME_MID = 1000;
    private static final int TIME_LONG = 3000;

    private static final int POWER_KEY = KeyEvent.KEYCODE_POWER;
    private Solo mSolo = null;
    private Activity mActivity = null;
    private Context mContext = null;
    private Instrumentation mInst = null;

    public YgpsAutoTest() {
        super(YgpsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();


        mInst = getInstrumentation();
        mContext = mInst.getTargetContext();
        mActivity = getActivity();
        mSolo = new Solo(mInst, mActivity);
        Settings.Secure.setLocationProviderEnabled(mActivity.getContentResolver(), LocationManager.GPS_PROVIDER, true);
        mSolo.sleep(TIME_MID);
    }


    public void test01() {  // check normal launch
        mSolo.sleep(TIME_LONG);
        Log.i(TAG, "[YGPS Auto Test]test01 success");
    }

    public void test02() {  // check restart button
        mSolo.clickOnText(mActivity.getString(R.string.information));

        mSolo.sleep(TIME_MID);

        String buttonHotStart = mActivity.getString(R.string.hot);

        for (int i = 0; i < 10; i++) {
            mSolo.clickOnButton(buttonHotStart);
            mSolo.sleep(TIME_SHORT);
        }
        //for(int i = 0; i < 2; i++) {
        //    mSolo.sendKey(POWER_KEY);
        //    mSolo.sleep(TIME_SHORT);
        //}
        mSolo.sleep(TIME_LONG);
        Log.i(TAG, "[YGPS Auto Test]test02 success");
    }

    public void test03() { // check provider
        mSolo.clickOnText(mActivity.getString(R.string.information));

        mSolo.sleep(TIME_MID);

        // enable gps
        Settings.Secure.setLocationProviderEnabled(mActivity.getContentResolver(), LocationManager.GPS_PROVIDER, true);
        mSolo.sleep(TIME_LONG);
        String mProvider = String.format(mActivity.getString(
                            R.string.provider_status_enabled,
                            LocationManager.GPS_PROVIDER));
        Log.i(TAG, mProvider);
        if (mSolo.searchText(mProvider)) {
            //Log.i(TAG, "[YGPS Auto Test]test03 success");
        } else {
            Log.i(TAG, "[YGPS Auto Test]test03 fail");
            return;
        }
        mSolo.sleep(TIME_SHORT);
        mProvider = String.format(mActivity.getString(
                            R.string.provider_status_disabled,
                            LocationManager.GPS_PROVIDER));
        // disable gps
        Log.i(TAG, mProvider);
        mSolo.sleep(TIME_LONG);
        Settings.Secure.setLocationProviderEnabled(mActivity.getContentResolver(), LocationManager.GPS_PROVIDER, false);
        if (mSolo.searchText(mProvider)) {
            Log.i(TAG, "[YGPS Auto Test]test03 success");
        } else {
            Log.i(TAG, "[YGPS Auto Test]test03 fail");
        }
        // enable gps again, prepare for test04
        Settings.Secure.setLocationProviderEnabled(mActivity.getContentResolver(), LocationManager.GPS_PROVIDER, true);
    }
    /*
    public void test02() {
        mSolo.sleep(EmOperate.TIME_LONG);
        if (null == adapter || BluetoothAdapter.STATE_OFF != adapter.getState()) {
            return;
        }
        final int COUNT = 7;
        int strId[] = {R.string.BT_tx_only_Title, R.string.BTNSRXTitle, R.string.BTTMTitle,
                        R.string.BTSSPDMTitle, R.string.BT_RelayerModeTitle, R.string.BtDebugFeatureTitle,
                    R.string.ClockSelectionTitle};
        boolean find = true;
        for(int i = 0; i < 7; i++){
            find = mSolo.searchText(mActivity.getString(strId[i]));
        }
        if(find == true) {
             Log.i(TAG, "[EM Auto Test]Bt list items test success");
        }else {
             Log.i(TAG, "[EM Auto Test]Bt list items test fail");
        }
    }
    */
}
