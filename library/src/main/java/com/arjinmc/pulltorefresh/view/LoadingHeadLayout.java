package com.arjinmc.pulltorefresh.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

/**
 * LoadingHeadView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public abstract class LoadingHeadLayout extends LoadingLayout {

    public LoadingHeadLayout(@NonNull Context context) {
        super(context);
    }

    public LoadingHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingHeadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
