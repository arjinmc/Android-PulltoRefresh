package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.pulltorefresh.view.DefaultHorizontalPullFootLayout;
import com.arjinmc.pulltorefresh.view.DefaultHorizontalPullHeadLayout;

/**
 * PulltoRefreshHorizontalScrollView
 * Created by Eminem Lo on 2018/6/13.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshHorizontalScrollView extends PulltoRefreshBase<HorizontalScrollView> {

    public PulltoRefreshHorizontalScrollView(Context context) {
        super(context);
        initView(context, null);
    }

    public PulltoRefreshHorizontalScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PulltoRefreshHorizontalScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setHeadView(new DefaultHorizontalPullHeadLayout(context));
        setFootView(new DefaultHorizontalPullFootLayout(context));
    }

    @Override
    protected HorizontalScrollView createContentView(Context context, AttributeSet attrs) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setId(R.id.pull_to_refresh_horizontal_scrollview);
        return horizontalScrollView;
    }

    @Override
    protected boolean isReadyToRefresh() {
        HorizontalScrollView horizontalScrollView = getContentView();
        if (horizontalScrollView == null) {
            return false;
        }
        return horizontalScrollView.getScrollX() == 0;
    }

    @Override
    protected boolean isReadyToLoadMore() {
        HorizontalScrollView horizontalScrollView = getContentView();
        if (horizontalScrollView == null) {
            return false;
        }
        View scrollViewChild = horizontalScrollView.getChildAt(0);
        if (null != scrollViewChild) {
            return horizontalScrollView.getScrollX() >= (scrollViewChild.getWidth() - getWidth());
        }
        return false;
    }

    @Override
    protected int getContentViewOrientation() {
        return LinearLayout.HORIZONTAL;
    }
}
