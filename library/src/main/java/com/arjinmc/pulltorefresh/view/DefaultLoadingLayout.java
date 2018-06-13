package com.arjinmc.pulltorefresh.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.arjinmc.pulltorefresh.R;

/**
 * Created by Eminem Lo on 2018/6/13.
 * email: arjinmc@hotmail.com
 */
public class DefaultLoadingLayout extends LoadingLayout {

    private ProgressBar mPbLoading;

    public DefaultLoadingLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DefaultLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_loading, null);
        mPbLoading = view.findViewById(R.id.pull_to_refresh_progress);

        addView(view, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onLoadingStart() {

        mPbLoading.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoadingEnd() {

        mPbLoading.setVisibility(View.GONE);

    }
}
