
 package com.mediatek.engineermode.sensor;

 import com.mediatek.engineermode.Elog;

 import android.content.Context;
 import android.view.View;
 import android.graphics.Canvas;
 import android.graphics.Paint;
 import android.graphics.Color;
 import android.util.AttributeSet;


 public class CollectionCurveView extends View {
    private static final int COUNT = 22;
    private static final int MAX_DIS = 200;
    private static final String TAG = "CollectionCurveView";

    //coordinate
    private static final int TOP_GAP = 20;
    private static final int BOTTOM_GAP = 30;
    private static final int LEFT_GAP = 1;
    private static final int LINE_LENGTH = 5;
    private static final int FONT_SIZE = 8;
    private static final int TEXT_HEIGHT = 10;
    private static final int Y_COUNT = 10;

    private static final float[] DATA = {0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f, 5f,
                        5.5f, 6f, 6.5f, 7f, 7.5f, 8f, 8.5f, 9f, 9.5f, 10f, 10.5f, 11f};
    private int[] mDis = new int[COUNT];

    private boolean mIsEmpty = true;

    public CollectionCurveView(Context context) {
        super(context);
    }
    public CollectionCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setDistance(int[] distance) {
        if (distance == null) {
            for (int i = 0; i < COUNT; i++) {
                mDis[i] = 0;
            }
            mIsEmpty = true;
        } else {
            mIsEmpty = false;
            System.arraycopy(distance, 0, mDis, 0, COUNT);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();

        int x1;
        int x2;
        float y1;
        float y2;

        int x;
        int y;

        Elog.v(TAG, "width = " + width);
        Elog.v(TAG, "height = " + height);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        // draw background as white
        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        // Axis - x
        canvas.drawLine(LEFT_GAP, height - BOTTOM_GAP, width - 1, height - BOTTOM_GAP, paint);
        for (int i = 1; i < COUNT + 1; i++) {
            x = LEFT_GAP + (width - LEFT_GAP) / COUNT * i;
            y1 = height - BOTTOM_GAP - LINE_LENGTH;
            y2 = height - BOTTOM_GAP;
            canvas.drawLine(x, y1, x, y2, paint);
        }
        paint.setTextSize(FONT_SIZE);
        for (int i = 1; i < COUNT + 1; i++) {
            x = LEFT_GAP + (width - LEFT_GAP) / COUNT * i - 2;
            y = height - BOTTOM_GAP + TEXT_HEIGHT;
            canvas.drawText(Float.toString(DATA[i - 1]), x, y, paint);

        }
        // Axis - y
        canvas.drawLine(LEFT_GAP, 0, LEFT_GAP, height - BOTTOM_GAP, paint);
        for (int i = 0; i < Y_COUNT; i++) {
            x1 = LEFT_GAP;
            x2 = width - 1; //LEFT_GAP + LINE_LENGTH;
            y = TOP_GAP + (height - BOTTOM_GAP - TOP_GAP) * i / Y_COUNT;
            canvas.drawLine(x1, y, x2, y, paint);
        }

        if (mIsEmpty) {
            return;
        }
        float max = 0;
        for (int i = 0; i < COUNT; i++) {
            if (max < mDis[i]) {
                max = mDis[i];
            }

        }
        float disString = 0;
        for (int i = 1; i < Y_COUNT + 1; i++) {
            x = LEFT_GAP + LINE_LENGTH;
            y = TOP_GAP + (height - BOTTOM_GAP - TOP_GAP) * (Y_COUNT - i) / Y_COUNT - 2;
            disString = max * i / Y_COUNT;

            canvas.drawText(Integer.toString((int) disString), x, y, paint);
        }

        //Curve
        paint.setColor(Color.BLUE);
        for (int i = 1; i < COUNT; i++) {
            x1 = width / COUNT * i;
            y1 = TOP_GAP + (height - BOTTOM_GAP - TOP_GAP) * (1 - mDis[i - 1] / max);

            x2 = (width / COUNT) * (i + 1);
            y2 = TOP_GAP + (height - BOTTOM_GAP - TOP_GAP) * (1 - mDis[i] / max);

            Elog.v(TAG, "x1 = " + x1 + "x2=" + x2);
            Elog.v(TAG, "y1=" + y1 + "y2=" + y2);
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }
}
