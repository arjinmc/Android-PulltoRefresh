package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * PulltoRefreshScrollView
 * Created by Eminem Lo on 2018/6/4.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshScrollView extends PulltoRefreshBase<ScrollView> {

    public PulltoRefreshScrollView(Context context) {
        super(context);
    }

    public PulltoRefreshScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PulltoRefreshScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected ScrollView createContentView(Context context, AttributeSet attrs) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setId(R.id.pull_to_refresh_scrollview);
        return scrollView;
    }

    @Override
    protected boolean isReadyToRefresh() {
        ScrollView scrollView = getContentView();
        if (scrollView == null) {
            return false;
        }
        return scrollView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyToLoadMore() {
        ScrollView scrollView = getContentView();
        if (scrollView == null) {
            return false;
        }
        View scrollViewChild = scrollView.getChildAt(0);
        if (null != scrollViewChild) {
            return scrollView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    @Override
    protected int getContentViewOrientation() {
        return LinearLayout.VERTICAL;
    }
}
