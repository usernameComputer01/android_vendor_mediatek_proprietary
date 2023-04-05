
package com.mediatek.ygps;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;


public class SatelLocationView extends SatelliteBaseView {

    private static class Point {
        float x;
        float y;
        Point() {}
        Point(float x1, float y1) {
            x = x1;
            y = y1;
        }
    }

    private static final String TAG = "SatelLocationView";
    private static final int MARGIN = 12;
    private static final float PERCENT_75 = 0.75f;
    private static final float RIGHT_ANGLE = 90.0f;
    private static final float STRAIGHT_ANGLE = 180.0f;

    private Paint mGraphicPaint = null;
    private Paint mTextPaint = null;
    private Paint mBgPaint = null;
    private Paint mCircleBorderPaint = null;
    private Bitmap mUnfixedBmp = null;
    private Bitmap mUsedInFixBmp = null;
    private Bitmap mUnusedInFixBmp = null;

    public SatelLocationView(Context context) {
        this(context, null, 0);
    }

    public SatelLocationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatelLocationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView();
    }

    private void onCreateView() {
        mGraphicPaint = new Paint();
        mGraphicPaint.setStyle(Style.STROKE);
        mGraphicPaint.setAntiAlias(true);
        mGraphicPaint.setColor(getResources().getColor(R.color.grid));
        mGraphicPaint.setStrokeWidth(1.0f);

        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(12.0f);
        mTextPaint.setColor(getResources().getColor(R.color.skyview_text_color));

        mBgPaint = new Paint();
        mBgPaint.setColor(getResources().getColor(R.color.skyview_background));

        mCircleBorderPaint = new Paint();
        mCircleBorderPaint.setStyle(Style.STROKE);
        mCircleBorderPaint.setAntiAlias(true);
        mCircleBorderPaint.setStrokeWidth(2.0f);

        mUnfixedBmp = getBmpFromRes(R.drawable.satred);
        mUnusedInFixBmp = getBmpFromRes(R.drawable.satyellow);
        mUsedInFixBmp = getBmpFromRes(R.drawable.satgreen);
    }

    private Bitmap getBmpFromRes(int rid) {
        BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(rid);
        return bmpDraw.getBitmap();
    }

    private Point computeViewPostion(SatelliteInfo si, Point origin, float baseRadius) {
        Point targPt = new Point();
        float project = baseRadius * (RIGHT_ANGLE - si.elevation) / RIGHT_ANGLE;
        float alpha = si.azimuth;
        float radian = (float) (alpha * Math.PI / STRAIGHT_ANGLE);
        targPt.x = origin.x + (float) (project * Math.sin(radian));
        targPt.y = origin.y - (float) (project * Math.cos(radian));
        return targPt;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        Point origin = new Point(viewWidth / 2, viewHeight / 2);
        int maxRadius = (int) Math.floor(origin.y - MARGIN);

        canvas.drawPaint(mBgPaint);
        float[] radiusArr = {maxRadius >> 2, maxRadius >> 1, maxRadius * PERCENT_75, maxRadius};
        for (int i = 0; i < radiusArr.length; i++) {
            canvas.drawCircle(origin.x, origin.y, radiusArr[i], mGraphicPaint);
        }
        canvas.drawLine(origin.x - maxRadius, origin.y, origin.x - (maxRadius >> 2), origin.y, mGraphicPaint);
        canvas.drawLine(origin.x + maxRadius, origin.y, origin.x + (maxRadius >> 2), origin.y, mGraphicPaint);
        canvas.drawLine(origin.x, origin.y - maxRadius, origin.x, origin.y - (maxRadius >> 2), mGraphicPaint);
        canvas.drawLine(origin.x, origin.y + maxRadius, origin.x, origin.y + (maxRadius >> 2), mGraphicPaint);

        SatelliteInfoManager simgr = getSatelliteInfoManager();
        if (simgr != null) {
            List<SatelliteInfo> siList = simgr.getSatelInfoList();
            for (SatelliteInfo si : siList) {
                //Xlog.d(TAG, "handle " + si.toString());
                if (si.prn <= 0 || si.elevation <= 0
                        || si.azimuth < 0) {
                    Log.d("@M_" + TAG, "invalid parameter; discard drawing; prn:" + si.prn
                            + " elevation:" + si.elevation + " azimuth:" + si.azimuth);
                    continue;
                }
                Point targPt = computeViewPostion(si, origin, maxRadius);
                Bitmap drawnBmp = null;
                if (!simgr.isUsedInFix(SatelliteInfoManager.PRN_ANY) || si.snr <= 0) {
                    drawnBmp = mUnfixedBmp;
                } else {
                    if (simgr.isUsedInFix(si.prn)) {
                        drawnBmp = mUsedInFixBmp;
                    } else {
                        drawnBmp = mUnusedInFixBmp;
                    }
                }
                int bmpHeight = drawnBmp.getHeight();
                float targX = targPt.x - bmpHeight / 2.0f;
                float targY = targPt.y - bmpHeight / 2.0f;
                //Xlog.d(TAG, "targX:" + targX + " targY:" + targY);
                canvas.drawBitmap(drawnBmp, targX, targY, mGraphicPaint);
                mCircleBorderPaint.setColor(si.color);
                canvas.drawCircle(targPt.x, targPt.y, bmpHeight / 2.0f - 1.5f, mCircleBorderPaint);
                canvas.drawText(String.valueOf(si.prn), targX, targY, mTextPaint);
            }

        }
    }


}
