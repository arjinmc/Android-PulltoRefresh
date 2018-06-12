package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Eminem Lo on 2018/6/4.
 * email: arjinmc@hotmail.com
 */
public class PulltoRefreshRecyclerView extends PulltoRefreshBase<RecyclerView> {

    public PulltoRefreshRecyclerView(Context context) {
        super(context);
    }

    public PulltoRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PulltoRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PulltoRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected RecyclerView createContentView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView = new RecyclerView(context, attrs);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setId(R.id.pull_to_refresh_recyclerview);
        return recyclerView;
    }

    @Override
    protected boolean isReadyToRefresh() {

        if (getContentViewOrientation() == -1) {
            return false;
        }
        if (getOrientation() != getContentViewOrientation()) {
            try {
                throw new IllegalAccessException("The orientation of PulltoRefreshView must be the same as RecyclerView");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        RecyclerView recyclerView = getContentView();
        if (recyclerView == null) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            if (layoutManager.getLayoutDirection() == LinearLayoutManager.VERTICAL) {
                if (!recyclerView.canScrollVertically(-1)) {
                    return true;
                }
            } else if (((LinearLayoutManager) layoutManager)
                    .findFirstCompletelyVisibleItemPosition() == 0) {
//                Log.d("isReadyToRefresh", "true");
                return true;
            }
        } else if (layoutManager instanceof GridLayoutManager) {

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

        }
        return false;
    }

    @Override
    protected boolean isReadyToLoadMore() {

        if (getContentViewOrientation() == -1) {
            return false;
        }
        if (getOrientation() != getContentViewOrientation()) {
            try {
                throw new IllegalAccessException("The orientation of PulltoRefreshView must be the same as RecyclerView");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        RecyclerView recyclerView = getContentView();
        if (recyclerView == null) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition()
                    == recyclerView.getAdapter().getItemCount() - 1) {
//                Log.d("isReadyToLoadMore", "true");
                return true;
            }

        } else if (layoutManager instanceof GridLayoutManager) {

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

        }
        return false;
    }

    @Override
    protected int getContentViewOrientation() {

        RecyclerView recyclerView = getContentView();
        if (recyclerView == null) {
            return -1;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager
                && ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return LinearLayout.HORIZONTAL;
        } else if (layoutManager instanceof GridLayoutManager
                && ((GridLayoutManager) layoutManager).getOrientation() == GridLayoutManager.HORIZONTAL) {
            return LinearLayout.HORIZONTAL;
        } else if (layoutManager instanceof StaggeredGridLayoutManager
                && ((StaggeredGridLayoutManager) layoutManager).getOrientation() == StaggeredGridLayoutManager.HORIZONTAL) {
            return LinearLayout.HORIZONTAL;
        }
        return LinearLayout.VERTICAL;
    }

}
