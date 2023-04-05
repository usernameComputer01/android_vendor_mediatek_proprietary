

package com.mediatek.engineermode;

import android.util.Log;

/**
 * Description: a help class for printing log.
 *
 * @author mtk54043
 *
 */

public class Elog {

    /**
     *
     * @param tag
     *            Log's tag
     * @param content
     *            Log' content
     */
    public static final void v(String tag, String content) {
        Log.v("@M_" + tag, content);
    }

    /**
     *
     * @param tag
     *            Log's tag
     * @param content
     *            Log' content
     */
    public static final void i(String tag, String content) {
        Log.i("@M_" + tag, content);
    }

    /**
     *
     * @param tag
     *            Log's tag
     * @param content
     *            Log' content
     */
    public static final void w(String tag, String content) {
        Log.w("@M_" + tag, content);
    }

    /**
     *
     * @param tag
     *            Log's tag
     * @param content
     *            Log' content
     */
    public static final void e(String tag, String content) {
        Log.e("@M_" + tag, content);
    }

    /**
     *
     * @param tag
     *            Log's tag
     * @param content
     *            Log' content
     */
    public static final void d(String tag, String content) {
        Log.d("@M_" + tag, content);
    }
}
