package com.ray.bitmap.core;

import android.graphics.Bitmap;

/**
 * 内存缓存接口
 *
 * @author zhangleilei
 *
 */
public interface IMemoryCache {
    void put(String key, Bitmap bitmap);

    Bitmap get(String key);

    void evictAll();

    void remove(String key);
}
