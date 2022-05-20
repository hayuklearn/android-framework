package com.af.widget.loadingable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;

import com.af.widget.loadingable.R;

/*
 * Copyright 2018 The Android Open Source Project
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
 *
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 *
 * =================================================================================================
 *      __
 *     / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *    / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *   / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 *  /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 * /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-25
 */
public class CircleImageView extends AppCompatImageView {

    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFFAFAFA;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;

    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;

    private Animation.AnimationListener mListener;
    private final int mShadowRadius;
    private int mBackgroundColor;

    CircleImageView(Context context) {

        super(context);

        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        // The style attribute is named `Loading` instead of `CircleImageView` because
        // CircleImageView is not part of the public api.
        @SuppressLint("CustomViewStyleable")
        TypedArray colorArray = getContext().obtainStyledAttributes(R.styleable.Loading);
        mBackgroundColor = colorArray.getColor(R.styleable.Loading_loadingLayoutProgressSpinnerBackgroundColor, DEFAULT_BACKGROUND_COLOR);
        colorArray.recycle();

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            circle = new ShapeDrawable(new OvalShadow(this, mShadowRadius));
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(mBackgroundColor);
        ViewCompat.setBackground(this, circle);
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ObsoleteSdkInt")
    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight() + mShadowRadius * 2);
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        Animation animation = getAnimation();
        if (null != mListener) {
            mListener.onAnimationStart(animation);
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        Animation animation = getAnimation();
        if (null != mListener) {
            mListener.onAnimationEnd(animation);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
            mBackgroundColor = color;
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    private static class OvalShadow extends OvalShape {

        private final Paint mShadowPaint;
        private final int mShadowRadius;
        private final CircleImageView mCircleImageView;

        OvalShadow(CircleImageView circleImageView, int shadowRadius) {
            super();
            mCircleImageView = circleImageView;
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int x = mCircleImageView.getWidth() / 2;
            final int y = mCircleImageView.getHeight() / 2;
            canvas.drawCircle(x, y, x, mShadowPaint);
            canvas.drawCircle(x, y, x - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mShadowPaint.setShader(new RadialGradient(
                    diameter >> 1,
                    diameter >> 1,
                    mShadowRadius,
                    new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    null,
                    Shader.TileMode.CLAMP));
        }
    }
}