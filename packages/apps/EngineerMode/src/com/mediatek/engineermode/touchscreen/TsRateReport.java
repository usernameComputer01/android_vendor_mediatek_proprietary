
package com.mediatek.engineermode.touchscreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import java.util.HashMap;

/**
 * Demonstrates wrapping a layout in a ScrollView.
 */

public class TsRateReport extends Activity {
    private static final String TAG = "EM/TouchScreen/RR";

    MyView mView = null;
    DisplayMetrics mMetrick = new DisplayMetrics();

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
        getWindowManager().getDefaultDisplay().getMetrics(mMetrick);
    }


    public class MyView extends View {
        private final HashMap<Integer, PointerData> mPtsStatus = new HashMap<Integer, PointerData>();
        private int mPointerNumDetected = 0;

        public MyView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.v("@M_" + TAG, "-->onDraw");
            int textsize = 15;
            canvas.drawText("Pointer number detected: "
                    + String.valueOf(mPointerNumDetected), 3, textsize + 10,
                    getPaint(4, textsize));
            Log.v("@M_" + TAG, "Pointer number detected: " + mPointerNumDetected);

            for (PointerData pt : mPtsStatus.values()) {
                pt.setUTimeStamp();
                pt.calculateRate();
                String s = String.format("pid=%2d, X=%3d, Y=%3d.", pt.mPid,
                        pt.mLastX, pt.mLastY);
                String ss = String.format("Rate=%dHz, Count=%d, Time=%dms",
                        pt.mRate, pt.mCnt, pt.mMills);

                int x = 3;
                int y = 10 + (textsize * 3) + pt.mPid * 3 * textsize;

                canvas.drawText(s, x, y, getPaint(pt.mPid, textsize));
                canvas.drawText(ss, x, y + textsize, getPaint(pt.mPid, textsize));
            }
        }

        private class PointerData {
            public volatile int mCnt;
            public volatile int mRate;
            public volatile int mMills;
            public volatile int mPid;
            public volatile int mLastX;
            public volatile int mLastY;
            private volatile long mDownTime;
            private volatile long mUpTime;

            public void setDTimeStamp() {
                mDownTime = System.currentTimeMillis();
            }

            public void setUTimeStamp() {
                mUpTime = System.currentTimeMillis();
            }

            public void calculateRate() {
                mMills = (int) (mUpTime - mDownTime);
                if (mMills == 0) {
                    mRate = -1;
                } else {
                    mRate = (int) ((1000L * mCnt) / mMills);
                }
            }

            public void clean() {
                mDownTime = 0;
                mUpTime = 0;
                mCnt = 0;
                mPid = 0;
                mRate = 0;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.v("@M_" + TAG, "-->onTouchEvent");
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            int idx = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            int pid = event.getPointerId(idx);

            if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                    || actionCode == MotionEvent.ACTION_DOWN) {
                PointerData pt = new PointerData();
                pt.mPid = pid;
                pt.setDTimeStamp();
                mPtsStatus.put(pid, pt);
                mPointerNumDetected++;
            } else if (actionCode == MotionEvent.ACTION_POINTER_UP
                    || actionCode == MotionEvent.ACTION_UP) {
                PointerData pt = mPtsStatus.get(pid);
                pt.setUTimeStamp();
            }
            if (actionCode == MotionEvent.ACTION_POINTER_UP) {
                mPointerNumDetected--;
            } else if (actionCode == MotionEvent.ACTION_UP) {
                mPointerNumDetected = 0;
            }

            int pointCt = event.getPointerCount();
            Log.v("@M_" + TAG, "Pointer counts = " + pointCt + " mPtsStatus.size()= "
                    + mPtsStatus.size());

            for (int i = 0; i < pointCt; i++) {
                PointerData pt = mPtsStatus.get(event.getPointerId(i));
                pt.mCnt++;
                pt.mLastX = (int) event.getX(i);
                pt.mLastY = (int) event.getY(i);
            }

            invalidate();
            return true;
        }

        public void clear() {
            mPtsStatus.clear();
            invalidate();
        }

        Paint getPaint(int idx, int textsize) {
            Paint paint = new Paint();
            paint.setAntiAlias(false);
            if (idx < TsMultiTouch.RGB.length) {
                paint.setARGB(255, TsMultiTouch.RGB[idx][0],
                        TsMultiTouch.RGB[idx][1], TsMultiTouch.RGB[idx][2]);
            } else {
                paint.setARGB(255, 255, 255, 255);
            }
            paint.setTextSize(textsize);
            return paint;
        }
    }
}
