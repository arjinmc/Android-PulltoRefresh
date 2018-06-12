package com.arjinmc.pulltorefresh.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.arjinmc.pulltorefresh.R;

/**
 * Created by Eminem Lo on 2018/6/12.
 * email: arjinmc@hotmail.com
 */
public class DefaultHorizontalPullHeadLayout extends PullLayout {

    private ImageView mIvArrow;
    private ProgressBar mPbLoading;
    private int mHeight;

    public DefaultHorizontalPullHeadLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DefaultHorizontalPullHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultHorizontalPullHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultHorizontalPullHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_header_horizontal, null);
        mIvArrow = view.findViewById(R.id.pull_to_refresh_image);
        mPbLoading = view.findViewById(R.id.pull_to_refresh_progress);
        mIvArrow.setRotation(-90);
        addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        requestLayout();
        view.measure(0, 0);
        mHeight = view.getMeasuredWidth();
    }

    @Override
    public void onPulling(int pullMaxHeight, int currentHeight) {

        if (currentHeight < mHeight) {
            mIvArrow.setRotation(-90 - 180 * currentHeight / mHeight);
        } else {
            mIvArrow.setRotation(-270);
        }

    }

    @Override
    public void onReset() {

        mIvArrow.setRotation(-90);
        mIvArrow.setVisibility(View.VISIBLE);
        mPbLoading.setVisibility(View.GONE);

    }

    @Override
    public void onLoading() {

        mIvArrow.setVisibility(View.GONE);
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSwitchTips(boolean showReleaseTips) {

    }
}
