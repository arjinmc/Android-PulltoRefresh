package com.arjinmc.pulltorefreshdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.pulltorefresh.PulltoRefreshRecyclerView;
import com.arjinmc.pulltorefresh.listener.OnRefreshListener;
import com.arjinmc.pulltorefreshdemo.view.JDHeadView;
import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eminem Lo on 2018/6/11.
 * email: arjinmc@hotmail.com
 */
public class RecyclerViewJDActivity extends AppCompatActivity {

    private final String TAG = "RVJDActivity";

    private PulltoRefreshRecyclerView mPtrRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<String> mDataList;

    private Handler mHandler = new Handler();
    private RefreshFinishRunnable mRefreshFinishRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        mRefreshFinishRunnable = new RefreshFinishRunnable();

        mPtrRecyclerView = findViewById(R.id.ptr_recyclerview);
        mPtrRecyclerView.setHeadView(new JDHeadView(this));
//        mPtrRecyclerView.setHeadView(new RingHeadView(this));
        mPtrRecyclerView.setLoadMoreEnable(false);
        mDataList = new ArrayList<>();

        RecyclerViewStyleHelper.toLinearLayout(mPtrRecyclerView.getContentView(), LinearLayout.VERTICAL);
        mPtrRecyclerView.getContentView().addItemDecoration(
                new RecyclerViewLinearItemDecoration.Builder(this)
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


        mPtrRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                mHandler.postDelayed(mRefreshFinishRunnable, 2000);
            }
        });

        refreshData();
    }

    private class RefreshFinishRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "mPtrRecyclerView.onComplete");
            mPtrRecyclerView.onRefreshComplete();
        }
    }

    private void refreshData() {
        mDataList.clear();
        for (int i = 0; i < 20; i++) {
            mDataList.add("item " + i);
        }
        mAdapter.notifyDataChanged(mDataList);

    }
}
