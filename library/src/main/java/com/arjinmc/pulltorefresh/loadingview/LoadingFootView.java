package com.arjinmc.pulltorefresh.loadingview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

/**
 * LoadingFootView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public abstract class LoadingFootView extends LoadingView {

    public LoadingFootView(@NonNull Context context) {
        super(context);
    }

    public LoadingFootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
