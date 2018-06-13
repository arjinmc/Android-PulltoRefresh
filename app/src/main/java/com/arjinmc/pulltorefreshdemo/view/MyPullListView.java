package com.arjinmc.pulltorefreshdemo.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.arjinmc.pulltorefresh.PulltoRefreshRecyclerView;
import com.arjinmc.pulltorefresh.view.DefaultEmptyLayout;
import com.arjinmc.pulltorefresh.view.DefaultLoadingLayout;
import com.arjinmc.pulltorefreshdemo.R;

/**
 * Created by Eminem Lo on 2018/6/13.
 * email: arjinmc@hotmail.com
 */
public class MyPullListView extends PulltoRefreshRecyclerView {

    private LinearLayout mLayoutError;
    private OnErrorCallback mOnErrorCallback;

    public MyPullListView(Context context) {
        super(context);
        init();
    }

    public MyPullListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyPullListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyPullListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        setEmptyView(new DefaultEmptyLayout(getContext()));
        setLoadingView(new DefaultLoadingLayout(getContext()));
        mLayoutError = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_status_error, null);
        setErrorView(mLayoutError);

        Button btnTry = mLayoutError.findViewById(R.id.btn_retry);
        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnErrorCallback != null) {
                    mOnErrorCallback.onError();
                }
            }
        });
    }

    public void setOnErrorCallback(OnErrorCallback onErrorCallback) {
        mOnErrorCallback = onErrorCallback;
    }

    public interface OnErrorCallback {
        void onError();
    }


}
