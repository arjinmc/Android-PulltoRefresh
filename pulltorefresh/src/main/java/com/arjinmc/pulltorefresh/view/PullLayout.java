package com.arjinmc.pulltorefresh.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * LoadingView
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public abstract class PullLayout extends FrameLayout implements IPullLayout {

    protected int mOrientation;

    public PullLayout(@NonNull Context context) {
        super(context);
    }

    public PullLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOrientation(int orientation) {

        if (orientation != LinearLayout.HORIZONTAL || orientation != LinearLayout.VERTICAL) {
            try {
                throw new IllegalAccessException("Only support LinearLayout Orientation!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }

        mOrientation = orientation;

    }
}
