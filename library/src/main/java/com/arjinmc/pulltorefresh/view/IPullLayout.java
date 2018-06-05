package com.arjinmc.pulltorefresh.view;

/**
 * Created by Eminem Lo on 2018/5/30.
 * email: arjinmc@hotmail.com
 */
public interface IPullLayout {

    void onPulling(int pullMaxHeight, int currentHeight);

    void onReset();

    void onLoading();

    void onSwitchTips(boolean showReleaseTips);
}
