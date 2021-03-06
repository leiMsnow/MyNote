package com.ray.api.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.ray.api.R;
import com.ray.api.helper.adapter.BaseAdapterHelper;
import com.ray.api.helper.adapter.QuickAdapter;
import com.ray.api.model.PhotoData;

import java.util.List;

/**
 * Created by dangdang on 15/6/15.
 */
public class AlbumAdapter extends QuickAdapter<PhotoData> {


    //文件夹路径
    private String mDirPath;

    public void setDirPath(String mDirPath) {
        this.mDirPath = mDirPath;
    }

    public String getDirPath() {
        return mDirPath;
    }

    private IPhotoSelectListener photoSelectListener;

//    private IPhotoClickListener photoClickListener;
//
//    public void setPhotoClickListener(IPhotoClickListener photoClickListener) {
//        this.photoClickListener = photoClickListener;
//    }


    public void setPhotoSelectListener(IPhotoSelectListener photoSelectListener) {
        this.photoSelectListener = photoSelectListener;
    }


    public AlbumAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, final PhotoData item) {
        String uri = mDirPath + "/" + item.getUri();
        //设置图片
        helper.setImageBitmapForUrl(R.id.iv_photo, uri);
        helper.setOnClickListener(R.id.iv_btn_select, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoSelectListener != null) {
                    photoSelectListener.onPhotoSelected(item, false);
                }
            }
        });
        helper.setOnClickListener(R.id.iv_photo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoSelectListener != null) {
                    photoSelectListener.onPhotoSelected(item, true);
                }
//                if (photoClickListener != null)
//                    photoClickListener.onPhotoClick(item);
            }
        });
        helper.setTag(R.id.iv_btn_select, item.getIndex());
        if (!item.isSelect()) {
            helper.setColorFilter(R.id.iv_photo, null);
            helper.setImageResource(R.id.iv_btn_select, R.drawable.btn_check_off);
        } else {
            helper.setColorFilter(R.id.iv_photo, Color.parseColor("#77000000"));
            helper.setImageResource(R.id.iv_btn_select, R.drawable.btn_check_on);
        }
    }


    public interface IPhotoSelectListener {
        void onPhotoSelected(PhotoData photoData, boolean isDisplay);
    }

//    public interface IPhotoClickListener {
//        void onPhotoClick(PhotoData photoData);
//    }
}
