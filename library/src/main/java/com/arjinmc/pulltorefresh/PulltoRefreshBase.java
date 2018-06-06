package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.arjinmc.pulltorefresh.listener.OnLoadMoreListener;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;
import com.arjinmc.pulltorefresh.view.PullFootLayout;
import com.arjinmc.pulltorefresh.view.PullHeadLayout;
import com.arjinmc.pulltorefresh.view.PullLayout;
import com.arjinmc.pulltorefresh.view.RetryLayout;

/**
 * PulltoRefreshView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public abstract class PulltoRefreshBase<T extends View> extends LinearLayout {

    private static final String LOG_TAG = "PulltoRefreshBase";

    //status of PulltoRefreshBase
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

    public static final int SMOOTH_SCROLL_DURATION_MS = 200;
    public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;

    private PullLayout mHeadView;
    private PullLayout mFootView;
    private View mEmptyView;
    private RetryLayout mRetryView;
    private FrameLayout mContentWrapper;

    private T mContentView;
    private int mStatus = STATUS_STANDER;
    private int mMode = MODE_BOTH;
    private int mOrientation = LinearLayout.VERTICAL;
    private int mPullHeight = 300;

    private int mHeadViewHeight;
    private int mFootViewHeight;
    private boolean mHeadViewShowReleaseTips;
    private boolean mFootViewShowReleaseTips;

    private float mPointDownY;
    private float mMove;

    // Runnable for headView to rewind for pulling to refresh
    private Runnable mHeadViewRewindRunnable;
    // Runnable for headView to start refresh
    private Runnable mHeadViewStartRefreshRunnable;
    //Runnable for footView to rewind for pulling to load more
    private Runnable mFootViewRewindRunnable;
    //Runnable for footView to start load more
    private Runnable mFootViewStartRefreshRunnable;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    public PulltoRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PulltoRefreshBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PulltoRefreshBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setOrientation(mOrientation);

        mContentView = createContentView(context, attrs);
        addContentView(context, mContentView);

        //init head and foot
        mHeadView = new PullHeadLayout(context, attrs);
//        mFootView = new PullFootLayout(context,attrs);
        updateUI();
    }

    private void addContentView(Context context, T contentView) {
        mContentWrapper = new FrameLayout(context);
        mContentWrapper.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mContentWrapper.addView(contentView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(mContentWrapper, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

    }

    public void setHeadView(PullHeadLayout headView) {
        if (mHeadView != null && mHeadView.getParent() != null) {
            removeView(mHeadView);
        }
        mHeadView = headView;
        updateUI();
    }

    public void setFootView(PullFootLayout footView) {
        if (mFootView != null && mFootView.getParent() != null) {
            removeView(mFootView);
        }
        mFootView = footView;
        updateUI();
    }

    /**
     * set the height should be pull
     *
     * @param height
     */
    public void setPullHeight(int height) {
        if (height <= 0 && (mPullHeight > mHeadViewHeight + 20 || mPullHeight > mFootViewHeight + 20)) {
            try {
                throw new IllegalAccessException(
                        "Pull height must be above zero and bigger than headViewHeight/footViewHeight");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }
        mPullHeight = height;
    }

    protected final void updateUI() {

        if (mHeadView != null && (mMode == MODE_REFRESH || mMode == MODE_BOTH)) {
            if (mHeadView.getParent() != null) {
                removeView(mHeadView);
            }
            addView(mHeadView, 0, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        }

        if (mFootView != null && (mMode == MODE_LOAD_MORE || mMode == MODE_BOTH)) {
            if (mFootView.getParent() != null) {
                removeView(mFootView);
            }
            addView(mHeadView, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }

        mHeadView.measure(0, 0);
        mHeadViewHeight = mHeadView.getMeasuredHeight();

//        mFootView.measure(0, 0);
//        mFootViewHeight = mFootView.getHeight();

        refreshLoadingViewsSize();
    }

    /**
     * hide or show loading
     */
    protected final void refreshLoadingViewsSize() {

        int paddingL = getPaddingLeft();
        int paddingR = getPaddingRight();

        int paddingT = getPaddingTop();
        int paddingB = getPaddingBottom();

        if (shouldShowHeadView()) {
            paddingT = 0;
        } else {
            paddingT = -mHeadViewHeight;
        }

        if (shouldShowFootView()) {
            paddingB = 0;
        } else {
            paddingB = -mFootViewHeight;
        }
        setPadding(paddingL, paddingT, paddingR, paddingB);

    }

    public void resetViews() {
        removeAllViews();
        updateUI();
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

    protected abstract T createContentView(Context context, AttributeSet attrs);

    protected abstract boolean isReadyToRefresh();

    protected abstract boolean isReadyToLoadMore();

    public final T getContentView() {
        return mContentView;
    }

//    @Override
//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        Log.d(LOG_TAG, "addView: " + child.getClass().getSimpleName());
//
//        final T refreshableView = getContentView();
//
//        if (refreshableView instanceof ViewGroup) {
//            ((ViewGroup) refreshableView).addView(child, index, params);
//        } else {
//            throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
//        }
//    }

    protected boolean shouldShowHeadView() {
        if (isContentOnTop() && mStatus == STATUS_REFRESH_PULL) {
            return true;
        }
        return false;
    }

    protected boolean shouldShowFootView() {
        if (isContentOnBottom() && mStatus == STATUS_LOAD_MORE_PULL) {
            return true;
        }
        return false;
    }

    /**
     * check content is on the top
     *
     * @return
     */
    protected boolean isContentOnTop() {
        if (mContentWrapper.getTop() == 0) {
            return true;
        }
        return false;
    }

    /**
     * check content is on the bottom
     *
     * @return
     */
    protected boolean isContentOnBottom() {
        if (mContentWrapper.getBottom() == 0) {
            return true;
        }
        return false;
    }

    /**
     * update headView
     */
    protected void updateHeadView() {
        if (mStatus != STATUS_REFRESH_PULL) {
            refreshLoadingViewsSize();
        }
        scrollTo(0, (int) mMove);
        int headViewMove = (int) Math.abs(mMove);
        if (headViewMove >= mHeadViewHeight) {
            if (!mHeadViewShowReleaseTips) {
                mHeadViewShowReleaseTips = true;
                mHeadView.onSwitchTips(true);
            }
        } else if (mHeadViewShowReleaseTips) {
            mHeadViewShowReleaseTips = false;
            mHeadView.onSwitchTips(false);
        }
        mHeadView.onPulling(mPullHeight, headViewMove);

    }

    /**
     * update footView
     */
    protected void updateFootView() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("onTouchEvent", "status:" + mStatus);
        if (mStatus == STATUS_REFRESHING) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "onTouchEvent:DOWN");
                if (isContentOnTop()) {
                    mPointDownY = event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "onTouchEvent:MOVE");
                if (mStatus != STATUS_REFRESH_PULL) {
                    return false;
                }
                float alter = mPointDownY - event.getY();
                mMove += alter;
                mPointDownY = event.getY();
                if (mMove < 0) {
                    //control for the border
                    if (Math.abs(mMove) >= mPullHeight) {
                        mMove = -mPullHeight;
                        return false;
                    }
                    updateHeadView();
                } else if (mMove == 0f) {
                    mStatus = STATUS_STANDER;
                } else {
                    updateFootView();
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOG_TAG, "onTouchEvent:UP");
                if (mMove < 0) {
                    if (Math.abs(mMove) < mHeadViewHeight) {
                        if (mHeadViewRewindRunnable == null) {
                            mHeadViewRewindRunnable = new HeadViewRewindRunnable();
                        }
                        mHeadView.post(mHeadViewRewindRunnable);
                    } else {
                        if (mHeadViewStartRefreshRunnable == null) {
                            mHeadViewStartRefreshRunnable = new HeadViewStartRefreshRunnable();
                        }
                        mHeadView.post(mHeadViewStartRefreshRunnable);
                    }
                } else {

                }
                mPointDownY = 0;
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("onInterceptTouchEvent", "status:" + mStatus);

        if (mStatus == STATUS_REFRESHING) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "onInterceptTouchEvent:DOWN");
                if (isContentOnTop()) {
                    mPointDownY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "onInterceptTouchEvent:MOVE");
                float alter = mPointDownY - ev.getY();
                mMove += alter;
                mPointDownY = ev.getY();
                if (mMove < 0) {
                    if (mStatus == STATUS_STANDER && isReadyToRefresh()) {
                        mStatus = STATUS_REFRESH_PULL;
                    }
                }
                if (mStatus == STATUS_REFRESH_PULL
                        || mStatus == STATUS_LOAD_MORE_PULL) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOG_TAG, "onInterceptTouchEvent:UP");
                break;
        }
//        return super.onInterceptTouchEvent(ev);
        return false;
    }

    /**
     * reset the status
     */
    public final void onRefreshComplete() {
        if (mMove < 0) {
            if (mHeadViewRewindRunnable == null) {
                mHeadViewRewindRunnable = new HeadViewRewindRunnable();
            }
            mHeadView.post(mHeadViewRewindRunnable);
        } else {
//            mFootView.post(mFootViewRewindRunnable);
        }
    }

    /**
     * Rewind headView when scroll height below headView head
     */
    private class HeadViewRewindRunnable implements Runnable {

        private Interpolator interpolator;

        @Override
        public void run() {

            if (interpolator == null) {
                interpolator = new DecelerateInterpolator();
            }

            if (mMove <= 0) {
                float deltaY = Math.abs(mMove)
                        * interpolator.getInterpolation(SMOOTH_SCROLL_DURATION_MS / 1000f);
                mMove += deltaY;
                if (Math.round(deltaY) == 0) {
                    mMove = 0;
                }
            } else {
                mMove = 0;
            }
            updateHeadView();
            if (mMove != 0) {
                ViewCompat.postOnAnimation(mHeadView, this);
            } else {
                mStatus = STATUS_STANDER;
                mHeadView.onReset();
            }
        }
    }

    /**
     * Start Refresh for headView
     */
    private class HeadViewStartRefreshRunnable implements Runnable {

        private Interpolator interpolator;

        @Override
        public void run() {

            if (interpolator == null) {
                interpolator = new DecelerateInterpolator();
            }

            if (!(mMove >= 0)) {
                float deltaY = (Math.abs(mMove) - mHeadViewHeight)
                        * interpolator.getInterpolation(SMOOTH_SCROLL_DURATION_MS / 1000f);
                mMove += deltaY;
                if (Math.round(deltaY) == 0) {
                    mMove = -mHeadViewHeight;
                }
            } else {
                mMove = -mHeadViewHeight;
            }
            updateHeadView();
            if (mMove != -mHeadViewHeight) {
                ViewCompat.postOnAnimation(mHeadView, this);
            } else {
                if (mStatus == STATUS_REFRESH_PULL) {
                    mStatus = STATUS_REFRESHING;
                    mHeadView.onLoading();
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }
            }
        }
    }
}
