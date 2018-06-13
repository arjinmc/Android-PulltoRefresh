package com.arjinmc.pulltorefresh.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.arjinmc.pulltorefresh.R;

/**
 * PullFootLayout
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public class DefaultVerticalPullFootLayout extends PullLayout {

    private ImageView mIvLoading;
    private TextView mTvTips;
    private ValueAnimator mRotateAnimation;

    public DefaultVerticalPullFootLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DefaultVerticalPullFootLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultVerticalPullFootLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultVerticalPullFootLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_header_vertical, null);
        mIvLoading = view.findViewById(R.id.pull_to_refresh_loading);
        mTvTips = view.findViewById(R.id.pull_to_refresh_tips);
        mTvTips.setText(R.string.pull_to_refresh_pull_to_load_more);
        addView(view, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onPulling(int pullMaxHeight, int currentHeight) {
        mIvLoading.setRotation(currentHeight % 360);
    }

    @Override
    public void onLoading() {

        mTvTips.setText(R.string.pull_to_refresh_loading);
        startRotateAnimation();
    }

    @Override
    public void onSwitchTips(boolean showReleaseTips) {
        if (showReleaseTips) {
            mTvTips.setText(R.string.pull_to_refresh_release_to_load_more);
        } else {
            mTvTips.setText(R.string.pull_to_refresh_pull_to_load_more);
        }
    }

    @Override
    public void onReset() {

        stopRotateAnimation();
        mIvLoading.setRotation(0);
        mTvTips.setText(R.string.pull_to_refresh_pull_to_load_more);
    }

    public void startRotateAnimation() {

        if (mRotateAnimation == null) {
            mRotateAnimation = ValueAnimator.ofInt(0, 360).setDuration(1000);

            mRotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
            mRotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = Float.valueOf((int) animation.getAnimatedValue());
                    if (mIvLoading.getRotation() != currentValue) {
                        mIvLoading.setRotation(currentValue);
                    }
                }
            });
        }
        if (!mRotateAnimation.isRunning()) {
            mRotateAnimation.start();
        }
    }

    public void stopRotateAnimation() {

        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
            mRotateAnimation = null;
        }
    }
}
