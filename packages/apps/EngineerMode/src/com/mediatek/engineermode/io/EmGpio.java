
package com.mediatek.engineermode.io;
import com.mediatek.engineermode.emsvr.AFMFunctionCallEx;
import com.mediatek.engineermode.emsvr.FunctionReturn;
import android.util.Log;
public class EmGpio {
    private static final String RESULT_ERROR = "ERROR";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String TAG = "EM/EmGpio";
    public static native int getGpioMaxNumber();

    public static native boolean gpioInit();

    public static native boolean gpioUnInit();

    public static native boolean setGpioInput(int gpioIndex);

    public static native boolean setGpioOutput(int gpioIndex);

    public static native boolean setGpioDataHigh(int gpioIndex);

    public static native boolean setGpioDataLow(int gpioIndex);

    public static native int getCurrent(int hostNumber);

    public static native boolean setCurrent(int hostNumber, int currentDataIdx,
            int currentCmdIdx);

    public static boolean setSd30Mode(int hostNumber, int sd30Mode,
            int sd30MaxCurrent, int sd30Drive, int sd30PowerControl) {
        String response = runCmdInNative(
        AFMFunctionCallEx.FUNCTION_EM_MSDC_SET30_MODE, 5, hostNumber, sd30Mode, sd30MaxCurrent, sd30Drive, sd30PowerControl);
        if (response.equals(RESULT_ERROR)) {
            return false;
        } else {
            return true;
        }
    }

    public static int newGetCurrent(int hostNumber, int opcode) {
        String response = runCmdInNative(
                AFMFunctionCallEx.FUNCTION_EM_MSDC_GET_CURRENT, 2, hostNumber, opcode);
        Log.d("@M_" + TAG, "newGetCurrent: " + response);
        int idx = -1;
        try {
            idx = Integer.parseInt(response);
        } catch (NumberFormatException e) {
            Log.i("@M_" + TAG, "parseInt failed--invalid number!");
        }
        return idx;
    }

    public static boolean newSetCurrent(int hostNumber, int clkpu,
            int clkpd, int cmdpu, int cmdpd, int datapu, int datapd,
            int rstpu, int rstpd, int dspu, int dspd,
            int hopbit, int hoptime, int opcode) {
        String response = runCmdInNative(
                AFMFunctionCallEx.FUNCTION_EM_MSDC_SET_CURRENT, 14, hostNumber, clkpu, clkpd, cmdpu, cmdpd,
                datapu, datapd, rstpu, rstpd, dspu, dspd, hopbit, hoptime, opcode);
        if (response.equals(RESULT_ERROR)) {
            return false;
        } else {
            return true;
        }

    }
    private static String runCmdInNative(int index, int paramNum, int... param) {
        StringBuilder build = new StringBuilder();
        AFMFunctionCallEx functionCall = new AFMFunctionCallEx();
        boolean result = functionCall.startCallFunctionStringReturn(index);
        functionCall.writeParamNo(paramNum);
        for (int i : param) {
            functionCall.writeParamInt(i);
        }
        if (result) {
            FunctionReturn r;
            do {
                r = functionCall.getNextResult();
                if (r.mReturnString.isEmpty()) {
                    break;
                }
                build.append(r.mReturnString);
            } while (r.mReturnCode == AFMFunctionCallEx.RESULT_CONTINUE);
            if (r.mReturnCode == AFMFunctionCallEx.RESULT_IO_ERR) {
                Log.d("@M_" + TAG, "AFMFunctionCallEx: RESULT_IO_ERR");
                build.replace(0, build.length(), RESULT_ERROR);
            }
        } else {
            Log.d("@M_" + TAG, "AFMFunctionCallEx return false");
            build.append(RESULT_ERROR);
        }
        return build.toString();
    }
    static {
        System.loadLibrary("em_gpio_jni");

    }
}
