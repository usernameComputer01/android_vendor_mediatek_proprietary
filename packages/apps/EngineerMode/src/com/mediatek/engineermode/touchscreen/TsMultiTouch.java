
package com.mediatek.engineermode.touchscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Demonstrates wrapping a layout in a ScrollView.
 *
 *
 */

public class TsMultiTouch extends Activity {
    public static final int CLEAR_CANVAS_ID = 1;
    public static final int SET_PT_SIZE_ID = 2;
    public static final int DIS_HISTORY_ID = 3;
    public static final int[][] RGB = {{255, 0, 0}, {0, 255, 0},
            {0, 0, 255}, {255, 255, 0}, {0, 255, 255}, {255, 0, 255},
            {100, 0, 0}, {0, 100, 0}, {0, 0, 100}, {100, 100, 0},
            {0, 100, 100}, {100, 0, 100}, {255, 255, 255}};
    private static final String TAG = "EM/TouchScreen/MT";
    MyView mView = null;
    volatile boolean mDisplayHistory = true;
    DisplayMetrics mMetrics = new DisplayMetrics();

    private int mPointSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = new MyView(this);
        setContentView(mView);
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences preferences = this.getSharedPreferences(
                "touch_screen_settings", android.content.Context.MODE_PRIVATE);
        String fileString = preferences.getString("filename", "N");
        if (!"N".equals(fileString)) {
            final String commPath = fileString;
            new Thread() {
                public void run() {
                    String[] cmd = {"/system/bin/sh", "-c",
                            "echo [ENTER_MULTI_TOUCH] >> " + commPath}; // file
                    int ret;
                    try {
                        ret = TouchScreenShellExe.execCommand(cmd);
                        if (0 == ret) {
                            Log.v("@M_" + TAG, "-->onResume Start logging...");
                        } else {
                            Log.v("@M_" + TAG, "-->onResume Logging failed!");
                        }
                    } catch (IOException e) {
                        Log.e("@M_" + TAG, e.toString());
                    }
                }
            } .start();

        }
        mPointSize = preferences.getInt("size", 10);
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    @Override
    public void onPause() {
        Log.v("@M_" + TAG, "-->onPause");
        final SharedPreferences preferences = this.getSharedPreferences(
                "touch_screen_settings", android.content.Context.MODE_PRIVATE);
        String fileString = preferences.getString("filename", "N");
        if (!"N".equals(fileString)) {
            String[] cmd = {"/system/bin/sh", "-c",
                    "echo [LEAVE_MULTI_TOUCH] >> " + fileString}; // file
            int ret;
            try {
                ret = TouchScreenShellExe.execCommand(cmd);
                if (0 == ret) {
                    Toast.makeText(this, "Stop logging...", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(this, "Logging failed!", Toast.LENGTH_LONG)
                            .show();
                }
            } catch (IOException e) {
                Log.e("@M_" + TAG, e.toString());
            }
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CLEAR_CANVAS_ID, 0, "Clean Table");
        menu.add(0, SET_PT_SIZE_ID, 0, "Set Point Size");
        menu.add(0, DIS_HISTORY_ID, 0, "Hide History");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDisplayHistory) {
            menu.getItem(2).setTitle("Hide History");
        } else {
            menu.getItem(2).setTitle("Show History");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
        case CLEAR_CANVAS_ID:
            mView.clear();
            break;
        case DIS_HISTORY_ID:
            if (mDisplayHistory) {
                mDisplayHistory = false;
            } else {
                mDisplayHistory = true;
            }
            mView.invalidate();
            break;
        case SET_PT_SIZE_ID:
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(this).setTitle(
                    "Insert pixel size of point [1-10]").setView(input)
                    .setPositiveButton("OK", new OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            boolean valid = true;
                            int sz = 0;
                            CharSequence cText = input.getText();
                            if (cText == null) {
                                valid = false;
                            }  else {
                                try {
                                    sz = Integer.valueOf(cText.toString());
                                } catch (NumberFormatException e) {
                                    valid = false;
                                }
                            }
                            if (sz < 1 || sz > 10) {
                                valid = false;
                            }

                            if (valid) {
                                TsMultiTouch.this.mPointSize = sz;
                                final SharedPreferences preferences = TsMultiTouch.this
                                        .getSharedPreferences(
                                                "touch_screen_settings",
                                                android.content.Context.MODE_PRIVATE);
                                preferences.edit().putInt("size",
                                        TsMultiTouch.this.mPointSize).commit();
                                mView.invalidate();
                            } else {
                                Toast.makeText(TsMultiTouch.this,
                                        "Invalid input, please input 1-10 number",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("Cancel", null).show();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(mi);
    }

    public class MyView extends View {
        private HashMap<Integer, TsPointDataStruct> mCurrPoints = new HashMap<Integer, TsPointDataStruct>();
        private ArrayList<TsPointDataStruct> mHistory = new ArrayList<TsPointDataStruct>();

        public MyView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mDisplayHistory) {
                for (int i = 0; i < mHistory.size(); i++) {
                    TsPointDataStruct point = mHistory.get(i);
                    int x = point.getmCoordinateX();
                    int y = point.getmCoordinateY();
                    canvas.drawCircle(x, y, mPointSize, getPaint(point.getmPid()));
                }
            }
            final String maxString = "pid 9 x=999, y=999";
            for (TsPointDataStruct point : mCurrPoints.values()) {
                Paint targetPaint = getPaint(point.getmPid());
                String s = "pid " + String.valueOf(point.getmPid())
                        + " x=" + String.valueOf(point.getmCoordinateX())
                        + ", y=" + String.valueOf(point.getmCoordinateY());
                Log.i("@M_" + TAG, "Touch pos: " + point.getmCoordinateX() + "," + point.getmCoordinateY());

                Rect rect = new Rect();
                targetPaint.getTextBounds(maxString, 0, maxString.length(), rect);
                if (rect.width() > mMetrics.widthPixels) {
                    targetPaint = getSlimPaint(point.getmPid());
                }
                targetPaint.getTextBounds(s, 0, s.length(), rect);

                int x = point.getmCoordinateX() - rect.width() / 2;
                int y = point.getmCoordinateY() - rect.height() * 3;

                if (x < 0) {
                    x = 0;
                } else if (x > mMetrics.widthPixels - rect.width()) {
                    x = mMetrics.widthPixels - rect.width() - 2;
                }

                if (y < rect.height()) {
                    y = rect.height();
                } else if (y > mMetrics.heightPixels) {
                    y = mMetrics.heightPixels;
                }

                canvas.drawText(s, x, y, targetPaint);
                canvas.drawCircle(point.getmCoordinateX(),
                        point.getmCoordinateY(), mPointSize * 3,
                        targetPaint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.i("@M_" + TAG, "onTouchEvent: Pointer counts: " + event.getPointerCount()
                    + " Action: " + event.getAction());

            for (int i = 0; i < event.getPointerCount(); i++) {
                Log.i("@M_" + TAG, "onTouchEvent: idx: " + i + " pid: " + event.getPointerId(i)
                        + " (" + event.getX(i) + ", " + event.getY(i) + ")");
                int pid = event.getPointerId(i);
                TsPointDataStruct n = new TsPointDataStruct();
                n.setmCoordinateX((int) event.getX(i));
                n.setmCoordinateY((int) event.getY(i));
                n.setmPid(pid);
                mCurrPoints.put(pid, n);
                mHistory.add(n);
            }

            invalidate();
            return true;
        }

        public void clear() {
            mCurrPoints.clear();
            mHistory.clear();
            invalidate();
        }

        Paint getPaint(int idx) {
            Paint paint = new Paint();
            paint.setAntiAlias(false);
            if (idx < RGB.length) {
                paint.setARGB(255, RGB[idx][0], RGB[idx][1], RGB[idx][2]);
            } else {
                paint.setARGB(255, 255, 255, 255);
            }
            int textsize = (int) (mPointSize * 3.63 + 7.37);
            paint.setTextSize(textsize);
            return paint;
        }
        private Paint getSlimPaint(int idx) {
            int pointSize;
            Paint paint = new Paint();
            paint.setAntiAlias(false);
            if (idx < RGB.length) {
                paint.setARGB(255, RGB[idx][0], RGB[idx][1], RGB[idx][2]);
            } else {
                paint.setARGB(255, 255, 255, 255);
            }
            pointSize = mPointSize / 2;
            if (pointSize < 1) {
                pointSize = 1;
            }
            int textsize = (int) (pointSize * 3.63 + 7.37);
            paint.setTextSize(textsize);
            return paint;
        }
    }
}
