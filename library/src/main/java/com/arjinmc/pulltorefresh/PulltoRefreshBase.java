package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
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

import static com.arjinmc.pulltorefresh.BuildConfig.DEBUG;

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
    private static final int STATUS_REFRESHING = 2;
    private static final int STATUS_LOAD_MORE_PULL = 3;
    private static final int STATUS_LOAD_MORE_LOADING = 4;

    private static final int SMOOTH_REWIND_DURATION_MS = 100;

    private PullLayout mHeadView;
    private PullLayout mFootView;
    private View mEmptyView;
    private RetryLayout mRetryView;
    private FrameLayout mContentWrapper;

    private T mContentView;
    private int mStatus = STATUS_STANDER;
    private boolean isRefreshEnable = true;
    private boolean isLoadMoreEnable = true;
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
        mFootView = new PullFootLayout(context, attrs);
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

    /**
     * set headView
     *
     * @param headView
     */
    public void setHeadView(PullHeadLayout headView) {
        if (mHeadView != null && mHeadView.getParent() != null) {
            removeView(mHeadView);
        }
        mHeadView = headView;
        updateUI();
    }

    /**
     * set footView
     *
     * @param footView
     */
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

    /**
     * set refresh function enable or not
     *
     * @param enable
     */
    public void setRefreshEnable(boolean enable) {
        isRefreshEnable = enable;
        updateUI();
    }

    public boolean isRefreshEnable() {
        return isRefreshEnable;
    }

    /**
     * set load more function enable or not
     *
     * @param enable
     */
    public void setLoadMoreEnable(boolean enable) {
        isLoadMoreEnable = enable;
        updateUI();
    }

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    /**
     * update ui for headView and footView
     */
    protected final void updateUI() {

        if (mHeadView != null) {
            if (mHeadView.getParent() != null) {
                removeView(mHeadView);
            }

            if (isRefreshEnable) {
                addView(mHeadView, 0, new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                mHeadView.measure(0, 0);
                mHeadViewHeight = mHeadView.getMeasuredHeight();
            } else {
                mHeadViewHeight = 0;
            }
        } else {
            mHeadViewHeight = 0;
        }

        if (mFootView != null) {
            if (mFootView.getParent() != null) {
                removeView(mFootView);
            }

            if (isLoadMoreEnable) {
                mFootView.measure(0, 0);
                mFootViewHeight = mFootView.getMeasuredHeight();

                addView(mFootView, new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, mFootViewHeight));
            } else {
                mFootViewHeight = 0;
            }
        } else {
            mFootViewHeight = 0;
        }

        refreshLoadingViewsSize();
    }

    /**
     * hide or show loading view like headView and footView
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

        setPadding(paddingL, paddingT, paddingR, paddingB);

    }

    /**
     * set onRefreshListener
     *
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * set onLoadMoreListener
     *
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * create content view
     *
     * @param context
     * @param attrs
     * @return
     */
    protected abstract T createContentView(Context context, AttributeSet attrs);

    /**
     * check if it on the top/left of the pullToRefreshView
     *
     * @return
     */
    protected abstract boolean isReadyToRefresh();

    /**
     * check if it on the bottom/right of the pullToRefreshView
     *
     * @return
     */
    protected abstract boolean isReadyToLoadMore();

    /**
     * get content view
     *
     * @return
     */
    public final T getContentView() {
        return mContentView;
    }

    /**
     * check if should show headView
     *
     * @return
     */
    protected boolean shouldShowHeadView() {
        if (isReadyToRefresh() && mStatus == STATUS_REFRESH_PULL) {
            return true;
        }
        return false;
    }

    /**
     * update headView
     */
    protected void updateHeadView() {
        //if headView is not shown
        if (mStatus != STATUS_REFRESH_PULL) {
            refreshLoadingViewsSize();
        }
        scrollTo(0, (int) mMove);
        int headViewMove = (int) Math.abs(mMove);
        if (headViewMove >= mHeadViewHeight) {
            //show release tips
            if (!mHeadViewShowReleaseTips) {
                mHeadViewShowReleaseTips = true;
                mHeadView.onSwitchTips(true);
            }
            //hide release tips
        } else if (mHeadViewShowReleaseTips) {
            mHeadViewShowReleaseTips = false;
            mHeadView.onSwitchTips(false);
        }

        //if need to move, pull the headView
        if (headViewMove != 0) {
            mHeadView.onPulling(mPullHeight, headViewMove);
        }
    }

    /**
     * update footView
     */
    protected void updateFootView() {

        scrollTo(0, (int) mMove);
        mFootView.requestLayout();
        int footViewMove = (int) Math.abs(mMove);
        if (footViewMove >= mFootViewHeight) {
            //show release tips
            if (!mFootViewShowReleaseTips) {
                mFootViewShowReleaseTips = true;
                mFootView.onSwitchTips(true);
            }
            //hide release tips
        } else if (mFootViewShowReleaseTips) {
            mFootViewShowReleaseTips = false;
            mFootView.onSwitchTips(false);
        }

        //if need to move, pull the footView
        if (footViewMove != 0) {
            mFootView.onPulling(mPullHeight, footViewMove);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("onTouchEvent", "status:" + mStatus);
        if (mStatus == STATUS_REFRESHING
                || (mStatus == STATUS_LOAD_MORE_LOADING)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "onTouchEvent:DOWN");
                if (isReadyToRefresh() || isReadyToLoadMore()) {
                    mPointDownY = event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "onTouchEvent:MOVE");
                if (!(mStatus == STATUS_REFRESH_PULL
                        || mStatus == STATUS_LOAD_MORE_PULL)) {
                    return false;
                }
                float alter = mPointDownY - event.getY();
                mMove += alter;
                mPointDownY = event.getY();
                if (mMove < 0) {
                    if (mStatus == STATUS_REFRESH_PULL) {
                        //control for the border
                        if (Math.abs(mMove) >= mPullHeight) {
                            mMove = -mPullHeight;
                            return false;
                        }
                        updateHeadView();
                    } else {
                        mStatus = STATUS_STANDER;
                        mMove = 0;
                        updateHeadView();
                        return false;
                    }
                } else if (mMove == 0f) {
                    mStatus = STATUS_STANDER;
                    mMove = 0;
                    return false;
                } else {
                    if (mStatus == STATUS_LOAD_MORE_PULL) {
                        //control for the border
                        if (Math.abs(mMove) >= mPullHeight) {
                            mMove = mPullHeight;
                            return false;
                        }
                        updateFootView();
                    } else {
                        mStatus = STATUS_STANDER;
                        mMove = 0;
                        updateFootView();
                        return false;
                    }
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
                } else if (mMove == 0f) {
                    mStatus = STATUS_STANDER;
                } else {
                    if (Math.abs(mMove) < mFootViewHeight) {
                        if (mFootViewRewindRunnable == null) {
                            mFootViewRewindRunnable = new FootViewRewindRunnable();
                        }
                        mFootView.post(mFootViewRewindRunnable);
                    } else {
                        if (mFootViewStartRefreshRunnable == null) {
                            mFootViewStartRefreshRunnable = new FootViewStartRefreshRunnable();
                        }
                        mFootView.post(mFootViewStartRefreshRunnable);
                    }
                }
                mPointDownY = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("onInterceptTouchEvent", "status:" + mStatus);

        if (mStatus == STATUS_REFRESHING
                || mStatus == STATUS_LOAD_MORE_LOADING) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "onInterceptTouchEvent:DOWN");
                if (isReadyToRefresh() || isReadyToLoadMore()) {
                    mPointDownY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "onInterceptTouchEvent:MOVE");
                float alter = mPointDownY - ev.getY();
                mMove += alter;
                mPointDownY = ev.getY();
                if (mMove < 0 && mStatus == STATUS_STANDER && isReadyToRefresh() && alter < 0 && isRefreshEnable) {
                    mStatus = STATUS_REFRESH_PULL;
                    return true;
                } else if (mMove == 0) {
                    mStatus = STATUS_STANDER;
                    return false;
                } else if (mMove > 0 && mStatus == STATUS_STANDER && isReadyToLoadMore() && alter > 0 && isLoadMoreEnable) {
                    mStatus = STATUS_LOAD_MORE_PULL;
                    return true;
                } else {
                    mMove -= alter;
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
        return false;
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (DEBUG) {
            Log.d(LOG_TAG, String.format("onSizeChanged. W: %d, H: %d", w, h));
        }

        super.onSizeChanged(w, h, oldw, oldh);

        refreshLoadingViewsSize();

        mContentWrapper.requestLayout();

        /**
         * As we're currently in a Layout Pass, we need to schedule another one
         * to layout any changes we've made here
         */
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
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
            if (mFootViewRewindRunnable == null) {
                mFootViewRewindRunnable = new FootViewRewindRunnable();
            }
            mFootView.post(mFootViewRewindRunnable);
        }
    }

    /**
     * Rewind headView when scroll height below headView top
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
                        * interpolator.getInterpolation(SMOOTH_REWIND_DURATION_MS / 1000f);
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
                        * interpolator.getInterpolation(SMOOTH_REWIND_DURATION_MS / 1000f);
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

    /**
     * Rewind footView when scroll height below footView bottom
     */
    private class FootViewRewindRunnable implements Runnable {

        private Interpolator interpolator;

        @Override
        public void run() {

            if (interpolator == null) {
                interpolator = new DecelerateInterpolator();
            }

            if (mMove >= 0) {
                float deltaY = Math.abs(mMove)
                        * interpolator.getInterpolation(SMOOTH_REWIND_DURATION_MS / 1000f);
                mMove -= deltaY;
                if (Math.round(deltaY) == 0) {
                    mMove = 0;
                }
            } else {
                mMove = 0;
            }
            updateFootView();
            if (mMove != 0) {
                ViewCompat.postOnAnimation(mFootView, this);
            } else {
                mStatus = STATUS_STANDER;
                mFootView.onReset();
            }
        }
    }

    /**
     * Start Refresh for footView
     */
    private class FootViewStartRefreshRunnable implements Runnable {

        private Interpolator interpolator;

        @Override
        public void run() {

            if (interpolator == null) {
                interpolator = new DecelerateInterpolator();
            }

            if (mMove >= mFootViewHeight) {
                float deltaY = (Math.abs(mMove) - mFootViewHeight)
                        * interpolator.getInterpolation(SMOOTH_REWIND_DURATION_MS / 1000f);
                mMove -= deltaY;
                if (Math.round(deltaY) == 0) {
                    mMove = mFootViewHeight;
                }
            } else {
                mMove = mFootViewHeight;
            }
            updateFootView();
            if (mMove != mFootViewHeight) {
                ViewCompat.postOnAnimation(mFootView, this);
            } else {
                if (mStatus == STATUS_LOAD_MORE_PULL) {
                    mStatus = STATUS_LOAD_MORE_LOADING;
                    mFootView.onLoading();
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        }
    }
}
