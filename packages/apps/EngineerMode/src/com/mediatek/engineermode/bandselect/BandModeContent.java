
package com.mediatek.engineermode.bandselect;

public class BandModeContent {
    /** GSM mode bit. */
    public static final int GSM_EGSM900_BIT = 1;
    public static final int GSM_DCS1800_BIT = 3;
    public static final int GSM_PCS1900_BIT = 4;
    public static final int GSM_GSM850_BIT = 7;

    /** UMTS mode bit. */
    public static final int UMTS_BAND_I_BIT = 0;
    public static final int UMTS_BAND_II_BIT = 1;
    public static final int UMTS_BAND_III_BIT = 2;
    public static final int UMTS_BAND_IV_BIT = 3;
    public static final int UMTS_BAND_V_BIT = 4;
    public static final int UMTS_BAND_VI_BIT = 5;
    public static final int UMTS_BAND_VII_BIT = 6;
    public static final int UMTS_BAND_VIII_BIT = 7;
    public static final int UMTS_BAND_IX_BIT = 8;
    public static final int UMTS_BAND_X_BIT = 9;

    /** Event or message id. */
    public static final int EVENT_QUERY_SUPPORTED = 100;
    public static final int EVENT_QUERY_CURRENT = 101;
    public static final int EVENT_SET = 110;
    public static final int EVENT_SET_OK = 0;
    public static final int EVENT_SET_FAIL = 1;
    public static final int EVENT_RESET = 2;

    public static final long GSM_MAX_VALUE = 0xFFL;
    public static final long UMTS_MAX_VALUE = 0xFFFFL;
    public static final long LTE_MAX_VALUE = 0xFFFFFFFFL;

    /** AT Command. */
    public static final String QUERY_SUPPORT_COMMAND = "AT+EPBSE=?";
    public static final String QUERY_CURRENT_COMMAND = "AT+EPBSE?";
    public static final String SET_COMMAND = "AT+EPBSE=";
    public static final String SAME_COMMAND = "+EPBSE:";

    public static final int EVENT_QUERY_SUPPORTED_CDMA = 102;
    public static final int EVENT_QUERY_CURRENT_CDMA = 103;
    public static final int EVENT_SET_CDMA = 111;
    public static final String QUERY_SUPPORT_COMMAND_CDMA = "AT+ECBAND=?";
    public static final String QUERY_CURRENT_COMMAND_CDMA = "AT+ECBAND?";
    public static final String SET_COMMAND_CDMA = "AT+ECBAND=";
    public static final String SAME_COMMAND_CDMA = "+ECBAND:";
}
