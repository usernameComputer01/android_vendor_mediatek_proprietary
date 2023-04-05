
package com.mediatek.engineermode.io;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.engineermode.R;
import com.mediatek.engineermode.ShellExe;

import java.io.IOException;

public class Eint extends Activity implements OnClickListener {

    private TextView mDispSensitivity;
    private TextView mDispPolarity;
    private Button mBtnQuery;
    private EditText mEdit;
    private static final String TAG = "EM/IO/EINT";
    private static final String ROOT_DIR = "/sys/bus/platform/drivers/eint/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eint);

        mDispSensitivity = (TextView) findViewById(R.id.EINT_sensitivity);
        mDispPolarity = (TextView) findViewById(R.id.EINT_polarity);

        mBtnQuery = (Button) findViewById(R.id.EINT_query);
        mEdit = (EditText) findViewById(R.id.EINT_edit);

        mBtnQuery.setOnClickListener(this);

    }


    private void showDialog(String title, String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(info);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public void onClick(View arg0) {
        Log.v("@M_" + TAG, "-->onClick");
        try {
            if (arg0.getId() == mBtnQuery.getId()) {
                String editString = mEdit.getText().toString();
                if (null == editString || editString.equals("")
                        || editString.length() > 4) {
                    Toast.makeText(this, "Please input the NO..",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String[] cmd = { "/system/bin/sh", "-c",
                        "echo " + editString + " > " + ROOT_DIR + "current_eint" };
                int ret = ShellExe.execCommand(cmd);
                if (0 != ret) {
                    mDispSensitivity.setText("Set EINT NO. Error.");
                    mDispPolarity.setText("Set EINT NO. Error.");
                    return;
                }

                cmd[2] = "cat  " + ROOT_DIR + "current_eint";
                ret = ShellExe.execCommand(cmd);
                if (0 != ret) {
                    mDispSensitivity.setText("Query Error.");
                    mDispPolarity.setText("Query Error.");
                    return;
                }

                if (!ShellExe.getOutput().equalsIgnoreCase(editString)) {
                    mDispSensitivity.setText("No Such EINT NO..");
                    mDispPolarity.setText("No Such EINT NO..");
                    return;
                }

                cmd[2] = "cat " + ROOT_DIR + "current_eint_sens";
                ret = ShellExe.execCommand(cmd);
                if (0 != ret) {
                    mDispSensitivity.setText("Get Sensitivity Error.");
                } else {
                    if (ShellExe.getOutput().equalsIgnoreCase("0")) {
                        mDispSensitivity.setText("edge");
                    } else {
                        mDispSensitivity.setText("level");
                    }

                }

                cmd[2] = "cat " + ROOT_DIR + "current_eint_pol";
                ret = ShellExe.execCommand(cmd);
                if (0 != ret) {
                    mDispPolarity.setText("Get Polarity Error.");
                    return;
                } else {
                    if (ShellExe.getOutput().equalsIgnoreCase("0")) {
                        mDispPolarity.setText("active-low");
                    } else {
                        mDispPolarity.setText("active-high");
                    }
                }

                return;
            }

        } catch (IOException e) {
            Log.i("@M_" + TAG, e.toString());
            showDialog("Shell Exception!", e.toString());
        }
    }
}
