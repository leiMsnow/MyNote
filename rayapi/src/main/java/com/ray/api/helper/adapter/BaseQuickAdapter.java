package com.ray.api.helper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类继承自BaseAdapter，完成BaseAdapter中部分通用抽象方法的编写，类似ArrayAdapter.
 * 该类声明了两个泛型，一个是我们的Bean（T），一个是BaseAdapterHelper(H)主要用于扩展BaseAdapterHelper时使用。
 * Created by zhangleilei on 15/6/4.
 */
public abstract class BaseQuickAdapter<T, H extends BaseAdapterHelper> extends BaseAdapter {

    protected Context mContext;
    //资源布局
    protected int mLayoutResId;
    //数据集合
    protected List<T> mData;
//    protected boolean mDisplayIndeterminateProgress = false;
    protected IMultiItemTypeSupport<T> mMultiItemTypeSupport;

    public BaseQuickAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    public BaseQuickAdapter(Context context, int layoutResId, List<T> data) {
        this.mContext = context;
        this.mLayoutResId = layoutResId;
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
    }

    //新增可以扩展item的构造参数
    public BaseQuickAdapter(Context context, List<T> data, IMultiItemTypeSupport<T> multiItemTypeSupport) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.mMultiItemTypeSupport = multiItemTypeSupport;
    }

    //---------------------------用与实现adapter方法-start-----------------------------------

    @Override
    public int getCount() {
//        int extra = mDisplayIndeterminateProgress ? 1 : 0;
//        if (mData.size() == 0) {
//            mDisplayIndeterminateProgress = true;
//            return 1;
//        } else {
//            mDisplayIndeterminateProgress = false;
//            return mData.size();
//        }
        return mData.size();//+ extra;
    }

    @Override
    public T getItem(int i) {
        if (i >= mData.size())
            return null;
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemTypeSupport != null)
            return mMultiItemTypeSupport.getViewTypeCount();
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null) {
//            if (mDisplayIndeterminateProgress) {
//                return position >= mData.size() ? 0 : mMultiItemTypeSupport.getItemViewType(position, mData.get(position));
//            } else {
                return mMultiItemTypeSupport.getItemViewType(position, mData.get(position));
//            }
        }
        return position >= mData.size() ? 0 : 1;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (mDisplayIndeterminateProgress) {
//            return createEmptyDataView(view, viewGroup);
//        }
        final H helper = getAdapterHelper(i, view, viewGroup);
        T item = getItem(i);
//        helper.setAssociatedObject(item);
        //对外公布的convert抽象方法
        convert(helper, item);
        return helper.getView();
    }

//    private View createEmptyDataView(View view, ViewGroup viewGroup) {
//        if (view == null) {
//            FrameLayout container = new FrameLayout(mContext);
//
//            container.setForegroundGravity(Gravity.CENTER);
//            ImageView emptyView = new ImageView(mContext);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            emptyView.setLayoutParams(params);
//            emptyView.setImageResource(R.drawable.ic_loading);
//            container.addView(emptyView);
//            view = container;
//        }
//        return view;
//    }
    //---------------------------用与实现adapter方法-end-----------------------------------


    //-------------------------------用与操作data-start-----------------------------------


    public void add(T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void add(T item, boolean isChanged) {
        mData.add(item);
        if (isChanged)
            notifyDataSetChanged();
    }

    public void add(int index, T item) {
        mData.add(index, item);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items) {
        mData.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mData.remove(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void set(T oldItem, T newItem) {
        set(mData.indexOf(oldItem), newItem);
    }

    public void set(int index, T item) {
        mData.set(index, item);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> items) {
        mData.clear();
        addAll(items);
    }

    public boolean contains(T item) {
        return mData.contains(item);
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
    //-------------------------------用与操作data-end-----------------------------------

//    public void showIndeterminateProgress(boolean dispaly) {
//        if (dispaly == mDisplayIndeterminateProgress) {
//            return;
//        }
//        mDisplayIndeterminateProgress = dispaly;
//        notifyDataSetChanged();
//
//    }

    @Override
    public boolean isEnabled(int position) {
        return position < mData.size();
    }

    /**
     * 这个抽象方法需要自行实现视图与数据的绑定
     *
     * @param helper 视图控件
     * @param item   数据
     */
    protected abstract void convert(H helper, T item);


    protected abstract H getAdapterHelper(int position, View view, ViewGroup viewGroup);
}
