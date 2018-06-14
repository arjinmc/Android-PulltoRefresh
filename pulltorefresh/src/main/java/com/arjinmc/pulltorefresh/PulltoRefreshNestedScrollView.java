package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * PulltoRefreshNestedScrollView
 * Created by Eminem Lo on 2018/6/4.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshNestedScrollView extends PulltoRefreshBase<NestedScrollView> {

    public PulltoRefreshNestedScrollView(Context context) {
        super(context);
    }

    public PulltoRefreshNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PulltoRefreshNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected NestedScrollView createContentView(Context context, AttributeSet attrs) {
        NestedScrollView nestedScrollView = new NestedScrollView(context);
        nestedScrollView.setId(R.id.pull_to_refresh_nested_scrollview);
        return nestedScrollView;
    }

    @Override
    protected boolean isReadyToRefresh() {
        NestedScrollView nestedScrollView = getContentView();
        if (nestedScrollView == null) {
            return false;
        }
        return nestedScrollView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyToLoadMore() {
        NestedScrollView nestedScrollView = getContentView();
        if (nestedScrollView == null) {
            return false;
        }
        View scrollViewChild = nestedScrollView.getChildAt(0);
        if (null != scrollViewChild) {
            return nestedScrollView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    @Override
    protected int getContentViewOrientation() {
        return LinearLayout.VERTICAL;
    }
}
