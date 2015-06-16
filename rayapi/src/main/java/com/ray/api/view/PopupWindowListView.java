package com.ray.api.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ray.api.R;
import com.ray.api.helper.adapter.BaseAdapterHelper;
import com.ray.api.helper.adapter.QuickAdapter;
import com.ray.api.model.PhotoFolder;

import java.util.List;

/**
 * Created by dangdang on 15/6/16.
 */
public class PopupWindowListView extends BasePopupWindow implements AdapterView.OnItemClickListener {


    private ListView mListImage;

    private IPhotoDirSelectListener iPhotoDirSelectListener;

    public void setiPhotoDirSelectListener(IPhotoDirSelectListener iPhotoDirSelectListener) {
        this.iPhotoDirSelectListener = iPhotoDirSelectListener;
    }

    public PopupWindowListView(View contentView, int width, int height, List<PhotoFolder> datas) {
        super(contentView, width, height, true, datas);
    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {

    }

    @Override
    protected void initView() {
        mListImage = (ListView) findViewById(R.id.lv_images);
    }

    @Override
    protected void initData() {
        mListImage.setAdapter(new QuickAdapter<PhotoFolder>(mContext, R.layout.item_list_images_dir, mDatas) {
            @Override
            protected void convert(BaseAdapterHelper helper, PhotoFolder item) {
                helper.setText(R.id.tv_dir_name, item.getFileName());
                helper.setImageBitmapForUrl(R.id.iv_dir_url, item.getFirstPhotoPath());
                helper.setText(R.id.tv_photo_count, item.getPhotoCount() + "张");
            }
        });
    }

    @Override
    protected void initListener() {
        mListImage.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (iPhotoDirSelectListener != null) {
            iPhotoDirSelectListener.onPhotoDirSelected((PhotoFolder) mDatas.get(i));
        }
    }

    /**
     * 文件夹选择监听
     */
    public interface IPhotoDirSelectListener {
        void onPhotoDirSelected(PhotoFolder photoFolder);
    }
}
