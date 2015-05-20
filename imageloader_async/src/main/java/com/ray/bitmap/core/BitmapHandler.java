package com.ray.bitmap.core;

import android.graphics.Bitmap;

import com.ray.bitmap.display.BitmapDisplayConfig;
import com.ray.bitmap.download.IDownloader;

/**
 * bitmap处理类 下载网络、加载本地图片和压缩等工作
 *
 * @author zhangleilei
 *
 */
public class BitmapHandler {
    private IDownloader mDownloader;
    private BitmapCache mBitmapCache;

    public BitmapHandler(IDownloader downloader, BitmapCache cache) {
        this.mDownloader = downloader;
        this.mBitmapCache = cache;
    }

    /**
     * 执行异步获取图片
     *
     * @param url
     *            图片路径
     * @param config
     *            图片显示配置类
     * @return 压缩后的图片
     */
    public Bitmap getBitmapForUrl(String url, BitmapDisplayConfig config) {

        Bitmap bitmap = mBitmapCache.getToSDCardCache(url, config);
        if (bitmap == null) {
            byte[] data = mDownloader.download(url);
            if (data != null && data.length > 0) {

                bitmap = BitmapDecoder.decodeSampleBitmapFromByteArray(data, 0,
                        data.length, config.getBitmapWidth(),
                        config.getBitmapHeight());

                if (bitmap != null) {
                    mBitmapCache.addToSDCardCache(url, bitmap);
                }
            }
        }
        return bitmap;
    }
}
