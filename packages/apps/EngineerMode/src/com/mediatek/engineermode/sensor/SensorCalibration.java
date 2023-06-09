
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
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.engineermode.R;
import java.util.Locale;

public class SensorCalibration extends Activity implements OnClickListener {
    public static final String CALIBRAION_TYPE = "type";
    public static final int GSENSOR = 0;
    public static final int GYROSCOPE = 1;
    private static final String TAG = "SensorCalibration";
    private static final int MSG_DO_CALIBRARION_20 = 0;
    private static final int MSG_DO_CALIBRARION_40 = 1;
    private static final int MSG_CLEAR_CALIBRARION = 2;
    private static final int MSG_GET_CALIBRARION = 3;
    private static final int MSG_SET_SUCCESS = 4;
    private static final int MSG_GET_SUCCESS = 5;
    private static final int MSG_SET_FAILURE = 6;
    private static final int MSG_GET_FAILURE = 7;
    private static final int TOLERANCE_20 = 2;
    private static final int TOLERANCE_40 = 4;
    private static final String[] SENSOR_NAME = {"GSENSOR", "GYROSCOPE"};

    private Button mSetCalibration20;
    private Button mSetCalibration40;
    private Button mClearCalibration;
    private TextView mCaliData;
    private TextView mCurrentData;
    private Toast mToast;

    private int mType;
    private int mSensorType;
    private String mData;

    private final HandlerThread mHandlerThread = new HandlerThread("async_handler");
    private Handler mHandler;
    private Handler mUiHandler;

    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == mSensorType) {
                Log.d("@M_" + TAG, String.format("onSensorChanged(), type %d, values %f, %f, %f",
                        event.sensor.getType(), event.values[0], event.values[1], event.values[2]));
                mCurrentData.setText(String.format(Locale.ENGLISH, "%+8.4f,%+8.4f,%+8.4f",
                        event.values[0], event.values[1], event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };

    private String getSensorName(int type) {
        if (type < 0 || type >= SENSOR_NAME.length) {
            return "Sensor";
        }
        return SENSOR_NAME[type];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_calibration);

        mType = getIntent().getIntExtra(CALIBRAION_TYPE, GSENSOR);
        Log.d("@M_" + TAG, String.format("onCreate(), type %d", mType));
        if (mType == GSENSOR) {
            mSensorType = Sensor.TYPE_ACCELEROMETER;
            setTitle(R.string.sensor_calibration_gsensor);
        } else {
            mSensorType = Sensor.TYPE_GYROSCOPE;
            setTitle(R.string.sensor_calibration_gyroscope);
        }

        mSetCalibration20 = (Button) findViewById(R.id.button_sensor_calibration_do_20);
        mSetCalibration40 = (Button) findViewById(R.id.button_sensor_calibration_do_40);
        mClearCalibration = (Button) findViewById(R.id.button_sensor_calibration_clear);

        mSetCalibration20.setOnClickListener(this);
        mSetCalibration40.setOnClickListener(this);
        mClearCalibration.setOnClickListener(this);

        mCurrentData = (TextView) findViewById(R.id.text_sensor_calibration_current_data);
        mCurrentData.setText("");
        mCaliData = (TextView) findViewById(R.id.text_sensor_calibration_cali_data);
        mCaliData.setText("");

        mUiHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_SET_SUCCESS:
                    Log.d("@M_" + TAG, "set success");
                    enableButtons(true);
                    showToast("Operation succeed");
                    break;
                case MSG_GET_SUCCESS:
                    Log.d("@M_" + TAG, "get success");
                    mCaliData.setText(mData);
                    break;
                case MSG_SET_FAILURE:
                    Log.d("@M_" + TAG, "set fail");
                    enableButtons(true);
                    showToast("Operation failed");
                    break;
                case MSG_GET_FAILURE:
                    Log.d("@M_" + TAG, "get fail");
                    enableButtons(true);
                    showToast("Get calibration failed");
                    break;
                default:
                }
            }
        };

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (MSG_GET_CALIBRARION == msg.what) {
                    getCalibration();
                } else {
                    setCalibration(msg.what);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@M_" + TAG, String.format("onResume(), type %d", mType));
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(mSensorType);
        if (mSensor != null) {
            Log.d("@M_" + TAG, "registerListener");
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, getSensorName(mType) + " was not supported.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        mHandler.sendEmptyMessage(MSG_GET_CALIBRARION);
    }

    @Override
    public void onPause() {
        Log.d("@M_" + TAG, String.format("onPause(), type %d", mType));
        Log.d("@M_" + TAG, "unregisterListener");
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorManager = null;
        super.onPause();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == mSetCalibration20.getId()) {
            Log.d("@M_" + TAG, "do calibration 20");
            mHandler.sendEmptyMessage(MSG_DO_CALIBRARION_20);
        } else if (arg0.getId() == mSetCalibration40.getId()) {
            Log.d("@M_" + TAG, "do calibration 40");
            mHandler.sendEmptyMessage(MSG_DO_CALIBRARION_40);
        } else if (arg0.getId() == mClearCalibration.getId()) {
            Log.d("@M_" + TAG, "clear calibration");
            mHandler.sendEmptyMessage(MSG_CLEAR_CALIBRARION);
        }
        enableButtons(false);
    }

    private boolean getCalibration() {
        Log.d("@M_" + TAG, "getGsensorCalibration()");
        float[] result = new float[3];
        int ret = 0;
        if (mType == GSENSOR) {
            ret = EmSensor.getGsensorCalibration(result);
        } else {
            ret = EmSensor.getGyroscopeCalibration(result);
        }
        Log.d("@M_" + TAG, String.format("getGsensorCalibration(), ret %d, values %f, %f, %f",
                ret, result[0], result[1], result[2]));

        if (ret == EmSensor.RET_SUCCESS) {
            mData = String.format(Locale.ENGLISH, "%+8.4f,%+8.4f,%+8.4f",
                    result[0], result[1], result[2]);
            mUiHandler.sendEmptyMessage(MSG_GET_SUCCESS);
            return true;
        } else {
            mData = "";
            mUiHandler.sendEmptyMessage(MSG_GET_FAILURE);
            return false;
        }
    }

    private void setCalibration(int what) {
        int result = 0;
        Log.d("@M_" + TAG, String.format("setCalibration(), operation %d", what));
        if (mType == GSENSOR) {
            if (MSG_DO_CALIBRARION_20 == what) {
                result = EmSensor.doGsensorCalibration(TOLERANCE_20);
            } else if (MSG_DO_CALIBRARION_40 == what) {
                result = EmSensor.doGsensorCalibration(TOLERANCE_40);
            } else if (MSG_CLEAR_CALIBRARION == what) {
                result = EmSensor.clearGsensorCalibration();
            }
        } else if (mType == GYROSCOPE) {
            if (MSG_DO_CALIBRARION_20 == what) {
                result = EmSensor.doGyroscopeCalibration(TOLERANCE_20);
            } else if (MSG_DO_CALIBRARION_40 == what) {
                result = EmSensor.doGyroscopeCalibration(TOLERANCE_40);
            } else if (MSG_CLEAR_CALIBRARION == what) {
                result = EmSensor.clearGyroscopeCalibration();
            }
        }
        Log.d("@M_" + TAG, String.format("setCalibration(), ret %d", result));

        if (result == EmSensor.RET_SUCCESS) {
            if (getCalibration()) {
                mUiHandler.sendEmptyMessage(MSG_SET_SUCCESS);
            }
        } else {
            mUiHandler.sendEmptyMessage(MSG_SET_FAILURE);
        }
    }

    private void enableButtons(boolean enable) {
        mSetCalibration20.setClickable(enable);
        mSetCalibration40.setClickable(enable);
        mClearCalibration.setClickable(enable);
    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

