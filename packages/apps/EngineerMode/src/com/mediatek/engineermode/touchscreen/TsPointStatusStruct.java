
package com.mediatek.engineermode.touchscreen;

public class TsPointStatusStruct {
    public boolean mDown = false;
    public boolean mNewLine = false;
    
    public boolean ismDown() {
        return mDown;
    }
    public void setmDown(boolean down) {
        this.mDown = down;
    }
    public boolean ismNewLine() {
        return mNewLine;
    }
    public void setmNewLine(boolean newLine) {
        this.mNewLine = newLine;
    }
    
}
