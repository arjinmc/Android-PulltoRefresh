package com.arjinmc.pulltorefreshdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.pulltorefresh.listener.OnLoadMoreListener;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;
import com.arjinmc.pulltorefreshdemo.view.MyPullListView;
import com.arjinmc.recyclerviewdecoration.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eminem Lo on 2018/6/11.
 * email: arjinmc@hotmail.com
 */
public class CustomPulltoRefreshActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "RVDefaultActivity";

    private MyPullListView mPtrRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<String> mDataList;
    private Button mBtnLoading, mBtnData, mBtnEmpty, mBtnError;
    private LinearLayout mLayoutError;
    private Button mBtnRetry;

    private Handler mHandler = new Handler();
    private RefreshFinishRunnable mRefreshFinishRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        mBtnLoading = findViewById(R.id.btn_loading);
        mBtnData = findViewById(R.id.btn_data);
        mBtnEmpty = findViewById(R.id.btn_empty);
        mBtnError = findViewById(R.id.btn_error);
        mBtnLoading.setOnClickListener(this);
        mBtnData.setOnClickListener(this);
        mBtnEmpty.setOnClickListener(this);
        mBtnError.setOnClickListener(this);

        mRefreshFinishRunnable = new RefreshFinishRunnable();

        mPtrRecyclerView = findViewById(R.id.ptr_recyclerview);
        mPtrRecyclerView.setOnErrorCallback(new MyPullListView.OnErrorCallback() {
            @Override
            public void onError() {
                mPtrRecyclerView.toLoading();
            }
        });


        mDataList = new ArrayList<>();

        RecyclerViewStyleHelper.toLinearLayout(mPtrRecyclerView.getContentView(), LinearLayout.VERTICAL);
        mPtrRecyclerView.getContentView().addItemDecoration(
                new RecyclerViewItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .thickness(2)
                        .paddingStart(10)
                        .paddingEnd(10)
                        .create());
        mAdapter = new RecyclerViewAdapter<>(
                this, mDataList, R.layout.item_main, new RecyclerViewSingleTypeProcessor<String>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, int position, String data) {
                TextView tvText = holder.getView(R.id.tv_text);
                tvText.setText(data);
            }
        });
        mPtrRecyclerView.getContentView().setAdapter(mAdapter);

//        mPtrRecyclerView.setRefreshEnable(false);
//        mPtrRecyclerView.setLoadMoreEnable(false);
        mPtrRecyclerView.setPullHeight(500);

        mPtrRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                mHandler.postDelayed(mRefreshFinishRunnable, 2000);
            }
        });


        mPtrRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
                mHandler.postDelayed(mRefreshFinishRunnable, 2000);
            }
        });

        mPtrRecyclerView.toLoading();
        refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loading:
                mPtrRecyclerView.toLoading();
                break;
            case R.id.btn_data:
                mPtrRecyclerView.toContent();
                break;
            case R.id.btn_empty:
                mPtrRecyclerView.toEmpty();
                break;
            case R.id.btn_error:
                mPtrRecyclerView.toError();
                break;
        }

    }

    private class RefreshFinishRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "mPtrRecyclerView.onComplete");
            mPtrRecyclerView.onRefreshComplete();
            mPtrRecyclerView.toContent();
        }
    }


    private void refreshData() {
        mDataList.clear();
        for (int i = 0; i < 20; i++) {
            mDataList.add("item " + i);
        }
        mAdapter.notifyDataChanged(mDataList);

    }

    private void loadMoreData() {
        int currentSize = mDataList.size();
        for (int i = currentSize; i < currentSize + 20; i++) {
            mDataList.add("item " + i);
        }
        mAdapter.notifyDataChanged(mDataList);
    }
}
