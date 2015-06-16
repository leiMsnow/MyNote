package com.ray.api.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;

/**
 * popupWindow模板类
 * Created by dangdang on 15/6/15.
 */
public abstract class BasePopupWindow<T> extends PopupWindow implements View.OnTouchListener {


    protected Context mContext;
    protected View mContentView;

    protected List<T> mDatas;

    public BasePopupWindow(View contentView, int width, int height, boolean focusable) {
        this(contentView, width, height, focusable, null);
    }

    public BasePopupWindow(View contentView, int width, int height,
                           boolean focusable, List<T> datas) {
        this(contentView, width, height, focusable, datas, new Object[0]);

    }

    public BasePopupWindow(View contentView, int width, int height,
                           boolean focusable, List<T> datas, Object... params) {
        super(contentView, width, height, focusable);
        this.mContentView = contentView;
        this.mContext = contentView.getContext();
        if (datas != null)
            this.mDatas = datas;
        if (params != null && params.length > 0) {
            beforeInitWeNeedSomeParams(params);
        }
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(this);
        initView();
        initListener();
        initData();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
            dismiss();
            return true;
        }
        return false;
    }

    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    protected abstract void beforeInitWeNeedSomeParams(Object... params);

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

}
