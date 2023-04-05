
package com.mediatek.ygps.tests;

import android.app.Activity;
import android.test.LaunchPerformanceBase;
import android.os.Bundle;


/**
 * Instrumentation class for YGPS launch performance testing.
 */
public class YgpsLaunchPerformance extends LaunchPerformanceBase {

    public static final String LOG_TAG = "YGPSLaunchPerformance";

    public YgpsLaunchPerformance() {
        super();
    }

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);

        mIntent.setClassName(getTargetContext(),
                "com.mediatek.ygps.YgpsActivity");
        start();
    }

    /**
     * Calls LaunchApp and finish.
     */
    @Override
    public void onStart() {
        super.onStart();
        LaunchApp();
        finish(Activity.RESULT_OK, mResults);
    }
}
