


package com.mediatek.engineermode.power;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.mediatek.engineermode.ChipSupport;
import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ChargeBattery extends Activity {

    private static final String TAG = "EM_BATTERY_CHARGE";

    private TextView mInfo = null;
    private String mCmdString = null;
    private static final int EVENT_UPDATE = 1;
    private static final float FORMART_TEN = 10.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_charge);

        mInfo = (TextView) findViewById(R.id.battery_charge_info_text);

        if (ChipSupport.getChip() == ChipSupport.MTK_6573_SUPPORT) {
            mCmdString = "cat /sys/devices/platform/mt6573-battery/";
        } else if (ChipSupport.getChip() == ChipSupport.MTK_6575_SUPPORT) {
            // mCmdString = "cat /sys/devices/platform/mt6575-battery/";
            // Jelly Bean (James Lo)
            mCmdString = "cat /sys/devices/platform/mt6329-battery/";
        } else if (ChipSupport.getChip() == ChipSupport.MTK_6577_SUPPORT) {
            // 6577 branch
            // Jelly Bean (James Lo)
            // mCmdString = "cat /sys/devices/platform/mt6577-battery/";
            mCmdString = "cat /sys/devices/platform/mt6329-battery/";
        } else if (ChipSupport.getChip() == ChipSupport.MTK_6589_SUPPORT) {
            mCmdString = "cat /sys/devices/platform/mt6320-battery/";
        } else if (ChipSupport.getChip() > ChipSupport.MTK_6589_SUPPORT) {
            mCmdString = "cat /sys/devices/platform/battery/";
        } else {
            mCmdString = "";
        }
    }

    private String getInfo(String cmd) {
        String result = null;
        try {
            String[] cmdx = { "/system/bin/sh", "-c", cmd }; // file must
            // exist// or
            // wait()
            // return2
            int ret = ShellExe.execCommand(cmdx);
            if (0 == ret) {
                result = ShellExe.getOutput();
            } else {
                // result = "ERROR";
                result = ShellExe.getOutput();
            }
        } catch (IOException e) {
            Elog.i(TAG, e.toString());
            result = "ERR.JE";
        }
        return result;
    }

    private static final int UPDATE_INTERVAL = 500; // 0.5 sec

    public Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EVENT_UPDATE:
                Bundle b = msg.getData();
                mInfo.setText(b.getString("INFO"));
                break;
            default:
                break;
            }
        }
    };

    private final String[][] mFiles = {

    { "ADC_Charger_Voltage", "mV" }, { "Power_On_Voltage", "mV" }, { "Power_Off_Voltage", "mV" },
            { "Charger_TopOff_Value", "mV" }, { "FG_Battery_CurrentConsumption", "mA" }, { "SEP", "" },
            { "ADC_Channel_0_Slope", "" }, { "ADC_Channel_1_Slope", "" }, { "ADC_Channel_2_Slope", "" },
            { "ADC_Channel_3_Slope", "" }, { "ADC_Channel_4_Slope", "" }, { "ADC_Channel_5_Slope", "" },
            { "ADC_Channel_6_Slope", "" }, { "ADC_Channel_7_Slope", "" }, { "ADC_Channel_8_Slope", "" },
            { "ADC_Channel_9_Slope", "" }, { "ADC_Channel_10_Slope", "" }, { "ADC_Channel_11_Slope", "" },
            { "ADC_Channel_12_Slope", "" }, { "ADC_Channel_13_Slope", "" }, { "SEP", "" }, { "ADC_Channel_0_Offset", "" },
            { "ADC_Channel_1_Offset", "" }, { "ADC_Channel_2_Offset", "" }, { "ADC_Channel_3_Offset", "" },
            { "ADC_Channel_4_Offset", "" }, { "ADC_Channel_5_Offset", "" }, { "ADC_Channel_6_Offset", "" },
            { "ADC_Channel_7_Offset", "" }, { "ADC_Channel_8_Offset", "" }, { "ADC_Channel_9_Offset", "" },
            { "ADC_Channel_10_Offset", "" }, { "ADC_Channel_11_Offset", "" }, { "ADC_Channel_12_Offset", "" },
            { "ADC_Channel_13_Offset", "" } };

    private boolean mRun = false;

    private float getMeanBatteryVal(String filePath, int totalCount, int intervalMs) {
        float meanVal = 0.0f;
        if (totalCount <= 0) {
            return 0.0f;
        }
        for (int i = 0; i < totalCount; i++) {
            try {
                float f = Float.valueOf(getFileContent(filePath));
                meanVal += f / totalCount;
            } catch (NumberFormatException e) {
                Elog.e("EM-PMU", "getMeanBatteryVal invalid result from cmd:" + filePath);
            }
            if (intervalMs <= 0) {
                continue;
            }
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Elog.e(TAG, "Catch InterruptedException");
            }
        }
        return meanVal;
    }

    private static String getFileContent(String filePath) {
        if (filePath == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            char[] buffer = new char[500];
            int ret = -1;
            while ((ret = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, ret);
            }
        } catch (IOException e) {
            Elog.e(TAG, "IOException:" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Elog.e(TAG, "IOException:" + e.getMessage());
                }
            }
        }
        String result = builder.toString();
        if (result != null) {
            result = result.trim();
        }
        return result;
    }

    class FunctionThread extends Thread {

        @Override
        public void run() {
            while (mRun) {
                StringBuilder text = new StringBuilder("");
                String cmd = "";
                for (int i = 0; i < mFiles.length; i++) {
                    if (mFiles[i][0].equalsIgnoreCase("SEP")) {
                        text.append("- - - - - - - - -\n");
                        continue;
                    }
                    cmd = mCmdString + mFiles[i][0];
                    String filePath = cmd;
                    if (filePath.startsWith("cat ")) {
                        filePath = filePath.substring(3).trim();
                    }
                    if (mFiles[i][1].equalsIgnoreCase("mA")) {
                        double f = 0.0f;
                        if (mFiles[i][0].equalsIgnoreCase("FG_Battery_CurrentConsumption")) {
                            f = getMeanBatteryVal(filePath, 5, 0) / FORMART_TEN;
                        } else {
                            try {
                                f = Float.valueOf(getFileContent(filePath)) / FORMART_TEN;
                            } catch (NumberFormatException e) {
                                Elog.e("EM-PMU", "read file error " + mFiles[i][0]);
                            }
                        }
                        text.append(String.format("%1$-28s:[ %2$-6s ]%3$s\n", mFiles[i][0], f, mFiles[i][1]));
                    } else {
                        text.append(String.format("%1$-28s: [ %2$-6s ]%3$s\n",
                                mFiles[i][0], getFileContent(filePath), mFiles[i][1]));
                    }
                }

                Bundle b = new Bundle();
                b.putString("INFO", text.toString());

                Message msg = new Message();
                msg.what = EVENT_UPDATE;
                msg.setData(b);

                mUpdateHandler.sendMessage(msg);
                try {
                    sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    Elog.e(TAG, "Catch InterruptedException");
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRun = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRun = true;
        new FunctionThread().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
