package com.ray.note.adapter;

import android.content.Context;
import android.view.View;

import com.ray.api.helper.adapter.BaseAdapterHelper;
import com.ray.api.helper.adapter.QuickAdapter;
import com.ray.note.R;

import java.util.List;

/**
 * Created by dangdang on 15/6/9.
 */
public class HelperListViewAdapter extends QuickAdapter<String> {


    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HelperListViewAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, String item) {
        helper.setText(R.id.text1, item);
        helper.setTag(R.id.text1, item);
        helper.setOnClickListener(R.id.text1, onClickListener);
    }
}
