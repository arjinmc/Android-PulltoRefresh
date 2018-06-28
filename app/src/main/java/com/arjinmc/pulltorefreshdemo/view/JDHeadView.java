package com.arjinmc.pulltorefreshdemo.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.arjinmc.pulltorefresh.view.PullLayout;
import com.arjinmc.pulltorefreshdemo.R;

/**
 * Created by Eminem Lo on 2018/6/11.
 * email: arjinmc@hotmail.com
 */
public class JDHeadView extends PullLayout {

    private ImageView mIvTwinkle, mIvFigure;
    private AnimationDrawable mFigureAnim;
    private Animation mTwinkleAnim;

    public JDHeadView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public JDHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public JDHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JDHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_jd_header, null);
        mIvFigure = view.findViewById(R.id.iv_figure);
        mIvTwinkle = view.findViewById(R.id.iv_twinkle);
        mFigureAnim = (AnimationDrawable) mIvFigure.getBackground();
        mTwinkleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_twinkle);
        addView(view, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onPulling(int pullMaxHeight, int currentHeight) {

        if (!mFigureAnim.isRunning()) {
            mFigureAnim.start();
        }
    }

    @Override
    public void onReset() {

        if (mFigureAnim.isRunning()) {
            mFigureAnim.stop();
        }
        mIvTwinkle.clearAnimation();
        mIvTwinkle.setVisibility(GONE);
    }

    @Override
    public void onLoading() {

        mIvTwinkle.setVisibility(VISIBLE);
        mIvTwinkle.startAnimation(mTwinkleAnim);
        if (!mFigureAnim.isRunning()) {
            mFigureAnim.start();
        }
    }

    @Override
    public void onSwitchTips(boolean showReleaseTips) {

    }
}
