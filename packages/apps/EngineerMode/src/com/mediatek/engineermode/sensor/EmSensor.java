
package com.mediatek.engineermode.sensor;

import com.mediatek.engineermode.emsvr.AFMFunctionCallEx;
import com.mediatek.engineermode.emsvr.FunctionReturn;
import android.util.Log;
import java.util.ArrayList;

public class EmSensor {
    private static final String TAG = "EmSensor";
    public static final int RET_SUCCESS = 1;
    public static final int RET_FAILED = 0;

    public static int doGsensorCalibration(int tolerance) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_DO_GSENSOR_CALIBRATION, 1,
                tolerance);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int getGsensorCalibration(float[] result) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_GET_GSENSOR_CALIBRATION, 0);
        if (ret.length >= 4 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            try {
                result[0] = Float.parseFloat(ret[1]);
                result[1] = Float.parseFloat(ret[2]);
                result[2] = Float.parseFloat(ret[3]);
                return RET_SUCCESS;
            } catch (NumberFormatException e) {
                return RET_FAILED;
            }
        }
        return RET_FAILED;
    }

    public static int clearGsensorCalibration() {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_CLEAR_GSENSOR_CALIBRATION, 0);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int doGyroscopeCalibration(int tolerance) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_DO_GYROSCOPE_CALIBRATION, 1,
                tolerance);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int getGyroscopeCalibration(float[] result) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_GET_GYROSCOPE_CALIBRATION, 0);
        if (ret.length >= 4 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            try {
                result[0] = Float.parseFloat(ret[1]);
                result[1] = Float.parseFloat(ret[2]);
                result[2] = Float.parseFloat(ret[3]);
                return RET_SUCCESS;
            } catch (NumberFormatException e) {
                return RET_FAILED;
            }
        }
        return RET_FAILED;
    }

    public static int clearGyroscopeCalibration() {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_CLEAR_GYROSCOPE_CALIBRATION, 0);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int doPsensorCalibration(int min, int max) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_DO_CALIBRATION, 2,
                min, max);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int clearPsensorCalibration() {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_CLEAR_CALIBRATION, 0);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static int setPsensorThreshold(int high, int low) {
        String[] ret = runCmdInEmSvr(
                AFMFunctionCallEx.FUNCTION_EM_SENSOR_SET_THRESHOLD, 2,
                high, low);
        if (ret.length > 0 && String.valueOf(RET_SUCCESS).equals(ret[0])) {
            return RET_SUCCESS;
        }
        return RET_FAILED;
    }

    public static native int getPsensorData();

    public static native int getPsensorThreshold(int[] result);

    public static native int calculatePsensorMinValue();

    public static native int getPsensorMinValue();

    public static native int calculatePsensorMaxValue();

    public static native int getPsensorMaxValue();

    public static String[] runCmdInEmSvr(int index, int paramNum, int... param) {
        ArrayList<String> arrayList = new ArrayList<String>();
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
                arrayList.add(r.mReturnString);
            } while (r.mReturnCode == AFMFunctionCallEx.RESULT_CONTINUE);
            if (r.mReturnCode == AFMFunctionCallEx.RESULT_IO_ERR) {
                Log.d("@M_" + TAG, "AFMFunctionCallEx: RESULT_IO_ERR");
                arrayList.clear();
                arrayList.add("ERROR");
            }
        } else {
            Log.d("@M_" + TAG, "AFMFunctionCallEx return false");
            arrayList.clear();
            arrayList.add("ERROR");
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }

    static {
        System.loadLibrary("em_sensor_jni");
    }
}
