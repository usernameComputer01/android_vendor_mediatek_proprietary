

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediatek.engineermode.camera;

import android.hardware.Camera.Parameters;

import java.util.List;

// A class that handles everything about focus in still picture mode.
// This also handles the metering area because it is the same as focus area.
public class FocusManager {
    // private static final String TAG = "FocusManager";

    private boolean mFocusAreaSupported;
    private boolean mLockAeAwbNeeded;
    private boolean mAeAwbLock;

    /**
     * This has to be initialized before initialize().
     * 
     * @param parameters
     *            : camera init para
     */
    public void initializeParameters(Parameters parameters) {
        Parameters mParameters = parameters;
        mFocusAreaSupported =
            (mParameters.getMaxNumFocusAreas() > 0 && isSupported(
                Parameters.FOCUS_MODE_AUTO, mParameters
                    .getSupportedFocusModes()));
        mLockAeAwbNeeded =
            (mParameters.isAutoExposureLockSupported() || mParameters
                .isAutoWhiteBalanceLockSupported());
    }

    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    /**
     * set AE lock.
     * 
     * @param lock
     *            : whether lock the AE
     */
    public void setAeAwbLock(boolean lock) {
        mAeAwbLock = lock;
    }

    /**
     * get AE lock value.
     * @return ture: ae is locked, false: ae is not locked
     */
    public boolean getAeAwbLock() {
        return mAeAwbLock;
    }

    /**
     * get whether focus area support.
     * @return true: focus area support, false: not
     */
    public boolean ismFocusAreaSupported() {
        return mFocusAreaSupported;
    }

    /**
     * set focus area support value.
     * 
     * @param focusAreaSupported
     *            : whether support the focus area.
     */
    public void setmFocusAreaSupported(boolean focusAreaSupported) {
        mFocusAreaSupported = focusAreaSupported;
    }

    /**
     * get whether lock ae aw.
     * @return ture: SWB locked, false: not locked
     */
    public boolean ismLockAeAwbNeeded() {
        return mLockAeAwbNeeded;
    }

    /**
     * set lock ae aw value.
     * 
     * @param lockAeAwbNeeded
     *            : whether lock ae aw.
     */
    public void setmLockAeAwbNeeded(boolean lockAeAwbNeeded) {
        mLockAeAwbNeeded = lockAeAwbNeeded;
    }
}
