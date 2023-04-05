


package com.mediatek.engineermode.nonsleep;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.engineermode.R;

import java.util.List;
/**
 * Non sleep mode : keep display on when activity is up
 * @author mtk54040
 *
 */
public class NonSleepMode extends Activity implements OnClickListener, ServiceConnection {

    private static final String TAG = "EM/NonSleep";
    private Button mSetButton;
    private EMWakeLockService mWakeLockServ = null;

    private static boolean isServiceRunning(Context context, Class<? extends Service> clazz) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxCount = 100;
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(maxCount);
        while (runningServices.size() == maxCount) {
            maxCount += 50;
            runningServices = am.getRunningServices(maxCount);
        }

        for (int i = 0; i < runningServices.size(); i++) {
            ActivityManager.RunningServiceInfo info = runningServices.get(i);
            if (info.service.getClass().equals(clazz)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_sleep_mode);
        mSetButton = (Button) findViewById(R.id.non_sleep_switch);

        mSetButton.setOnClickListener(this);
        if (!isServiceRunning(this, EMWakeLockService.class)) {
            startService(new Intent(this, EMWakeLockService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSetButton.setEnabled(false);
        Intent intent = new Intent(this, EMWakeLockService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        unbindService(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isServiceRunning(this, EMWakeLockService.class) &&
                mWakeLockServ != null && !mWakeLockServ.isHeld()) {
            stopService(new Intent(this, EMWakeLockService.class));
        }
        super.onDestroy();
    }

    /**
     * set flag value
     * */
    public void onClick(final View arg0) {

        if (arg0 == mSetButton) {
            if ((getString(R.string.non_sleep_enable)).equals(
                    mSetButton.getText())) {
                mSetButton.setText(R.string.non_sleep_disable);
                if (!mWakeLockServ.isHeld()) {
                    mWakeLockServ.acquire(NonSleepMode.class);
                }
            } else {
                mSetButton.setText(R.string.non_sleep_enable);
                if (mWakeLockServ.isHeld()) {
                    mWakeLockServ.release();
                }
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        EMWakeLockService.LocalBinder binder = (EMWakeLockService.LocalBinder) service;
        mWakeLockServ = binder.getService();
        mSetButton.setEnabled(true);
        if (mWakeLockServ.isHeld()) {
            mSetButton.setText(R.string.non_sleep_disable);
        } else {
            mSetButton.setText(R.string.non_sleep_enable);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        Log.d("@M_" + TAG, "onServiceDisconnected");

    }
}
