

package com.mediatek.engineermode.camera;

/*
 * Copyright (C) 2009 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mediatek.engineermode.R;

/**
 * A layout which handles the preview aspect ratio and the position of the
 * gripper.
 */
public class PreviewFrameLayout extends ViewGroup {

    private static final int MIN_HORIZONTAL_MARGIN = 10; // 10dp
    private static final double MGIC_NRM_4 = 4.0;
    private static final double MGIC_NRM_3 = 3.0;
    private static final double MGIC_NRM_5 = .5;

    private double mAspectRatio = MGIC_NRM_4 / MGIC_NRM_3;
    private FrameLayout mFrame;
    private OnSizeChangedListener mSizeListener;
    private final DisplayMetrics mMetrics = new DisplayMetrics();

    /** A callback to be invoked when the preview frame's size changes. */
    public interface OnSizeChangedListener {
        /**
         * On camera preview's size changed
         */
        void onSizeChanged();
    }

    /**
     * preview frame layout
     *
     * @param context
     *            : parentactivity's content
     * @param attrs
     *            : attribute set
     */
    public PreviewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(
            mMetrics);
    }

    /**
     * set camera preview changed lis
     *
     * @param listener
     *            : listener
     */
    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mSizeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        mFrame = (FrameLayout) findViewById(R.id.frame);
        if (mFrame == null) {
            throw new IllegalStateException(
                "must provide child with id as \"frame\"");
        }
    }

    /**
     * set the aspect ratio
     *
     * @param ratio
     *            : ratio value
     */
    public void setAspectRatio(double ratio) {
        if (ratio <= 0.0) {
            throw new IllegalArgumentException();
        }
        if (mAspectRatio != ratio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Try to layout the "frame" in the center of the area, and put
        // "gripper" just to the left of it. If there is no enough space for
        // the gripper, the "frame" will be moved a little right so that
        // they won't overlap with each other.

        int frameWidth = getWidth();
        int frameHeight = getHeight();

        final FrameLayout frameLayout = mFrame;

        final int horizontalPadding =
            Math.max(frameLayout.getPaddingLeft()
                + frameLayout.getPaddingRight(),
                (int) (MIN_HORIZONTAL_MARGIN * mMetrics.density));
        final int verticalPadding =
            frameLayout.getPaddingBottom() + frameLayout.getPaddingTop();

        // Ignore the vertical paddings, so that we won't draw the frame on the
        // top and bottom sides
        int previewHeight = frameHeight;
        int previewWidth = frameWidth - horizontalPadding;

        // resize frame and preview for aspect ratio
        if (previewWidth > previewHeight * mAspectRatio) {
            previewWidth = (int) (previewHeight * mAspectRatio + MGIC_NRM_5);
        } else {
            previewHeight = (int) (previewWidth / mAspectRatio + MGIC_NRM_5);
        }

        frameWidth = previewWidth + horizontalPadding;
        frameHeight = previewHeight + verticalPadding;

        final int hSpace = ((r - l) - frameWidth) / 2;
        final int vSpace = ((b - t) - frameHeight) / 2;
        mFrame.measure(MeasureSpec.makeMeasureSpec(frameWidth,
            MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(frameHeight,
            MeasureSpec.EXACTLY));
        mFrame.layout(l + hSpace, t + vSpace, r - hSpace, b - vSpace);
        if (mSizeListener != null) {
            mSizeListener.onSizeChanged();
        }
    }
}
