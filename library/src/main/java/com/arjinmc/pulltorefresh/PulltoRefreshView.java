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
import com.arjinmc.pulltorefresh.loadingview.LoadingFootView;
import com.arjinmc.pulltorefresh.loadingview.LoadingHeadView;

/**
 * PulltoRefreshView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshView extends LinearLayout {

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

    private LoadingHeadView mHeadView;
    private LoadingFootView mFootView;
    private View mContentView;
    private int mMode = MODE_BOTH;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    public PulltoRefreshView(Context context) {
        super(context);
        init();
    }

    public PulltoRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PulltoRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        setOrientation(LinearLayout.VERTICAL);
    }

    public void setHeadView(LoadingHeadView headView) {
        mHeadView = headView;
    }

    public void setFootView(LoadingFootView footView) {
        mFootView = footView;
    }

    public void setMode(@ModeType int mode) {
        mMode = mode;
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
