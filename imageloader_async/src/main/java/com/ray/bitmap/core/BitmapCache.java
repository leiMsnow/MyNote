package com.ray.bitmap.core;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.ray.bitmap.core.DiskLruCache.Snapshot;
import com.ray.bitmap.display.BitmapDisplayConfig;
import com.ray.utils.Utils;

/**
 * bitmap缓存类
 *
 * @author zhangleilei
 *
 */
public class BitmapCache {

    // 默认内存大小设置 8M
    private static final int DEFAULT_M_CACHE_SIZE = 1024 * 1024 * 8;
    // 默认磁盘缓存大小 50M
    private static final int DEFAULT_D_CACHE_SIZE = 1024 * 1024 * 50;

    private IMemoryCache mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private BitmapCacheParams mBitmapCacheParams;

    public BitmapCache(BitmapCacheParams params) {
        initParams(params);
    }

    /**
     * 初始化缓存参数
     *
     * @param params
     */
    private void initParams(BitmapCacheParams params) {
        mBitmapCacheParams = params;
        mMemoryCache = new DefaultMemoryCache(
                mBitmapCacheParams.memoryCacheSize);
        try {
            mDiskLruCache = DiskLruCache.open(mBitmapCacheParams.diskCacheDir,
                    mBitmapCacheParams.appVersion, 1,
                    mBitmapCacheParams.diskCacheSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加数据到内存缓存中
     *
     * @param url
     * @param bitmap
     */
    public void addToMemoryCache(String url, Bitmap bitmap) {
        if (url == null || bitmap == null) {
            return;
        }
        mMemoryCache.put(url, bitmap);
    }

    /**
     * 获取内存缓存文件
     *
     * @param url
     * @return
     */
    public Bitmap getMemoryCache(String url) {
        if (url == null) {
            return null;
        }
        if (mMemoryCache != null) {
            return mMemoryCache.get(url);
        }
        return null;
    }

    /**
     * 添加数据到SD卡缓存中
     *
     * @param url
     * @param bitmap
     */
    public void addToSDCardCache(String url, Bitmap bitmap) {
        if (mDiskLruCache == null || url == null || bitmap == null) {
            return;
        }
        // 生成图片URL对应的key
        String newKey = Utils.hashKeyFromSDCard(url);
        try {
            // 写入SD卡缓存
            DiskLruCache.Editor editor = mDiskLruCache.edit(newKey);
            if (editor != null) {
                OutputStream outputStream;
                outputStream = editor.newOutputStream(0);

                if (bitmap
                        .compress(Bitmap.CompressFormat.PNG, 80, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SD卡的缓存文件
     *
     * @param url
     * @param config
     * @return
     */
    public Bitmap getToSDCardCache(String url, BitmapDisplayConfig config) {
        Bitmap bitmap = null;
        Snapshot snapShot = null;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        // 生成图片URL对应的key
        String newKey = Utils.hashKeyFromSDCard(url);
        try {
            if (mDiskLruCache != null) {
                snapShot = mDiskLruCache.get(newKey);
                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot
                            .getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
            }
            if (fileDescriptor != null) {
                bitmap = BitmapDecoder.decodeSampleBitmapFromFileDescriptor(
                        fileDescriptor, config.getBitmapWidth(),
                        config.getBitmapHeight());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /** 清除内存缓存 */
    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    /** 清除磁盘缓存 */
    public void clearDiskCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 获取当前磁盘缓存大小 */
    public int getDiskCacheSize() {
        if (mDiskLruCache != null) {
            return (int) mDiskLruCache.size();
        }
        return 0;
    }

    /**
     * bitmap缓存参数配置类
     *
     * @author zhangleilei
     *
     */
    public static class BitmapCacheParams {
        public int memoryCacheSize = DEFAULT_M_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_D_CACHE_SIZE;
        public int appVersion;
        public File diskCacheDir;

        public BitmapCacheParams(String diskCacheDir, Context context) {
            this.diskCacheDir = new File(diskCacheDir);
            this.appVersion = Utils.getAppVersion(context);

        }

        public void setMemoryCacheSizePercent(Context context, float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException(
                        "setMemoryCacheSizePercent - set memory cache size percent must  be between 0.05 and 0.8");
            }
            memoryCacheSize = Math.round(percent * getMemoryClass(context)
                    * 1024 * 1024);
        }

        public void setMemCacheSize(int memCacheSize) {
            this.memoryCacheSize = memCacheSize;
        }

        private int getMemoryClass(Context context) {
            return ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
        }
    }

}
