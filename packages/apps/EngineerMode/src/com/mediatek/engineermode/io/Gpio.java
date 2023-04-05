
package com.mediatek.engineermode.io;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.engineermode.R;

public class Gpio extends Activity implements OnClickListener {

    private Button mBtnDirIn;
    private Button mBtnDirOut;
    private Button mBtnDataHigh;
    private Button mBtnDataLow;

    private EditText mEdit;
    private TextView mMaxGpioNum;

    private static final String TAG = "EM/IO/GPIO";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gpio);

        mBtnDirIn = (Button) findViewById(R.id.GPIO_Direction_In);
        mBtnDirOut = (Button) findViewById(R.id.GPIO_Direction_Out);
        mBtnDataHigh = (Button) findViewById(R.id.GPIO_Data_High);
        mBtnDataLow = (Button) findViewById(R.id.GPIO_Data_Low);

        mEdit = (EditText) findViewById(R.id.GPIO_Edit_Value);
        mMaxGpioNum = (TextView) findViewById(R.id.GPIO_Setnomax_Text);

        mBtnDirIn.setOnClickListener(this);
        mBtnDirOut.setOnClickListener(this);
        mBtnDataHigh.setOnClickListener(this);
        mBtnDataLow.setOnClickListener(this);

        // mMaxGpioNum.setText("max");
    }

    @Override
    public void onPause() {
        Log.v("@M_" + TAG, "-->onPause");
        EmGpio.gpioUnInit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v("@M_" + TAG, "-->onResume");
        super.onResume();
        boolean ret = EmGpio.gpioInit();
        if (!ret) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Driver Init error!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Gpio.this.finish();
                        }
                    });
            builder.create().show();
        } else {
            mMaxGpioNum.setText("Max Num:"
                    + String.valueOf(EmGpio.getGpioMaxNumber() - 1));
        }

    }

    public void onClick(View arg0) {
        String editString = mEdit.getText().toString();
        if (null == editString || editString.equals("")
                || editString.length() > 4) {
            Toast.makeText(this, "Please input the value.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        int inputValue = Integer.valueOf(mEdit.getText().toString());
        if (inputValue > EmGpio.getGpioMaxNumber() - 1 || inputValue < 0) {
            Toast.makeText(this, "ERR: Value is too small or too big.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        boolean setFlag = false;
        if (arg0.getId() == mBtnDirIn.getId()) {

            boolean ret = EmGpio.setGpioInput(inputValue);
            if (ret) {
                // ShowDialog("Set Success", "Set Direction Input succeeded.");
//                Toast.makeText(this, "Set Direction Input succeeded.",
//                        Toast.LENGTH_LONG).show();
                setFlag = true;
            }

        } else if (arg0.getId() == mBtnDirOut.getId()) {
            boolean ret = EmGpio.setGpioOutput(inputValue);
            if (ret) {
                setFlag = true;
            } 
        } else if (arg0.getId() == mBtnDataHigh.getId()) {
            boolean ret = EmGpio.setGpioDataHigh(inputValue);
            if (ret) {
                setFlag = true;
            } 

        } else if (arg0.getId() == mBtnDataLow.getId()) {
            boolean ret = EmGpio.setGpioDataLow(inputValue);
            if (ret) {
                setFlag = true;
            } 
        }
        if (setFlag) {
            Toast.makeText(this, "Set success.",
                  Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Set fail.",
                    Toast.LENGTH_SHORT).show();
        }
        
    }

}
