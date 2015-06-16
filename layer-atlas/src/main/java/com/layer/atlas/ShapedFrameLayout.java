/*
 * Copyright (c) 2015 Layer. All rights reserved.
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
package com.layer.atlas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.layer.atlas.Atlas.Tools;


/**
 * @author Oleg Orlov
 * @since 8 May 2015
 */
public class ShapedFrameLayout extends FrameLayout {

    private static final String TAG = ShapedFrameLayout.class.getSimpleName();
    private static final boolean debug = true;
    
    private float[] corners = new float[] { 0, 0, 0, 0 };
    private boolean refreshShape = true;
    private Path shaper = new Path();
    
    private RectF pathRect = new RectF();
    
    public ShapedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareRendering();
    }

    public ShapedFrameLayout(Context context) {
        super(context);
        prepareRendering();
    }

    public ShapedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        prepareRendering();
    }

    public void setCornersDp(float[] cornerRadii) {
        System.arraycopy(cornerRadii, 0, this.corners, 0, 4);
        refreshShape = true;
    }
    
    private void prepareRendering() {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            if (debug)  Log.d(TAG, "setSoftwareRendering() software rendering...");
        }
    }
    
    public void setCornerRadiusDp(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.corners[0] = topLeft;
        this.corners[1] = topRight;
        this.corners[2] = bottomRight;
        this.corners[3] = bottomLeft;
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // clipPath according to shape
        
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        
        if (debug)  Log.d(TAG, "dispatchDraw() drawSize: " + width + "x" + height 
                + ", measuredSize: " + getMeasuredWidth() + "x" + getMeasuredHeight()
                + ", size: " + getWidth() + "x" + getHeight()
                + ", resetShape: " + refreshShape);
        
        if (refreshShape || true) {
            shaper.reset();
            pathRect.set(0, 0, width, height);
            float[] roundRectRadii = Atlas.Tools.getRoundRectRadii(corners, getResources().getDisplayMetrics());
            shaper.addRoundRect(pathRect, roundRectRadii,  Direction.CW);
            
            refreshShape = false;
        }
        
        int saved = canvas.save();
        canvas.clipPath(shaper);
        
        super.dispatchDraw(canvas);
        
        canvas.restoreToCount(saved);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshShape = true;
    }
    
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidthBefore  = getMeasuredWidth();
        int mHeightBefore = getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int mWidthAfter = getMeasuredWidth();
        int mHeightAfter = getMeasuredHeight();
        
        int measuredWidth = getMeasuredWidth();
        
//        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED && measuredWidth == 0) {
//            measuredWidth = (int) Tools.getPxFromDp(defaultWidthDp, getContext());
//        }
//        int measuredHeight = getMeasuredHeight();
//        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED && measuredHeight == 0) {
//            measuredHeight = (int) Tools.getPxFromDp(defaultHeightDp, getContext());
//        }
//
//        setMeasuredDimension(measuredWidth, measuredHeight);
        if (debug) Log.w(TAG, "onMeasure() before: " + mWidthBefore + "x" + mHeightBefore
                + ", spec: " + Tools.toStringSpec(widthMeasureSpec) + "x" + Tools.toStringSpec(heightMeasureSpec)
                + ", after: " + mWidthAfter + "x" + mHeightAfter
//                + ", final: " + getMeasuredWidth() + "x" + getMeasuredHeight()
                );
    }


}
