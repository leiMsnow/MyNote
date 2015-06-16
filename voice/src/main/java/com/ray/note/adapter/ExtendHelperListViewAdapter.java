package com.ray.note.adapter;

import android.content.Context;
import android.view.View;

import com.ray.api.helper.adapter.BaseAdapterHelper;
import com.ray.api.helper.adapter.QuickAdapter;
import com.ray.api.helper.adapter.IMultiItemTypeSupport;
import com.ray.note.R;

import java.util.List;

/**
 * Created by dangdang on 15/6/9.
 */
public class ExtendHelperListViewAdapter extends QuickAdapter<String> {


    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public ExtendHelperListViewAdapter(Context context, List data, IMultiItemTypeSupport multiItemTypeSupport) {
        super(context, data, multiItemTypeSupport);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, String item) {
        switch (helper.getLayoutId()) {
            case android.R.layout.simple_expandable_list_item_1:
                helper.setText(android.R.id.text1, item);
                helper.setTextColor(android.R.id.text1, mContext.getResources().getColor(R.color.gray));
                break;
            case android.R.layout.simple_expandable_list_item_2:
                helper.setText(android.R.id.text1, item);
                helper.setText(android.R.id.text2, item);

                helper.setTag(android.R.id.text1,item);
                helper.setOnClickListener(android.R.id.text1,onClickListener);
                break;
        }
    }
}
