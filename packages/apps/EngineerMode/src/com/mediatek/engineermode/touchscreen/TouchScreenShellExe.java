
package com.mediatek.engineermode.touchscreen;

import com.mediatek.engineermode.ShellExe;
import android.util.Log;

import java.io.IOException;

public class TouchScreenShellExe {

    /*
     * how to use try { execCommand("./data/kenshin/x.sh"); } catch (IOException
     * e) { e.printStackTrace(); } }
     */
    public static final String ERROR = "ERROR";
    private static final String TAG = "EM/TouchScreen_ShellExe";

    public static String getOutput() {
        String result = ShellExe.getOutput();
        Log.i("@M_" + TAG, "getOutPut:sb-- " + result);
        return result;
    }

    public static int execCommand(String[] command) throws IOException {
        return ShellExe.execCommand(command);
    }
}
