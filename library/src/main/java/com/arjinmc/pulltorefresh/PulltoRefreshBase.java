package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.arjinmc.pulltorefresh.listener.OnLoadMoreListener;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;
import com.arjinmc.pulltorefresh.view.LoadingFootLayout;
import com.arjinmc.pulltorefresh.view.LoadingHeadLayout;
import com.arjinmc.pulltorefresh.view.RetryLayout;

/**
 * PulltoRefreshView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshBase<T extends View> extends LinearLayout {

    //status
    private static final int STATUS_STANDER = 0;
    private static final int STATUS_REFRESH_PULL = 1;
    private static final int STATUS_REFRESH_RELEASE = 2;
    private static final int STATUS_REFRESHING = 3;
    private static final int STATUS_LOAD_MORE_PULL = 4;
    private static final int STATUS_LOAD_MORE_RELEASE = 5;
    private static final int STATUS_LOAD_MORE_LOADING = 6;

    public static final int MODE_BOTH = 0;
    public static final int MODE_REFRESH = 1;
    public static final int MODE_LOAD_MORE = 2;

    @IntDef({MODE_BOTH, MODE_REFRESH, MODE_LOAD_MORE})
    @interface ModeType {
    }


    public static final int DIRECTION_VERTICAL = 0;
    public static final int DIRECTION_HORIZONATL = 1;

    @IntDef({DIRECTION_VERTICAL, DIRECTION_HORIZONATL})
    @interface DirectionType {
    }

    private LoadingHeadLayout mHeadView;
    private LoadingFootLayout mFootView;
    private View mEmptyView;
    private RetryLayout mRetryView;

    private T mContentView;
    private int mMode = MODE_BOTH;
    private int mDirection = DIRECTION_VERTICAL;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    public PulltoRefreshBase(Context context) {
        super(context);
        init();
    }

    public PulltoRefreshBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PulltoRefreshBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        setOrientation(LinearLayout.VERTICAL);
    }

    public void setHeadView(LoadingHeadLayout headView) {
        mHeadView = headView;
    }

    public void setFootView(LoadingFootLayout footView) {
        mFootView = footView;
    }

    public void resetViews() {
        removeAllViews();
        addView(mHeadView, new LayoutParams(LayoutParams.MATCH_PARENT, mHeadView.getMeasuredHeight()));
        addView(mContentView);
        addView(mFootView);
    }

    public void setMode(@ModeType int mode) {
        mMode = mode;
    }

    public void setDirection(@DirectionType int direction) {
        mDirection = direction;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
