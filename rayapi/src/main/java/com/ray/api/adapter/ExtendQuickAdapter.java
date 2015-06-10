package com.ray.api.adapter;

import android.content.Context;

import java.util.List;

/**
 * 扩展通用baseAdapter;
 * 主要为convert方法添加一个itemChanged参数，用于区分 dataset changed / dataset invalidated
 * 它的实现是通过helper.associatedObject的equals()方法，
 * associatedObject即我们的bean。在BaseHelperAdapter可以看到其赋值的代码。
 * Created by zhangleilei on 15/6/8.
 */
@Deprecated
public abstract class ExtendQuickAdapter<T> extends QuickAdapter<T> {


    public ExtendQuickAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public ExtendQuickAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }

    public ExtendQuickAdapter(Context context, List data, IMultiItemTypeSupport multiItemTypeSupport) {
        super(context, data, multiItemTypeSupport);
    }


    @Override
    protected final void convert(BaseAdapterHelper helper, T item) {
//        boolean itemChanged = helper.associatedObject == null || !helper.associatedObject.equals(item);
//        helper.associatedObject = item;
//        convert(helper, item, itemChanged);
    }

    protected abstract void convert(BaseAdapterHelper helper, T item, boolean itemChanged);


}
