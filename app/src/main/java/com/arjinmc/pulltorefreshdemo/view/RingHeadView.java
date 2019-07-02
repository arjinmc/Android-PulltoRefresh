package com.arjinmc.pulltorefreshdemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.pulltorefresh.view.PullLayout;

/**
 * RingHeadView
 * Created by Eminem Lo on 2018/6/25.
 * email: arjinmc@hotmail.com
 */
public class RingHeadView extends PullLayout {

    private RingView mIvRing;
    private ValueAnimator mLoadingAnimation;

    public RingHeadView(@NonNull Context context) {
        super(context);
        init();
    }

    public RingHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RingHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mIvRing = new RingView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 20;
        layoutParams.bottomMargin = 20;
        addView(mIvRing, layoutParams);
        mIvRing.requestLayout();
    }

    @Override
    public void onPulling(int pullMaxHeight, int currentHeight) {

        int edge = (int) (pullMaxHeight / 3f * 2);
        int radius = -90 + (int) ((currentHeight / (float) edge) * 450);
        if (currentHeight >= edge) {
            radius = 360;
        }
        mIvRing.setRadius(radius);
    }

    @Override
    public void onReset() {

        if (mLoadingAnimation != null && mLoadingAnimation.isRunning()) {
            mLoadingAnimation.cancel();
        }
        mIvRing.setRotation(0);
        mIvRing.setRadius(-90);
    }

    @Override
    public void onLoading() {
        if (mLoadingAnimation == null) {
            mLoadingAnimation = ValueAnimator.ofFloat(0, 1f);
            mLoadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
            mLoadingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mIvRing.setRotation(((float) animation.getAnimatedValue()) * 360);
                }
            });
        }
        if (!mLoadingAnimation.isRunning()) {
            mLoadingAnimation.start();
        }

    }

    @Override
    public void onSwitchTips(boolean showReleaseTips) {

    }

    private class RingView extends View {

        private final int mThickness = 5;
        //radius of the circle
        private final int mWidth = 100;
        private final int mColors = Color.RED;
        private Paint mPaint;
        //the angle of the circle
        private float mRadius;
        private RectF mRectF;

        public RingView(Context context) {
            super(context);
            init();
        }

        public RingView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        private void init() {

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(mThickness);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mColors);
            mRadius = -90;

            mRectF = new RectF(mThickness, mThickness, mWidth, mWidth);
        }

        private void setRadius(float radius) {

            mRadius = radius;
            postInvalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawArc(mRectF, -90, mRadius, false, mPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(mWidth + mThickness, mWidth + mThickness);
        }
    }

}
