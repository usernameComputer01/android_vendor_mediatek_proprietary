




package com.mediatek.engineermode;

public class ChipSupport {

    /**
     * Chip support.
     */
    public static final int MTK_UNKNOWN_SUPPORT = 0;
    public static final int MTK_6573_SUPPORT = 1;
    public static final int MTK_6516_SUPPORT = 2;
    public static final int MTK_6575_SUPPORT = 4;
    public static final int MTK_6577_SUPPORT = 8;
    public static final int MTK_6589_SUPPORT = 16;
    public static final int MTK_6582_SUPPORT = 18;
    public static final int MTK_6572_SUPPORT = 32;
    public static final int MTK_8135_SUPPORT = 20;
    public static final int MTK_8127_SUPPORT = 22;
    public static final int MTK_6571_SUPPORT = 33;
    public static final int MTK_6592_SUPPORT = 40;
    public static final int MTK_6595_SUPPORT = 42;
    public static final int MTK_6752_SUPPORT = 64;
    public static final int MTK_6795_SUPPORT = 65;
    public static final int MTK_6735_SUPPORT = 66;
    public static final int MTK_8163_SUPPORT = 67;
    public static final int MTK_6580_SUPPORT = 68;
    public static final int MTK_6755_SUPPORT = 69;
    public static final int MTK_6797_SUPPORT = 70;

    /**
     * Feature support.
     */
    public static final int MTK_FM_SUPPORT = 0;
    public static final int MTK_FM_TX_SUPPORT = 1;
    public static final int MTK_RADIO_SUPPORT = 2;
    public static final int MTK_AGPS_APP = 3;
    public static final int MTK_GPS_SUPPORT = 4;
    public static final int HAVE_MATV_FEATURE = 5;
    public static final int MTK_BT_SUPPORT = 6;
    public static final int MTK_WLAN_SUPPORT = 7;
    public static final int MTK_TTY_SUPPORT = 8;
    public static final int MTK_NFC_SUPPORT = 9;
    public static final int MTK_AUDIO_TUNING_TOOL_V2_1 = 10;

    public static final int[] CHIP_657X_SERIES_NEW = {MTK_6571_SUPPORT, MTK_6572_SUPPORT};
    public static final int[] CHIP_64BIT_SUPPORT = {MTK_6752_SUPPORT, MTK_6795_SUPPORT,
                                                    MTK_6735_SUPPORT, MTK_8163_SUPPORT};

    public static boolean isCurrentChipHigher(int comparedChip, boolean allowEquel) {
        int curChip = getChip();

        if (allowEquel) {
            return curChip >= comparedChip;
        } else {
            return curChip > comparedChip;
        }
    }

    public static boolean isCurrentChipEquals(int targetChip) {
        int chip = getChip();

        return chip == targetChip;
    }

    public static boolean isChipInSet(int[] chips) {
        if (chips == null) {
            return false;
        }
        int chip = getChip();
        for (int c : chips) {
            if (c == chip) {
                return true;
            }
        }
        return false;
    }

    private ChipSupport() {
        /* do nothing - hiding constructor */
    }
    /**
     * Get Chip ID from native.
     *
     * @return Chip ID.
     */
    public static native int getChip();

    /**
     * Check feature supported.
     *
     * @param featureId
     *            The feature ID
     * @return True if feature is supported
     */
    public static native boolean isFeatureSupported(int featureId);

    static {
        System.loadLibrary("em_support_jni");
    }
}
