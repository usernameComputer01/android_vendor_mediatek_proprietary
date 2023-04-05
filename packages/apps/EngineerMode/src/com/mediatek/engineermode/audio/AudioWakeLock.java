

package com.mediatek.engineermode.audio;

import android.app.Activity;
import android.media.AudioSystem;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

/**
 * Audio Wake Lock main activity.
 * @author mtk
 */
public class AudioWakeLock extends Activity implements
        android.view.View.OnClickListener {
    private static final String TAG = "Audio/WakeLock";
    private static final String PARAM_WAKELOCK = "AudioTestWakelock";
    private static final String PARAM_VALUE_LOCK = "1";
    private static final String PARAM_VALUE_UNLOCK = "0";

    private ToggleButton mTbtnState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_wakelock);
        mTbtnState = (ToggleButton) findViewById(R.id.audio_wakelock_state_tbtn);
        boolean heldLock = isHeldWakeLock();
        updateWakeLockUi(heldLock);
        mTbtnState.setChecked(heldLock);
        mTbtnState.setOnClickListener(this);
    }

    private boolean isHeldWakeLock() {
        String param = AudioSystem.getParameters(PARAM_WAKELOCK);
        if (param == null) {
            Elog.d(TAG, "get null parameter of audio wake lock");
            return false;
        }
        String[] params = param.trim().split(" *; *");
        param = params[0];
        if (!param.contains(PARAM_WAKELOCK)) {
            Elog.d(TAG, "invalid audio wake lock parameter:" + param);
            return false;
        }
        String[] pairs = param.split(" *= *");
        if (pairs.length < 2) {
            Elog.d(TAG, "Invalid pairs length:" + param);
            return false;
        }
        String value = pairs[1].trim();
        if (PARAM_VALUE_LOCK.equals(value)) {
            return true;
        }
        return false;
    }

    private String buildWakeLockParam(boolean acquireLock) {
        String param = PARAM_WAKELOCK + "=";
        if (acquireLock) {
            param += PARAM_VALUE_LOCK;
        } else {
            param += PARAM_VALUE_UNLOCK;
        }
        return param;
    }

    private void enableAudioWakeLock(boolean acquireLock) {
        String param = buildWakeLockParam(acquireLock);
        Elog.d(TAG, "enableAudioWakelock " + acquireLock + " param:" + param);
        AudioSystem.setParameters(param);
    }

    private void updateWakeLockUi(boolean acquireLock) {
        int strId = 0;
        if (acquireLock) {
            strId = R.string.audio_wakelock_lock;
        } else {
            strId = R.string.audio_wakelock_unlock;
        }
        mTbtnState.setText(strId);
    }

    @Override
    public void onClick(View view) {
        if (view == mTbtnState) {
            boolean acquireLock = mTbtnState.isChecked();
            enableAudioWakeLock(acquireLock);
            updateWakeLockUi(acquireLock);
        }
    }
}
