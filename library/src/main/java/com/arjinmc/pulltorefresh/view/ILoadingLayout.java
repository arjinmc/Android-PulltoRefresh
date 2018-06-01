package com.arjinmc.pulltorefresh.view;

/**
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public interface ILoadingLayout {

    void onPulling(int height);

    void onRelease();

    void onReset();

    void onLoading();
}
