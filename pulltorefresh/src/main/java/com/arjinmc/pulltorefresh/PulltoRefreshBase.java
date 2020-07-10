package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.pulltorefresh.listener.OnLoadMoreListener;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;
import com.arjinmc.pulltorefresh.view.DefaultVerticalPullFootLayout;
import com.arjinmc.pulltorefresh.view.DefaultVerticalPullHeadLayout;
import com.arjinmc.pulltorefresh.view.LoadingLayout;
import com.arjinmc.pulltorefresh.view.PullLayout;

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
    private static final int STATUS_LOADING = 5;
    private static final int STATUS_EMPTY = 6;
    private static final int STATUS_ERROR = 7;

    //action to show/hide head or foot view
    private static final int ACTION_SHOW_VIEW = 0;
    private static final int ACTION_HIDE_VIEW = 1;

    private static final int SMOOTH_REWIND_DURATION_MS = 100;

    private PullLayout mHeadView;
    private PullLayout mFootView;
    private LoadingLayout mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private FrameLayout mContentWrapper;

    private T mContentView;
    private int mStatus = STATUS_STANDER;
    /**
     * return back to the status before pull to refresh
     */
    private int mStoreStatus = mStatus;
    private boolean isRefreshEnable = true;
    private boolean isLoadMoreEnable = true;
    private int mOrientation = LinearLayout.VERTICAL;
    private int mPullHeight = 300;
    private int mOriginalPaddingL, mOriginalPaddingT, mOriginalPaddingR, mOriginalPaddingB;
    /**
     * mark for is first init
     */
    private boolean isFirstInit;
    /**
     * mark for do next status when onRefresh complete
     */
    private int mDoNextStatus = -1;
    private int mDoChangeHeadView = -1;
    private int mDoChangeFootView = -1;

    private int mHeadViewHeight;
    private int mFootViewHeight;
    private boolean mHeadViewShowReleaseTips;
    private boolean mFootViewShowReleaseTips;

    private float mPointDown;
    private float mPointDownX;
    private float mMove;

    // Runnable for headView to rewind for pulling to refresh
    private Runnable mHeadViewRewindRunnable;
    // Runnable for headView to start refresh
    private Runnable mHeadViewStartRefreshRunnable;
    //Runnable for footView to rewind for pulling to load more
    private Runnable mFootViewRewindRunnable;
    //Runnable for footView to start load more
    private Runnable mFootViewStartRefreshRunnable;
    //Mark for animation is playing or not
    private boolean mIsAnimationPlaying;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    //flag if empty / error view can pull this view
    private boolean isEmptyCanPull, isErrorCanPull;

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
        mHeadView = new DefaultVerticalPullHeadLayout(context, attrs);
        mFootView = new DefaultVerticalPullFootLayout(context, attrs);
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
    public void setHeadView(PullLayout headView) {
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
    public void setFootView(PullLayout footView) {
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
                        "Pull height must be above zero and bigger 20 than headViewHeight/footViewHeight");
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
        if (mStatus == STATUS_REFRESHING || mStatus == STATUS_LOAD_MORE_LOADING) {
            mDoChangeHeadView = enable ? ACTION_SHOW_VIEW : ACTION_HIDE_VIEW;
        } else {
            isRefreshEnable = enable;
            updateUI();
        }
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
        if (mStatus == STATUS_REFRESHING || mStatus == STATUS_LOAD_MORE_LOADING) {
            mDoChangeFootView = enable ? ACTION_SHOW_VIEW : ACTION_HIDE_VIEW;
        } else {
            isLoadMoreEnable = enable;
            updateUI();
        }
    }

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    /**
     * set loading view
     *
     * @param loadingView
     */
    public void setLoadingView(LoadingLayout loadingView) {
        if (mLoadingView != null) {
            mContentWrapper.removeView(mLoadingView);
        }
        mLoadingView = loadingView;
        addStatusView(mLoadingView);
    }

    /**
     * set empty
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        if (mEmptyView != null) {
            mContentWrapper.removeView(mEmptyView);
        }
        mEmptyView = emptyView;
        addStatusView(mEmptyView);
    }

    /**
     * set retryView
     *
     * @param errorView
     */
    public void setErrorView(View errorView) {
        if (mErrorView != null) {
            mContentWrapper.removeView(mErrorView);
        }
        mErrorView = errorView;
        addStatusView(mErrorView);
    }

    /**
     * set when empty view shown that this view can be pull or not
     *
     * @param canPull
     */
    public void setEmptyCanPull(boolean canPull) {
        isEmptyCanPull = canPull;
    }

    /**
     * set when error view shown that this view can be pull or not
     *
     * @param canPull
     */
    public void setErrorCanPull(boolean canPull) {
        isErrorCanPull = canPull;
    }

    /**
     * check the status if can be refreshed
     *
     * @return
     */
    private boolean isCanRefreshPullStatus() {
        if (isEmptyCanPull && mStatus == STATUS_EMPTY) {
            return true;
        } else if (isErrorCanPull && mStatus == STATUS_ERROR) {
            return true;
        } else if (isReadyToRefresh() && mStatus == STATUS_STANDER) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * add status view for content wrapper
     *
     * @param view
     */
    private final void addStatusView(View view) {

        mContentWrapper.addView(view, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        view.setVisibility(View.GONE);
    }

    /**
     * show statusView in content wrapper
     *
     * @param view
     */
    private final void showStatusView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hide statusView in content wrapper
     *
     * @param view
     */
    private final void hideStatusView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * show loading status view
     */
    public final void toLoading() {
        checkSwitchStatus(STATUS_LOADING);
    }

    /**
     * show empty status view
     */
    public final void toEmpty() {
        checkSwitchStatus(STATUS_EMPTY);
    }

    /**
     * show empty status view
     */
    public final void toError() {
        checkSwitchStatus(STATUS_ERROR);
    }

    /**
     * show content view / standard status
     */
    public final void toContent() {
        checkSwitchStatus(STATUS_STANDER);
    }

    /**
     * check status if need to switch
     *
     * @param status
     */
    private void checkSwitchStatus(int status) {
        if (status == mStatus) {
            return;
        }
        if (mStatus == STATUS_REFRESH_PULL
                || mStatus == STATUS_LOAD_MORE_PULL) {
            return;
        }
        mStoreStatus = status;

        if (mStatus == STATUS_REFRESHING
                || mStatus == STATUS_LOAD_MORE_LOADING) {
            if (mDoNextStatus == status) {
                return;
            } else {
                mDoNextStatus = status;
            }
            return;
        }
        switchStatus(status);
    }

    /**
     * switch status
     *
     * @param status
     */
    private void switchStatus(int status) {
        hideStatusView(mContentView);
        hideStatusView(mLoadingView);
        hideStatusView(mEmptyView);
        hideStatusView(mErrorView);
        switch (status) {
            case STATUS_LOADING:
                showStatusView(mLoadingView);
                if (mLoadingView != null) {
                    mLoadingView.onLoadingStart();
                }
                break;
            case STATUS_EMPTY:
                showStatusView(mEmptyView);
                if (mLoadingView != null) {
                    mLoadingView.onLoadingEnd();
                }
                break;
            case STATUS_ERROR:
                showStatusView(mErrorView);
                if (mLoadingView != null) {
                    mLoadingView.onLoadingEnd();
                }
                break;
            case STATUS_STANDER:
            default:
                showStatusView(mContentView);
                if (mLoadingView != null) {
                    mLoadingView.onLoadingEnd();
                }
                break;
        }
        mStatus = status;
        mDoNextStatus = -1;

    }

    /**
     * switch head and foot view for the mark when on refreshing or loading more status
     */
    private void switchHeadAndFootView() {
        if (mDoChangeHeadView != -1) {
            switch (mDoChangeHeadView) {
                case ACTION_SHOW_VIEW:
                    setRefreshEnable(true);
                    break;
                case ACTION_HIDE_VIEW:
                    setRefreshEnable(false);
                    break;
            }
            mDoChangeHeadView = -1;
        }

        if (mDoChangeFootView != -1) {
            switch (mDoChangeFootView) {
                case ACTION_SHOW_VIEW:
                    setLoadMoreEnable(true);
                    break;
                case ACTION_HIDE_VIEW:
                    setLoadMoreEnable(false);
                    break;
            }
            mDoChangeFootView = -1;
        }
    }

    /**
     * onRefresh
     */
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * onLoadMore
     */
    public void onLoadMore() {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
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
                if (getOrientation() == VERTICAL) {
                    addView(mHeadView, 0, new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    mHeadView.measure(0, 0);
                    mHeadViewHeight = mHeadView.getMeasuredHeight();
                } else {
                    addView(mHeadView, 0, new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                    mHeadView.measure(0, 0);
                    mHeadViewHeight = mHeadView.getMeasuredWidth();
                }

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

                if (getOrientation() == VERTICAL) {
                    mFootView.measure(0, 0);
                    mFootViewHeight = mFootView.getMeasuredHeight();

                    addView(mFootView, new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, mFootViewHeight));
                } else {
                    mFootView.measure(0, 0);
                    mFootViewHeight = mFootView.getMeasuredWidth();

                    addView(mFootView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT
                            , mFootViewHeight));
                }
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

        if (!isFirstInit) {
            mOriginalPaddingL = getPaddingLeft();
            mOriginalPaddingR = getPaddingRight();
            mOriginalPaddingT = getPaddingTop();
            mOriginalPaddingB = getPaddingBottom();
            isFirstInit = true;
        }
        int paddingL = mOriginalPaddingL;
        int paddingR = mOriginalPaddingR;

        int paddingT = mOriginalPaddingT;
        int paddingB = mOriginalPaddingB;

        if (getOrientation() == VERTICAL) {
            if (shouldShowHeadView()) {
                paddingT = 0;
            } else {
                paddingT = -mHeadViewHeight;
            }

        } else {
            if (shouldShowHeadView()) {
                paddingL = 0;
            } else {
                paddingL = -mHeadViewHeight;
            }
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
     * get the orientation of Content View (return with orientation of LinearLayout
     *
     * @return
     */
    protected abstract int getContentViewOrientation();

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
        if (isCanRefreshPullStatus() && mStatus == STATUS_REFRESH_PULL) {
            return true;
        }
        return false;
    }

    /**
     * check if should show footView
     *
     * @return
     */
    protected boolean shouldShowFootView() {
        if (isReadyToLoadMore() && mStatus == STATUS_LOAD_MORE_PULL) {
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

        if (getOrientation() == VERTICAL) {
            scrollTo(0, (int) mMove);
        } else {
            scrollTo((int) mMove, 0);
        }
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

        mHeadView.onPulling(mPullHeight, headViewMove);
    }

    /**
     * update footView
     */
    protected void updateFootView() {

        if (getOrientation() == VERTICAL) {
            scrollTo(0, (int) mMove);
        } else {
            scrollTo((int) mMove, 0);
        }
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

        mFootView.onPulling(mPullHeight, footViewMove);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mStatus == STATUS_REFRESHING
                || (mStatus == STATUS_LOAD_MORE_LOADING)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isCanRefreshPullStatus() || isReadyToLoadMore()) {
                    mPointDownX = event.getX();
                    if (getOrientation() == VERTICAL) {
                        mPointDown = event.getY();
                    } else {
                        mPointDown = event.getX();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (!(mStatus == STATUS_REFRESH_PULL || mStatus == STATUS_LOAD_MORE_PULL)) {
                    return false;
                }
                float alter = 0;
                if (getOrientation() == VERTICAL) {
                    alter = mPointDown - event.getY();
                } else {
                    alter = mPointDown - event.getX();
                }
                mMove += alter;
                if (getOrientation() == VERTICAL) {
                    mPointDown = event.getY();
                } else {
                    mPointDown = event.getX();
                }
                if (mMove < 0) {
                    if (mStatus == STATUS_REFRESH_PULL) {
                        //control for the border
                        if (Math.abs(mMove) >= mPullHeight) {
                            mMove = -mPullHeight;
                            return false;
                        }
                        updateHeadView();
                    } else {
                        mMove = 0;
                        updateHeadView();
                        return false;
                    }
                } else if (mMove == 0f) {
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
                        mMove = 0;
                        updateFootView();
                        return false;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:

                if (mMove < 0) {
                    if (Math.abs(mMove) < mHeadViewHeight) {
                        if (mHeadViewRewindRunnable == null) {
                            mHeadViewRewindRunnable = new HeadViewRewindRunnable();
                        }
                        if (!mIsAnimationPlaying) {
                            mHeadView.post(mHeadViewRewindRunnable);
                        }
                    } else {
                        if (mHeadViewStartRefreshRunnable == null) {
                            mHeadViewStartRefreshRunnable = new HeadViewStartRefreshRunnable();
                        }
                        if (!mIsAnimationPlaying) {
                            mHeadView.post(mHeadViewStartRefreshRunnable);
                        }
                    }
                } else if (mMove > 0) {
                    if (Math.abs(mMove) < mFootViewHeight) {
                        if (mFootViewRewindRunnable == null) {
                            mFootViewRewindRunnable = new FootViewRewindRunnable();
                        }
                        if (!mIsAnimationPlaying) {
                            mFootView.post(mFootViewRewindRunnable);
                        }
                    } else {
                        if (mFootViewStartRefreshRunnable == null) {
                            mFootViewStartRefreshRunnable = new FootViewStartRefreshRunnable();
                        }
                        if (!mIsAnimationPlaying) {
                            mFootView.post(mFootViewStartRefreshRunnable);
                        }
                    }
                } else {
                    //when mMove = 0,back to store status
                    mStatus = mStoreStatus;
                }
                mPointDown = 0;
                mPointDownX = 0;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mStatus == STATUS_REFRESHING
                || mStatus == STATUS_LOAD_MORE_LOADING) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (isCanRefreshPullStatus() || isReadyToLoadMore()) {
                    if ((isEmptyCanPull && mStoreStatus == STATUS_EMPTY)
                            || (isErrorCanPull && mStoreStatus == STATUS_ERROR)) {
                        mStatus = STATUS_REFRESH_PULL;
                    }
                    mPointDownX = ev.getX();
                    if (getOrientation() == VERTICAL) {
                        mPointDown = ev.getY();
                    } else {
                        mPointDown = ev.getX();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mPointDownX != ev.getX() && Math.abs(mPointDownX - ev.getX()) <= 8) {
                    return false;
                }
                float alter = 0;
                if (getOrientation() == VERTICAL) {
                    alter = mPointDown - ev.getY();
                } else {
                    alter = mPointDown - ev.getX();
                }
                mMove += alter;

                if (mMove == 0) {
                    mStatus = mStoreStatus;
                    return false;
                }

                if (getOrientation() == VERTICAL) {
                    mPointDown = ev.getY();
                } else {
                    mPointDown = ev.getX();
                }
                if (mMove < 0 && isCanRefreshPullStatus()
                        && alter < 0 && isRefreshEnable) {
                    mStatus = STATUS_REFRESH_PULL;
                    return true;
                } else if (mMove > 0 && mStatus == STATUS_STANDER
                        && isReadyToLoadMore() && alter > 0 && isLoadMoreEnable) {
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
                mPointDownX = 0;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        final T contentView = getContentView();

        if (contentView instanceof RecyclerView && getChildCount() >= 3) {
            throw new UnsupportedOperationException("PulltoRefreshRecyclerView cannot add child in XML!");
        } else if (contentView instanceof ScrollView
                || contentView instanceof HorizontalScrollView
                || contentView instanceof NestedScrollView) {
            if (getChildCount() < 3) {
                super.addView(child, index, params);
            } else if (getChildCount() == 3) {
                ((ViewGroup) contentView).addView(child, 0, params);
            } else {
                throw new UnsupportedOperationException("PulltoRefreshScrollView should have only one child!");
            }
        } else {
            super.addView(child, index, params);
        }
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
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

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        updateUI();
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
        } else if (mMove > 0) {
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
                mIsAnimationPlaying = true;
            } else {
                mIsAnimationPlaying = false;
                mStatus = mStoreStatus;
                mHeadView.onReset();
                if (mDoNextStatus != -1) {
                    switchStatus(mDoNextStatus);
                }
                switchHeadAndFootView();
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

            if (mStatus == STATUS_REFRESH_PULL) {
                mStatus = STATUS_REFRESHING;
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
                mIsAnimationPlaying = true;
            } else {
                mIsAnimationPlaying = false;
                mHeadView.onLoading();
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
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
                mIsAnimationPlaying = true;
            } else {
                mIsAnimationPlaying = false;
                mStatus = mStoreStatus;
                mFootView.onReset();
                if (mDoNextStatus != -1) {
                    switchStatus(mDoNextStatus);
                }
                switchHeadAndFootView();
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

            if (mStatus == STATUS_LOAD_MORE_PULL) {
                mStatus = STATUS_LOAD_MORE_LOADING;
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
                mIsAnimationPlaying = true;
            } else {

                mIsAnimationPlaying = false;
                mFootView.onLoading();
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }

            }
        }
    }
}
