package com.arjinmc.pulltorefreshdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.pulltorefresh.PulltoRefreshRecyclerView;
import com.arjinmc.recyclerviewdecoration.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PulltoRefreshRecyclerView mPtrRecyclerView;
    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPtrRecyclerView = findViewById(R.id.ptr_recyclerview);
        mDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDataList.add("item " + i);
        }

        RecyclerViewStyleHelper.toLinearLayout(mPtrRecyclerView.getContentView(), LinearLayout.VERTICAL);
        mPtrRecyclerView.getContentView().addItemDecoration(
                new RecyclerViewItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .thickness(2)
                        .paddingStart(10)
                        .paddingEnd(10)
                        .create());
        mPtrRecyclerView.getContentView().setAdapter(new RecyclerViewAdapter<>(
                this, mDataList, R.layout.item_main, new RecyclerViewSingleTypeProcessor<String>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, int position, String data) {
                TextView tvText = holder.getView(R.id.tv_text);
                tvText.setText(data);
            }
        }));
    }
}
