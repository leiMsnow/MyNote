package com.ray.api.activity;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.ray.api.fragment.BaseFragment;
import com.ray.api.handler.ActivityManager;
import com.ray.api.view.BaseProgressDialog;

/**
 * 基础activity
 */
public abstract class BaseActivity extends Activity {

    protected Context mContext;
    protected FragmentManager mFragmentManager;
    protected BaseFragment mBaseFragment;
    protected ActivityManager mManager;
    protected BaseProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = ActivityManager.getInstance();
        mManager.addActivity(this);
        this.mContext = this;
        mFragmentManager = getFragmentManager();

        initView();
        initData();
        initListener();
    }

    /**初始化view */
    protected abstract void initView();
    /**初始化data*/
    protected abstract void initData();
    /**初始化listener*/
    protected abstract void initListener();

}
