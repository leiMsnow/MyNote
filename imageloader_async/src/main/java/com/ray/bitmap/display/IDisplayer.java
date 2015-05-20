package com.ray.bitmap.display;

import android.graphics.Bitmap;
import android.view.View;

/**
 * 显示器接口
 *
 * @author zhangleilei
 *
 */
public interface IDisplayer {
    /**
     * 回调加载完成
     *
     * @param view
     * @param bitmap
     * @param config
     */
    void loadComplete(View view, Bitmap bitmap, BitmapDisplayConfig config);

    /**
     * 回调加载失败
     *
     * @param view
     * @param bitmap
     */
    void loadFail(View view, Bitmap bitmap);

}
