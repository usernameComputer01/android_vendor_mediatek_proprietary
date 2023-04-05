
package com.mediatek.ygps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class SatelliteBaseView extends View {
    private static final String TAG = "SatelliteBaseView";
    public static final int STATE_UNFIXED = 1;
    public static final int STATE_USED_IN_FIX = 2;
    public static final int STATE_UNUSED_IN_FIX = 3;

    private SatelliteInfoManager mSiManager = null;

    public SatelliteBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SatelliteBaseView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public SatelliteBaseView(Context context) {
        this(context, null, 0);
    }

    public void requestUpdate(SatelliteInfoManager manager) {
        mSiManager = manager;
        this.postInvalidate();
    }

    protected SatelliteInfoManager getSatelliteInfoManager() {
        return mSiManager;
    }

}
