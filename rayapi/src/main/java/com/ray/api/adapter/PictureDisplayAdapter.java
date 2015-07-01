package com.ray.api.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ray.api.model.PhotoSelectData;
import com.ray.api.utils.ScreenUtils;
import com.ray.api.view.ZoomImageView;
import com.ray.bitmap.RayBitmap;

/**
 * Created by dangdang on 15/6/18.
 */
public class PictureDisplayAdapter extends PagerAdapter {


    private PhotoSelectData photoSelectData;
    private Context mContext;
    private ImageView[] images;

    public PhotoSelectData getPhotoSelectData() {
        return photoSelectData;
    }

    public PictureDisplayAdapter(Context context, PhotoSelectData datas) {
        this.mContext = context;
        this.photoSelectData = datas;
        this.images = new ImageView[photoSelectData.getSelectData().size()];
    }

    @Override
    public int getCount() {
        return photoSelectData.getSelectData().size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView image = new ZoomImageView(mContext);
        images[position] = image;
        RayBitmap.openBitmap(mContext).display(image,
                photoSelectData.getDir() + photoSelectData.getSelectData().get(position).getUri(),
                ScreenUtils.getScreenWidth(mContext),
                ScreenUtils.getScreenHeight(mContext));
        container.addView(image);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(images[position]);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
