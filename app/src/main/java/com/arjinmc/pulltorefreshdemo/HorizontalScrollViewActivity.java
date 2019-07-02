package com.arjinmc.pulltorefreshdemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.pulltorefresh.PulltoRefreshHorizontalScrollView;
import com.arjinmc.pulltorefresh.listener.OnLoadMoreListener;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;

/**
 * Created by Eminem Lo on 2018/6/13.
 * email: arjinmc@hotmail.com
 */
public class HorizontalScrollViewActivity extends AppCompatActivity {

    private final String TAG = "ScrollViewActivity";

    private Handler mHandler = new Handler();
    private RefreshFinishRunnable mRefreshFinishRunnable;

    private PulltoRefreshHorizontalScrollView mPtrScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_scrollview);

        mRefreshFinishRunnable = new RefreshFinishRunnable();

        mPtrScrollView = findViewById(R.id.ptr_scrollview);
        mPtrScrollView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(mRefreshFinishRunnable, 2000);
            }
        });


        mPtrScrollView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mHandler.postDelayed(mRefreshFinishRunnable, 2000);
            }
        });
    }

    private class RefreshFinishRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "mPtrScrollView.onComplete");
            mPtrScrollView.onRefreshComplete();
        }
    }
}
