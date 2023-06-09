
package com.mediatek.engineermode.memory;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.IOException;

/**
 * Class for showing flash information.
 *
 */
@SuppressWarnings("deprecation")
public class Flash extends TabActivity {

    private static final String TAG = "EM/Memory_flash";
    private static final String FILE_NAND = "/proc/driver/nand";
    private static final String FILE_MOUNTS = "/proc/mounts";
    private static final String FILE_PARTITION = "/proc/partitions";
    private static final String FILE_MTD = "/proc/mtd";
    private static final String FILE_CID = "/sys/block/mmcblk0/device/cid";
    private static final String FILE_DUMCHAR_INFO = "/proc/dumchar_info";
    private static final String FILE_PARTINFO = "/proc/partinfo";
    private static final String READ_COMMAND = "cat ";
    private static final String EMMC_ID_HEADER = "emmc ID: ";
    private boolean mHaveEmmc = false;

    private TextView mTvCommInfo = null;
    private TextView mTvFSInfo = null;
    private TextView mTvPartInfo = null;
    private String mFileSysTabName = null;
    private String mPartitionTabName = null;
    private String mTabId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileSysTabName = getString(R.string.memory_file_sys_info);
        mPartitionTabName = getString(R.string.memory_partition_info);
        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.memory_tabs,
                tabHost.getTabContentView(), true);

        // tab1
        tabHost.addTab(tabHost.newTabSpec(mFileSysTabName).setIndicator(
                mFileSysTabName).setContent(R.id.file_sys_view));

        // tab2
        tabHost.addTab(tabHost.newTabSpec(mPartitionTabName).setIndicator(
                mPartitionTabName).setContent(R.id.partition_view));
        mTvFSInfo = (TextView) findViewById(R.id.file_sys_info);
        mTvPartInfo = (TextView) findViewById(R.id.partition_info);
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                mTabId = tabId;
                showTabContent();
            }
        });

        // init
        mTabId = mFileSysTabName;
        showTabContent();
    }

    /**
     * Show TAB content.
     */
    private void showTabContent() {
        if (mTabId.equals(mFileSysTabName)) {
            mTvFSInfo.setText(getInfo(FILE_MOUNTS));
        } else if (mTabId.equals(mPartitionTabName)) {
            mTvPartInfo.setText(getInfo(FILE_PARTITION));

        }
    }

     /**
     * Get file content.
     *
     * @param file
     *            The file's path
     * @return The file's content
     */
    private String getInfo(String file) {
        String result = null;
        try {
            int ret = ShellExe.execCommand(READ_COMMAND + file);
            if (0 == ret) {
                result = ShellExe.getOutput();
            } else {
                result = getString(R.string.memory_getinfo_error);
            }
        } catch (IOException e) {
            Log.i("@M_" + TAG, e.toString());
            result = e.toString();
        }
        return result;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        showTabContent();
    }

}
