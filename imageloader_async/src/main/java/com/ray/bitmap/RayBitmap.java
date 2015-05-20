package com.ray.bitmap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.ray.bitmap.core.AsyncTask;
import com.ray.bitmap.core.BitmapCache;
import com.ray.bitmap.core.BitmapHandler;
import com.ray.bitmap.display.BitmapDisplayConfig;
import com.ray.bitmap.display.DefaultDisplayer;
import com.ray.bitmap.display.IDisplayer;
import com.ray.bitmap.download.DefaultDownloader;
import com.ray.bitmap.download.IDownloader;
import com.ray.utils.Utils;

/**
 * 图片加载主体类
 *
 * @author zhangleilei
 *
 */
public class RayBitmap {

    private Context mContext;
    private RayBitmapConfig mBitmapConfig;
    private BitmapCache mBitmapCache;
    private BitmapHandler mBitmapHandler;
    private ExecutorService bitmapExecutorService;
    private final Object mPauseWorkLock = new Object();
    private boolean mExitTasksEarly = false;
    private boolean mPauseWork = false;
    // 初始化
    private boolean mInit = false;

    private static RayBitmap mRayBitmap;

    private RayBitmap(Context context) {
        mContext = context;
        mBitmapConfig = new RayBitmapConfig(mContext);
        File cacheDir = Utils.getDiskCacheDir(mContext, "bitmap");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        setDiskCachePah(cacheDir.getAbsolutePath());
        setDisplayer(new DefaultDisplayer());
        setDownLoader(new DefaultDownloader());
    }

    /**
     * 创建bitmap
     *
     * @param context
     * @return 返回bitmap实例
     */
    public static synchronized RayBitmap openBitmap(Context context) {
        if (mRayBitmap == null) {
            mRayBitmap = new RayBitmap(context);
        }
        return mRayBitmap;
    }

    /**
     * 配置磁盘缓存路径
     *
     * @param path
     */
    public void setDiskCachePah(String path) {
        if (!TextUtils.isEmpty(path)) {
            mBitmapConfig.cachePath = path;
        }
    }

    // ************************配置bitmap*************************************
    /**
     * 配置显示器
     *
     * @param displayer
     */
    public void setDisplayer(IDisplayer displayer) {
        mBitmapConfig.displayer = displayer;
    }

    /**
     * 配置下载器
     *
     * @param downloader
     */
    public void setDownLoader(IDownloader downloader) {
        mBitmapConfig.downloader = downloader;
    }

    /**
     * 设置正在加载的图片
     *
     * @param bitmap
     */
    public void setLoadingBitmap(Bitmap bitmap) {
        mBitmapConfig.bitmapDisplayConfig.setLoadingBitmap(bitmap);
    }

    /**
     * 设置正在加载的图片
     *
     * @param resId
     *            资源id
     */
    public void setLoadingBitmap(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                resId);
        setLoadingBitmap(bitmap);
    }

    /**
     * 设置图片加载失败的图片
     *
     * @param bitmap
     */
    public void setFaildBitmap(Bitmap bitmap) {
        mBitmapConfig.bitmapDisplayConfig.setLoadfaildBitmap(bitmap);
    }

    /**
     * 设置图片加载失败的图片
     *
     * @param resId
     */
    public void setFaildBitmap(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                resId);
        mBitmapConfig.bitmapDisplayConfig.setLoadfaildBitmap(bitmap);
    }

    // ***********************加载图片*****************************************************
    /**
     * 显示 图片-使用默认配置
     *
     * @param view
     *            显示的view
     * @param url
     *            地址/可以是本地路径
     */
    public void display(View view, String url) {
        doDisplay(view, url, null);
    }

    /**
     * 显示图片-指定图片大小
     *
     * @param view 显示的view
     * @param url 地址
     *            /可以是本地路径
     * @param imageWidth
     *            指定宽度
     * @param imageHeight
     *            指定高度
     */
    public void display(View view, String url, int imageWidth, int imageHeight) {

        BitmapDisplayConfig displayConfig = getDisplayConfig();
        displayConfig.setBitmapHeight(imageHeight);
        displayConfig.setBitmapWidth(imageWidth);

        doDisplay(view, url, displayConfig);
    }

    /**
     * 设置任务是否关闭
     *
     * @param exitTasksEarly
     *            true暂停线程 false 继续线程
     */
    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * 让线程继续 在activity的onResume()调用
     */
    public void onResumeBitmap() {
        setExitTasksEarly(false);
    }

    /**
     * 让线程暂停 在activity的onPause()调用
     */
    public void onPauseBitmap() {
        setExitTasksEarly(true);
    }

    /**
     * 暂停正在加载的线程，监听listview或者gridview正在滑动的时候调用
     *
     * @param pauseWork
     *            true停止暂停线程，false继续线程
     */
    public void pauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    /**
     * 退出正在加载的线程，程序退出的时候调用
     *
     */
    public void onExitTasksEarly() {
        mExitTasksEarly = true;
        pauseWork(false);
    }

    // ***********************清理工作*****************************************************


    /**
     * 释放内存缓存在activity的onDestroy()调用 释放之后bimap已经失效，请重新open();
     */
    public void onDestroyBitmap() {
        new CacheOperationTask().execute(CacheOperationTask.CLOSE_CACHE);
    }

    /**
     * 清除磁盘缓存
     */
    public void onClearDiskCache() {
        new CacheOperationTask().execute(CacheOperationTask.CLEAR_DISK_CACHE);
    }

    /**
     * 清除内存缓存
     */
    public void onClearMemoryCache() {
        new CacheOperationTask().execute(CacheOperationTask.CLEAR_MEMORY_CACHE);
    }

    /** 清除缓存线程 */
    private class CacheOperationTask extends AsyncTask<Integer, Void, Void> {

        public static final int CLEAR_MEMORY_CACHE = 1;
        public static final int CLEAR_DISK_CACHE = 2;
        public static final int CLOSE_CACHE = 3;

        @Override
        protected Void doInBackground(Integer... params) {
            switch (params[0]) {

                case CLEAR_MEMORY_CACHE:
                    clearMemoryCache();
                    break;

                case CLEAR_DISK_CACHE:
                    clearDiskCache();
                    break;

                case CLOSE_CACHE:
                    closeCache();
                    break;
            }
            return null;
        }
    };

    // 清除内存缓存
    private void clearMemoryCache() {
        if (mBitmapCache != null) {
            mBitmapCache.clearMemoryCache();
        }
    }

    // 清除磁盘缓存
    private void clearDiskCache() {
        if (mBitmapCache != null) {
            mBitmapCache.clearDiskCache();
        }
    }

    // 清除内存缓存,并且释放当前对象
    private void closeCache() {
        clearMemoryCache();
        mBitmapCache = null;
        mRayBitmap = null;
    }

    // ************************清除工作**************************************

    /**
     * 配置项
     *
     * @return
     */
    private BitmapDisplayConfig getDisplayConfig() {
        BitmapDisplayConfig config = new BitmapDisplayConfig();
        config.setAnimation(mBitmapConfig.bitmapDisplayConfig.getAnimation());
        config.setAnimationType(mBitmapConfig.bitmapDisplayConfig
                .getAnimationType());
        config.setBitmapHeight(mBitmapConfig.bitmapDisplayConfig
                .getBitmapHeight());
        config.setBitmapWidth(mBitmapConfig.bitmapDisplayConfig
                .getBitmapWidth());
        config.setLoadfaildBitmap(mBitmapConfig.bitmapDisplayConfig
                .getLoadfaildBitmap());
        config.setLoadingBitmap(mBitmapConfig.bitmapDisplayConfig
                .getLoadingBitmap());
        return config;
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        if (!mInit) {
            BitmapCache.BitmapCacheParams cacheParams = new BitmapCache.BitmapCacheParams(
                    mBitmapConfig.cachePath, mContext);
            // 初始化缓存
            mBitmapCache = new BitmapCache(cacheParams);
            // 创建线程池
            bitmapExecutorService = Executors.newFixedThreadPool(
                    mBitmapConfig.poolSize, new ThreadFactory() {

                        @Override
                        public Thread newThread(Runnable run) {
                            Thread t = new Thread(run);
                            // 设置线程的优先级
                            t.setPriority(Thread.NORM_PRIORITY - 1);
                            return t;
                        }
                    });
            // 初始化bitmap处理类
            mBitmapHandler = new BitmapHandler(mBitmapConfig.downloader,
                    mBitmapCache);

            mInit = true;
        }
    }

    // 开始执行主方法
    private void doDisplay(View view, String url,
                           BitmapDisplayConfig displayConfig) {
        if (view == null || TextUtils.isEmpty(url)) {
            return;
        }
        if (!mInit) {
            initConfig();
        }
        if (displayConfig == null) {
            displayConfig = mBitmapConfig.bitmapDisplayConfig;
        }

        Bitmap bitmap = null;
        if (mBitmapCache != null) {
            bitmap = mBitmapCache.getMemoryCache(url);
        }
        if (bitmap != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            } else {
                Drawable background = new BitmapDrawable(bitmap);
                view.setBackgroundDrawable(background);
            }
        } else if (cancelPotentialWork(url, view)) {
            BitmapWorkerTask bitmapTask = new BitmapWorkerTask(view,
                    displayConfig);
            // 设置默认图片
            AsyncDrawable asyncDrawable = new AsyncDrawable(
                    mContext.getResources(), displayConfig.getLoadingBitmap(),
                    bitmapTask);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(asyncDrawable);
            } else {
                view.setBackgroundDrawable(asyncDrawable);
            }

            bitmapTask.executeOnExecutor(bitmapExecutorService, url);
        }

    }

    /**
     * 检查确保了另外一个在ImageView中运行的任务得以取消
     *
     * @param data
     * @param view
     * @return true有线程在运行 false 没有
     */
    private boolean cancelPotentialWork(String data, View view) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(view);

        if (bitmapWorkerTask != null) {
            String bitmapData = bitmapWorkerTask.url;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    // 创建一个专用的Drawable的子类来储存返回工作任务的引用。
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask workTask) {
            super(res, bitmap);
            bitmapWeakReference = new WeakReference<RayBitmap.BitmapWorkerTask>(
                    workTask);
        }

        public BitmapWorkerTask getBitmapWorkTask() {
            return bitmapWeakReference.get();
        }
    }

    // 检索任务是否已经被分配到指定的 View
    private static BitmapWorkerTask getBitmapWorkerTask(View view) {
        if (view != null) {
            Drawable drawable = null;
            if (view instanceof ImageView) {
                drawable = ((ImageView) view).getDrawable();
            } else {
                drawable = view.getBackground();
            }

            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkTask();
            }
        }
        return null;
    }

    /**
     * bitmap下载显示的线程
     *
     * @author zhangleilei
     *
     */
    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private WeakReference<View> viewReference;
        private BitmapDisplayConfig displayConfig;

        public BitmapWorkerTask(View view, BitmapDisplayConfig config) {
            viewReference = new WeakReference<View>(view);
            displayConfig = config;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            Bitmap bitmap = null;

            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (Exception e) {

                    }
                }
            }

            if (bitmap == null && !isCancelled() && getAttachedView() != null
                    && !mExitTasksEarly) {
                bitmap = handlerBitmap(url, displayConfig);
            }

            if (bitmap != null) {
                mBitmapCache.addToMemoryCache(url, bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            // 判断线程和当前view是否匹配
            View view = getAttachedView();
            if (bitmap != null && view != null) {
                mBitmapConfig.displayer.loadComplete(view, bitmap,
                        displayConfig);
            } else if (bitmap == null && view != null) {
                mBitmapConfig.displayer.loadFail(view, bitmap);
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * 获得绑定的view
         *
         * @return
         */
        private View getAttachedView() {
            final View view = viewReference.get();
            final BitmapWorkerTask bitmapWorkTask = getBitmapWorkerTask(view);
            if (this == bitmapWorkTask) {
                return view;
            }
            return null;
        }

    }

    /**
     * 网络加载图片
     *
     * @param url
     * @param config
     * @return
     */
    private Bitmap handlerBitmap(String url, BitmapDisplayConfig config) {
        if (mBitmapHandler != null) {
            return mBitmapHandler.getBitmapForUrl(url, config);
        }
        return null;
    }

    /**
     * 基础配置项-内部类
     *
     * @author zhangleilei
     *
     */
    private class RayBitmapConfig {
        // 缓存路径
        public String cachePath;
        // 默认线程池并发数量
        public int poolSize = 5;
        // 显示器
        public IDisplayer displayer;
        // 下载器
        public IDownloader downloader;
        // 显示器配置项
        public BitmapDisplayConfig bitmapDisplayConfig;

        public RayBitmapConfig(Context context) {
            bitmapDisplayConfig = new BitmapDisplayConfig();
            // 设置动画
            bitmapDisplayConfig.setAnimation(null);
            bitmapDisplayConfig
                    .setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);

            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            // 设置默认屏幕1/4的大小
            int pixels = (int) Math.floor(displayMetrics.widthPixels / 4);
            bitmapDisplayConfig.setBitmapHeight(pixels);
            bitmapDisplayConfig.setBitmapWidth(pixels);
        }
    }

}
