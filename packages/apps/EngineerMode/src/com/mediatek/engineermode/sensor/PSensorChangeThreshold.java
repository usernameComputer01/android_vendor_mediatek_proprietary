
package com.mediatek.engineermode.sensor;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mediatek.engineermode.R;


public class PSensorChangeThreshold extends Activity implements OnClickListener {
    private static final String TAG = "PSensorChangeThreshold";
    private static final int MSG_CHANGE_THRESHOLD = 0;
    private static final int MSG_SET_SUCCESS = 1;
    private static final int MSG_SET_FAILURE = 2;
    private static final int MSG_INVALID_NUMBER = 3;
    private static final int THRESHOLD_MIN = 0;
    private static final int THRESHOLD_MAX = 65535;

    private Button mBtnSet;
    private EditText mEtHigh;
    private EditText mEtLow;
    private Toast mToast;

    private final HandlerThread mHandlerThread = new HandlerThread("async_handler");
    private Handler mHandler;
    private Handler mUiHandler;

    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Do nothing
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_ps_change_threshold);

        mBtnSet = (Button) findViewById(R.id.button_sensor_ps_change_threshold);
        mBtnSet.setOnClickListener(this);

        mEtHigh = (EditText) findViewById(R.id.edit_sensor_ps_threshold_high);
        mEtLow = (EditText) findViewById(R.id.edit_sensor_ps_threshold_low);

        mUiHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_SET_SUCCESS:
                    Log.d("@M_" + TAG, "set success");
                    mBtnSet.setClickable(true);
                    showToast("Set threshold succeed");
                    break;
                case MSG_SET_FAILURE:
                    Log.d("@M_" + TAG, "set fail");
                    mBtnSet.setClickable(true);
                    showToast("Set threshold failed");
                    break;
                case MSG_INVALID_NUMBER:
                    Log.d("@M_" + TAG, "set fail");
                    mBtnSet.setClickable(true);
                    showToast("Invalid value");
                    break;
                default:
                }
            }
        };

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (MSG_CHANGE_THRESHOLD == msg.what) {
                    Log.d("@M_" + TAG, String.format("MSG_CHANGE_THRESHOLD"));
                    int high = 0, low = 0;
                    try {
                        high = Integer.parseInt(mEtHigh.getText().toString());
                        low = Integer.parseInt(mEtLow.getText().toString());
                        if (high < THRESHOLD_MIN || high > THRESHOLD_MAX || low < THRESHOLD_MIN || low > THRESHOLD_MAX) {
                            throw new NumberFormatException("");
                        }
                    } catch (NumberFormatException e) {
                        mUiHandler.sendEmptyMessage(MSG_INVALID_NUMBER);
                        return;
                    }

                    if (EmSensor.RET_SUCCESS == EmSensor.setPsensorThreshold(high, low)) {
                        mUiHandler.sendEmptyMessage(MSG_SET_SUCCESS);
                    } else {
                        mUiHandler.sendEmptyMessage(MSG_SET_FAILURE);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@M_" + TAG, "onResume()");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        Log.d("@M_" + TAG, "onPause()");
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorManager = null;
        super.onPause();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == mBtnSet.getId()) {
            Log.d("@M_" + TAG, "change threshold");
            mHandler.sendEmptyMessage(MSG_CHANGE_THRESHOLD);
        }
        mBtnSet.setClickable(false);
    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

