package com.ray.api.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * 基础activity
 */
public abstract class BaseActivity extends Activity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initView();
        initData();
        initListener();
    }

    /**初始化view*/
    protected abstract void initView();
    /**初始化data*/
    protected abstract void initData();
    /**初始化listener*/
    protected abstract void initListener();

}
