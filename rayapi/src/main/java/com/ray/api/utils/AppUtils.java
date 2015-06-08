package com.ray.api.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 跟App相关的辅助类
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取系统版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建水印效果
     * @param context
     * @param src
     * @param res
     * @return
     */
    public static Bitmap createWatermarkBitmap(Context context,Bitmap src, int res) {
        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), res);
        float w = src.getWidth();
        float h = src.getHeight();
        float ww = watermark.getWidth();
        float wh = watermark.getHeight();

        // 缩放图片的尺寸
        float scaleWidth = w / 2 / ww;
        float scaleHeight = h / 2 / wh;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(watermark, 0, 0, (int) ww, (int) wh, matrix, false);

        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.ARGB_8888);
        // 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // draw src into
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        cv.drawBitmap(src, 0, 0, paint);// 在 0，0坐标开始画入src
        cv.drawBitmap(resizeBitmap, 0, 0, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        // resizeBitmap.recycle();
        return newb;
    }

}
