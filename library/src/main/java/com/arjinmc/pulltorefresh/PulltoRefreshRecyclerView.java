package com.arjinmc.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

        RecyclerView recyclerView = getContentView();
//        RecyclerView.LayoutManager layoutManager  = recyclerView.getLayoutManager();
//        layoutManager.getLayoutDirection() == LinearLayoutManager
        if (!recyclerView.canScrollVertically(-1)) {
            Log.e("isReadyToRefresh", "true");
            return true;
        }
        return false;
    }

    @Override
    protected boolean isReadyToLoadMore() {
        RecyclerView recyclerView = getContentView();
        if (!recyclerView.canScrollVertically(1)) {
            Log.e("isReadyToLoadMore", "true");
            return true;
        }
        return false;
    }


}
