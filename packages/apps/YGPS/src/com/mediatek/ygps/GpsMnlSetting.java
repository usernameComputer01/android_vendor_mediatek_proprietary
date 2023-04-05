
package com.mediatek.ygps;

import android.os.SystemProperties;
import android.util.Log;


import java.util.ArrayList;

/**
 * GPS MNL flag setting
 *
 * @author mtk54046
 * @version 1.0
 */
public class GpsMnlSetting {

    private static final String TAG = "YGPS/Mnl_Setting";
    public static final String PROP_VALUE_0 = "0";
    public static final String PROP_VALUE_1 = "1";
    public static final String PROP_VALUE_2 = "2";

    public static final String KEY_DEBUG_DBG2SOCKET = "debug.dbg2socket";
    public static final String KEY_DEBUG_NMEA2SOCKET = "debug.nmea2socket";
    public static final String KEY_DEBUG_DBG2FILE = "debug.dbg2file";
    public static final String KEY_DEBUG_DEBUG_NMEA = "debug.debug_nmea";
    public static final String KEY_BEE_ENABLED = "BEE_enabled";
    public static final String KEY_TEST_MODE = "test.mode";
    public static final String KEY_SUPLLOG_ENABLED = "SUPPLOG_enabled";
    public static final String KEY_GPS_EPO = "gps.epo";

    private static final String MNL_PROP_NAME = "persist.radio.mnl.prop";
    private static final String GPS_CHIP_PROP = "gps.gps.version";

    private static final String DEFAULT_MNL_PROP = "00011001";
    private static ArrayList<String> sKeyList = null;
    private static final String GPS_CLOCK_PROP = "gps.clock.type";
    /**
     * Get gps chip version
     *
     * @param defaultValue
     *            Default value
     * @return GPS chip version
     */
    public static String getChipVersion(String defaultValue) {
        String chipVersion = SystemProperties.get(GPS_CHIP_PROP);
        if (null == chipVersion || chipVersion.isEmpty()) {
            chipVersion = defaultValue;
        }
        return chipVersion;
    }

    /**
     * Get MNL system property
     *
     * @param key
     *            The key of the property
     * @param defaultValue
     *            The default value of the property
     * @return The value of the property
     */
    public static String getMnlProp(String key, String defaultValue) {
        String result = defaultValue;
        String prop = SystemProperties.get(MNL_PROP_NAME);
        if (null == sKeyList) {
            initList();
        }
        int index = sKeyList.indexOf(key);
        Log.v("@M_" + TAG, "getMnlProp: " + prop);
        if (null == prop || prop.isEmpty()
                || -1 == index || index >= prop.length()) {
            result = defaultValue;
        } else {
            char c = prop.charAt(index);
            result = String.valueOf(c);
        }
        Log.v("@M_" + TAG, "getMnlProp result: " + result);
        return result;
    }

    /**
     * Set MNL system property
     *
     * @param key
     *            The key of the property
     * @param value
     *            The value of the property
     */
    public static void setMnlProp(String key, String value) {
        Log.v("@M_" + TAG, "setMnlProp: " + key + " " + value);
        String prop = SystemProperties.get(MNL_PROP_NAME);
        if (null == sKeyList) {
            initList();
        }
        int index = sKeyList.indexOf(key);
        if (index != -1) {
            if (null == prop || prop.isEmpty()) {
                prop = DEFAULT_MNL_PROP;
            }
            if (prop.length() > index) {
                char[] charArray = prop.toCharArray();
                charArray[index] = value.charAt(0);
                String newProp = String.valueOf(charArray);
                SystemProperties.set(MNL_PROP_NAME, newProp);
                Log.v("@M_" + TAG, "setMnlProp newProp: " + newProp);
            }
        }
    }

    public static String getClockProp(String defaultValue) {
        String clockType = SystemProperties.get(GPS_CLOCK_PROP);
        if (null == clockType || clockType.isEmpty()) {
            clockType = defaultValue;
        }
        return clockType;
    }

    private static void initList() {
        sKeyList = new ArrayList<String>();
        sKeyList.add(KEY_DEBUG_DBG2SOCKET);
        sKeyList.add(KEY_DEBUG_NMEA2SOCKET);
        sKeyList.add(KEY_DEBUG_DBG2FILE);
        sKeyList.add(KEY_DEBUG_DEBUG_NMEA);
        sKeyList.add(KEY_BEE_ENABLED);
        sKeyList.add(KEY_TEST_MODE);
        sKeyList.add(KEY_SUPLLOG_ENABLED);
        sKeyList.add(KEY_GPS_EPO);
    }
}
