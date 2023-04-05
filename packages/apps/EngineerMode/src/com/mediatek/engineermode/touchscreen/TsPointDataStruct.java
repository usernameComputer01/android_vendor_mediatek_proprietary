
package com.mediatek.engineermode.touchscreen;

public class TsPointDataStruct {
    public long mTimeStamp;
    public int mPid;
    public int mAction = 0;
    public int mCoordinateX = 0;
    public int mCoordinateY = 0;
    public float mPressure = 0.0f;
    public float mVelocityX = 0.0f;
    public float mVelocityY = 0.0f;
    public float mFatSize = 0.01f; // (0~1)
    
    
    public TsPointDataStruct() {
        mTimeStamp = System.currentTimeMillis();
    }

    public void setTimeStamp() {
        mTimeStamp = System.currentTimeMillis();
    }

    public long getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(long timeStamp) {
        this.mTimeStamp = timeStamp;
    }

    public int getmPid() {
        return mPid;
    }

    public void setmPid(int pid) {
        this.mPid = pid;
    }

    public int getmAction() {
        return mAction;
    }

    public void setmAction(int action) {
        this.mAction = action;
    }

    public int getmCoordinateX() {
        return mCoordinateX;
    }

    public void setmCoordinateX(int coordinateX) {
        this.mCoordinateX = coordinateX;
    }

    public int getmCoordinateY() {
        return mCoordinateY;
    }

    public void setmCoordinateY(int coordinateY) {
        this.mCoordinateY = coordinateY;
    }

    public float getmPressure() {
        return mPressure;
    }

    public void setmPressure(float pressure) {
        this.mPressure = pressure;
    }

    public float getmVelocityX() {
        return mVelocityX;
    }

    public void setmVelocityX(float velocityX) {
        this.mVelocityX = velocityX;
    }

    public float getmVelocityY() {
        return mVelocityY;
    }

    public void setmVelocityY(float velocityY) {
        this.mVelocityY = velocityY;
    }

    public float getmFatSize() {
        return mFatSize;
    }

    public void setmFatSize(float fatSize) {
        this.mFatSize = fatSize;
    }
}
