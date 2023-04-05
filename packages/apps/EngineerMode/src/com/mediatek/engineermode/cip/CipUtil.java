
package com.mediatek.engineermode.cip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.mediatek.engineermode.Elog;

public class CipUtil {
    private static final String TAG = "CipUtil";
    public static final String CIP_PROP_FILE = "/custom/cip-build.prop";
    public static boolean isCipSupported() {
        boolean result = false;
        if (new File(CIP_PROP_FILE).exists()) {
            result = true;
        }
        return result;
    }

    public static String getFileAllContent(File file) {
        if (file == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
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
        return builder.toString();
    }
}
