package com.ray.note.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ray.api.activity.BaseActivity;
import com.ray.api.utils.ScreenUtils;
import com.ray.bitmap.RayBitmap;
import com.ray.note.R;

/**
 * Created by zhangll on 15/5/22.
 */
public class ImageViewActivity extends BaseActivity implements View.OnClickListener {


    private ImageView ivZoomImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_zoom_image_view);

        ivZoomImage = (ImageView) findViewById(R.id.iv_zoom_image);
    }


    @Override
    protected void initData() {
        RayBitmap.openBitmap(mContext).display(ivZoomImage,"http://img.win7china.com/NewsUploadFiles/20090823_121117_718_u.jpg"
        , ScreenUtils.getScreenWidth(mContext),ScreenUtils.getScreenHeight(mContext));
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {
    }
}
