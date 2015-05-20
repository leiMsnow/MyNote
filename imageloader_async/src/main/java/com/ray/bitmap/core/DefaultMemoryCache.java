package com.ray.bitmap.core;

import com.ray.utils.Utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 默认内存缓存类
 *
 * @author zhangleilei
 *
 */
public class DefaultMemoryCache implements IMemoryCache {

    private final LruCache<String, Bitmap> mLruCache;

    public DefaultMemoryCache(int size) {
        mLruCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return Utils.getBitmapSize(value);
            }
        };
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mLruCache.put(key, bitmap);
    }

    @Override
    public Bitmap get(String key) {
        return mLruCache.get(key);
    }

    @Override
    public void evictAll() {
        mLruCache.evictAll();
    }

    @Override
    public void remove(String key) {
        mLruCache.remove(key);
    }
}
