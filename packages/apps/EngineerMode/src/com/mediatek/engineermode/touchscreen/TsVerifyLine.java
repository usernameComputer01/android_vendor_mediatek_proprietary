
package com.mediatek.engineermode.touchscreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


import java.util.Vector;

public class TsVerifyLine extends Activity implements View.OnTouchListener {
    public DiversityCanvas mDiversityCanvas;
    public boolean mRun = false;
    public double mDiversity = 0;
    public Vector<Point> mPts1 = null;
    public Vector<Point> mInput = new Vector<Point>();
    public int mLineIndex = 0;

    public static final int CALCULATE_ID = Menu.FIRST;
    public static final int NEXTLINE_ID = Menu.FIRST + 1;

    private static final String TAG = "EM/TouchScreen/VL";

    private int mZoom = 1;
    private int mRectWidth;
    private int mRectHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mRectWidth = dm.widthPixels;
        mRectHeight = dm.heightPixels;
        if ((480 == mRectWidth && 800 == mRectHeight)
                || (800 == mRectWidth && 480 == mRectHeight)) {
            mZoom = 2;
        }

        // mRun=true;
        mPts1 = readPoints(0);
        mLineIndex++;
        mDiversityCanvas = new DiversityCanvas((Context) this);
        setContentView(mDiversityCanvas);
        mDiversityCanvas.setOnTouchListener(this);
        Log.i("@M_" + TAG, "Oncreate");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALCULATE_ID, 0, "Calculate");
        menu.add(0, NEXTLINE_ID, 1, "NextLine");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aMenuItem) {
        switch (aMenuItem.getItemId()) {
        case CALCULATE_ID:
            calculateDiversity();
            mDiversityCanvas.requestUpdate();
            break;
        case NEXTLINE_ID:
            mInput.clear();

            mPts1 = readPoints(mLineIndex);
            mLineIndex++;

            if (4 == mLineIndex) {
                mLineIndex = 0;
            }
            mDiversity = 0.0;
            mDiversityCanvas.requestUpdate();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(aMenuItem);
    }

    public void calculateDiversity() {
        int i;
        Point cp = new Point(0, 0);
        if (mInput.isEmpty()) {
//            if (mInput.size() == 0) {
            return;
        }
        double error = 0.0;
        float ratio = (float) mRectHeight / (float) mRectWidth;

        switch (mLineIndex - 1) {
        case 0:
            for (i = 0; i < mInput.size(); i++) {
                cp = mInput.get(i);
                error += Math.abs(cp.x - mRectWidth / 2);
            }
            break;
        case 1:
            for (i = 0; i < mInput.size(); i++) {
                cp = mInput.get(i);
                error += Math.abs(cp.y - mRectHeight / 2);
            }
            break;
        case 2:
            for (i = 0; i < mInput.size(); i++) {
                cp = mInput.get(i);
                error += Math.abs(ratio * cp.x - cp.y)
                        / Math.sqrt(1 + ratio * ratio);
            }
            break;
        case -1:
            for (i = 0; i < mInput.size(); i++) {
                cp = mInput.get(i);
                error += Math.abs(ratio * cp.x + cp.y - mRectHeight)
                        / Math.sqrt(1 + ratio * ratio);
            }
            break;
        default:
            break;
        }

        mDiversity = error / mInput.size();
    }

    public boolean onTouch(View v, MotionEvent e) {

        if (MotionEvent.ACTION_DOWN == e.getAction()
                || MotionEvent.ACTION_MOVE == e.getAction()) {
            if (v.equals(mDiversityCanvas)) {
//                if (v == mDiversityCanvas) {
                mInput.add(new Point((int) e.getX(), (int) e.getY()));
                mDiversityCanvas.requestUpdate();
            }
        }

        return true;
    }

    public Vector<Point> readPoints(int lineIndex) {
        int x;
        int y;
        int i;
        Vector<Point> v = new Vector<Point>();
        Point p;
        float ratio = (float) mRectHeight / (float) mRectWidth;

        switch (mLineIndex) {
        case 0:
            for (i = 0; i < mRectHeight; i++) {
                x = mRectWidth / 2;
                y = i;
                p = new Point(x, y);
                v.add(p);
            }
            break;
        case 1:
            for (i = 0; i < mRectWidth; i++) {
                x = i;
                y = mRectHeight / 2;
                p = new Point(x, y);
                v.add(p);
            }
            break;
        case 2:
            for (i = 0; i < mRectWidth; i++) {
                x = i;
                y = (int) (i * ratio);
                p = new Point(x, y);
                v.add(p);
            }
            break;
        case 3:
            for (i = 0; i < mRectWidth; i++) {
                x = mRectWidth - i;
                y = (int) (i * ratio);
                p = new Point(x, y);
                v.add(p);
            }
            break;
        default:
            break;
        }
        return v;
    }

    class DiversityCanvas extends SurfaceView implements SurfaceHolder.Callback {
        DiversityThread mThread = null;

        public DiversityCanvas(Context context) {
            super(context);
            SurfaceHolder holder = getHolder();
            holder.addCallback(this);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            Log.v("@M_" + TAG, "surfaceChanged");
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.v("@M_" + TAG, "surfaceCreated");
            mRun = true;

            mThread = new DiversityThread(holder, null);
            mThread.start();
            requestUpdate();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mRun = false;
            Log.v("@M_" + TAG, "surfaceDestroyed");
            mThread.quit();
        }

        void requestUpdate() {
            mThread.mHandler.sendEmptyMessage(DiversityThread.MSG_REQUEST_UPDATE);
        }

        class DiversityThread extends HandlerThread {

            private static final int MSG_REQUEST_UPDATE = 10;
            private SurfaceHolder mSurfaceHolder = null;
            private Paint mLinePaint = null;
            private Paint mTextPaint = null;
            private Paint mRectPaint = null;
            private Rect mRect = null;
            private Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                    case MSG_REQUEST_UPDATE:
                        doUpdate();
                        break;
                    default:
                        Log.d("@M_" + TAG, "Unknown msg:" + msg.what);
                        break;
                    }
                }
            };

            private void doUpdate() {
                Log.d("@M_" + TAG, "doUpdate()");
                Canvas c = null;
                c = mSurfaceHolder.lockCanvas(null);
                if (c != null) {
                    doDraw(c);
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }

            public DiversityThread(SurfaceHolder s, Context c) {
                super("TouchScreen.verifyLine");
                mSurfaceHolder = s;
                mLinePaint = new Paint();
                mLinePaint.setAntiAlias(true);
                mTextPaint = new Paint();
                mTextPaint.setAntiAlias(true);
                mTextPaint.setTextSize(9.0f * mZoom);
                mTextPaint.setARGB(255, 0, 0, 0);
                mRect = new Rect(0, 0, mRectWidth, mRectHeight);
                mRectPaint = new Paint();
                mRectPaint.setARGB(255, 255, 255, 255);
            }

            private void doDraw(Canvas canvas) {
                int i;
                Point p1;
                Point p2;
                canvas.drawRect(mRect, mRectPaint);
                mLinePaint.setARGB(255, 0, 0, 255);
                try {
                    for (i = 0; i < mPts1.size() - 1; i++) {
                        p1 = mPts1.get(i);
                        p2 = mPts1.get(i + 1);
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.v("@M_" + TAG, "mPts1 ArrayIndexOutOfBoundsException: " + e.getMessage());
                    return;
                }

                mLinePaint.setARGB(255, 255, 0, 0);
                try {
                    for (i = 0; i < mInput.size() - 1; i++) {
                        p1 = mInput.get(i);
                        p2 = mInput.get(i + 1);
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.v("@M_" + TAG, "mInput ArrayIndexOutOfBoundsException: "
                            + e.getMessage());
                    return;
                }
                canvas.drawText("Diversity : " + Double.toString(mDiversity),
                        20 * mZoom, mRectHeight - 10 * mZoom, mTextPaint);
            }
        }
    }
}
