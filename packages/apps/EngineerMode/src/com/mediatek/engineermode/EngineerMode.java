


package com.mediatek.engineermode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.net.ConnectivityManager;




/**
 * This is main UI of EngineerMode. It uses viewPager to show each classified
 * modules. There are six viewPager, each viewPager is in one category:
 * 1.telephony;
 * 2.connectivity;
 * 3.hardware testing;
 * 4.location;
 * 5.log&debugging;
 * 6.others.
 * RDs can add each module in HomeViewData.java file.
 *
 * @author mtk54034
 *
 */
public class EngineerMode extends Activity {

    private static final String TAG = "EM/MainView";
    private static int TAB_COUNT = 6; // Total count of PagerView
    private static int TAB_COUNT_WIFIONLY = 5; // Total count of PagerView
    // Define each tabs which will attach to PagerView
    private PrefsFragment mTabs[] = new PrefsFragment[TAB_COUNT];

    // Record each viewPager title string IDs in array:
    private static int[] TAB_TITLE_IDS = { R.string.tab_telephony,
            R.string.tab_connectivity, R.string.tab_hardware_testing,
            R.string.tab_location, R.string.tab_log_and_debugging,
            R.string.tab_others, };

    private static int[] TAB_TITLE_IDS_WIFIONLY = {
            R.string.tab_connectivity, R.string.tab_hardware_testing,
            R.string.tab_location, R.string.tab_log_and_debugging,
            R.string.tab_others, };

    private MyPagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager
                .beginTransaction();

        if(isWifiOnly()){
            TAB_TITLE_IDS = TAB_TITLE_IDS_WIFIONLY;
            TAB_COUNT = TAB_COUNT_WIFIONLY;
        }

        Log.v("@M_" + TAG, "new fregments");
        for (int i = 0; i < TAB_COUNT; i++) {
            mTabs[i] = new PrefsFragment();
            mTabs[i].setResource(i);
            transaction.add(R.id.viewpager, mTabs[i], String.valueOf(i));
            transaction.hide(mTabs[i]);
        }

        ViewPager viewPager;
        PagerTabStrip pagerTabStrip;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertitle);
        pagerTabStrip
                .setTabIndicatorColorResource(android.R.color.holo_blue_light);

        transaction.commitAllowingStateLoss();
//        fragmentManager.executePendingTransactions();

        mPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(0);
    }

   private boolean isWifiOnly() {
        ConnectivityManager connManager = (ConnectivityManager) EngineerMode.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bWifiOnly = false;
        if (null != connManager) {
            bWifiOnly = !connManager
                    .isNetworkSupported(ConnectivityManager.TYPE_MOBILE);
            Log.i("@M_" + TAG, "bWifiOnly: " + bWifiOnly);
        }
        return bWifiOnly;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mPagerAdapter.updateCurrentFragment();
    }


    class MyPagerAdapter extends PagerAdapter {
        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private Fragment mCurPrimaryItem;

        MyPagerAdapter() {
            mFragmentManager = getFragmentManager();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TAB_TITLE_IDS[position]).toString();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment fragment = getFragment(position);
            mCurTransaction.show(fragment);

            // Non primary pages are not visible.
            fragment.setUserVisibleHint(fragment.equals(mCurPrimaryItem));
            return fragment;
        }

        @Override
        public void finishUpdate(View container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public void setPrimaryItem(View container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (!fragment.equals(mCurPrimaryItem)) {
                if (mCurPrimaryItem != null) {
                    mCurPrimaryItem.setUserVisibleHint(false);
                }
                mCurPrimaryItem = fragment;
                mCurPrimaryItem.setUserVisibleHint(true);
            }
        }

        private Fragment getFragment(int position) {
            if (position < TAB_COUNT) {
                return mTabs[position];
            }
            throw new IllegalArgumentException("position: " + position);
        }

        public void updateCurrentFragment() {
            if (mCurPrimaryItem != null) {
                mCurPrimaryItem.setUserVisibleHint(true);
            }

        }
    }

}
