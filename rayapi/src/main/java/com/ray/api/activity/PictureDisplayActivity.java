package com.ray.api.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.ray.api.R;
import com.ray.api.adapter.PictureDisplayAdapter;
import com.ray.api.model.PhotoSelectData;
import com.ray.api.utils.Constants;

public class PictureDisplayActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private View mBottomView;
    private ImageButton mIbtnSelect;

    private PictureDisplayAdapter mAdapter;
    private PhotoSelectData mDatas;

    private boolean isReayOnly = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_picture_display);
        mViewPager = (ViewPager) findViewById(R.id.vp_images);
        mBottomView = findViewById(R.id.rl_bottom_panel);
        mIbtnSelect = (ImageButton) findViewById(R.id.iv_btn_select);
    }

    @Override
    protected void initData() {
        mDatas = (PhotoSelectData) getIntent().getSerializableExtra(Constants.PICTURE_DISPLAY_IMAGES);
        isReayOnly = getIntent().getBooleanExtra(Constants.PICTURE_READ_ONLY, true);
        if (!isReayOnly) {
            mBottomView.setVisibility(View.VISIBLE);
        }
        mAdapter = new PictureDisplayAdapter(mContext, mDatas);
        mViewPager.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.PICTURE_DISPLAY_IMAGES, mAdapter.getPhotoSelectData());
        setResult(AlbumActivity.SELECT_IMAGES_CODE, intent);
        finish();
    }

    @Override
    protected void initListener() {
        mViewPager.setOnPageChangeListener(this);
        mIbtnSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIbtnSelect) {
            int index = mViewPager.getCurrentItem();
            updateSelctState(true, index);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        updateSelctState(false, i);
    }

    private void updateSelctState(boolean isChanged, int i) {
        boolean isChecked = mAdapter.getPhotoSelectData().getSelectData().get(i).isSelect();
        if (isChanged) {
            mAdapter.getPhotoSelectData().getSelectData().get(i).setSelect(!isChecked);
        }
        if (isChecked) {
            mIbtnSelect.setImageResource(R.drawable.btn_check_on);
        } else {
            mIbtnSelect.setImageResource(R.drawable.btn_check_off);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
