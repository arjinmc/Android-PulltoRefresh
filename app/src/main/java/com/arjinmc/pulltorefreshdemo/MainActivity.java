package com.arjinmc.pulltorefreshdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mPtrRecyclerView;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPtrRecyclerView = findViewById(R.id.recyclerview);
        titles = getResources().getStringArray(R.array.list);

        RecyclerViewStyleHelper.toLinearLayout(mPtrRecyclerView, LinearLayout.VERTICAL);
        mPtrRecyclerView.addItemDecoration(
                new RecyclerViewLinearItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .thickness(2)
                        .create());
        mPtrRecyclerView.setAdapter(new RecyclerViewAdapter<>(
                this, Arrays.asList(titles), R.layout.item_main
                , new RecyclerViewSingleTypeProcessor<String>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, final int position, String data) {
                TextView tvText = holder.getView(R.id.tv_text);
                tvText.setText(data);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (position) {
                            case 0:
                                jumpActivity(RecyclerViewDefaultActivity.class);
                                break;
                            case 1:
                                jumpActivity(RecyclerViewJDActivity.class);
                                break;
                            case 2:
                                jumpActivity(RecyclerViewHorizontalActivity.class);
                                break;
                            case 3:
                                jumpActivity(StatusModeActivity.class);
                                break;
                            case 4:
                                jumpActivity(CustomPulltoRefreshActivity.class);
                                break;
                            case 5:
                                jumpActivity(ScrollViewActivity.class);
                                break;
                            case 6:
                                jumpActivity(HorizontalScrollViewActivity.class);
                                break;
                            case 7:
                                jumpActivity(NestedScrollViewActivity.class);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }));

    }

    private void jumpActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }
}
