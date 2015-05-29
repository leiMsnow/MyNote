package com.ray.api.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ray.api.utils.ToastUtil;

/**
 * 基础通用Fragment类
 *
 * @author zhangleilei
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected View mView;

    protected IBackPressListener backPressListener;
    protected ISwitchFragmentListener switchFragmentListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            backPressListener = (IBackPressListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + "must implements IBackPressListener");
        }
        try {
            switchFragmentListener = (ISwitchFragmentListener) activity;
        } catch (Exception e) {
            new Throwable(activity.toString()
                    + " must implements ISwitchFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置当前显示的fragment
        backPressListener.setSelectedFragment(this);
    }

    public void showToast(String text) {
        ToastUtil.getInstance(mContext).showToast(text);
    }

    public void showToast(int text) {
        ToastUtil.getInstance(mContext).showToast(
                getResources().getString(text));
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    /**
     * 处理按下back功能,子类可以复写该功能
     *
     * @return true 不执行back操作;false执行back操作
     */
    public boolean onBackPressed() {
        return false;
    }


    /**
     * 设置当前的fragment
     *
     * @author zhangleilei
     */
    public interface IBackPressListener {
        public void setSelectedFragment(BaseFragment selectedFragment);
    }

    /**
     * 切换fragment监听
     *
     * @author zhangleilei
     */
    public interface ISwitchFragmentListener {
        public void onSwitchFragment(String fragmentFlag, Bundle bundle);
    }
}
