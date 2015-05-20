package com.ray.bitmap.display;

import android.graphics.Bitmap;
import android.view.animation.Animation;

/**
 * 图片显示配置类
 *
 * @author zhangleilei
 *
 */
public class BitmapDisplayConfig {

    // 图片宽度
    private int bitmapWidth;
    // 图片高度
    private int bitmapHeight;
    // 动画类型
    private int animationType;
    // 图片动画
    private Animation animation;
    // 加载中图片
    private Bitmap loadingBitmap;
    // 加载失败图片
    private Bitmap loadfaildBitmap;

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(int bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(int bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public int getAnimationType() {
        return animationType;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Bitmap getLoadingBitmap() {
        return loadingBitmap;
    }

    public void setLoadingBitmap(Bitmap loadingBitmap) {
        this.loadingBitmap = loadingBitmap;
    }

    public Bitmap getLoadfaildBitmap() {
        return loadfaildBitmap;
    }

    public void setLoadfaildBitmap(Bitmap loadfaildBitmap) {
        this.loadfaildBitmap = loadfaildBitmap;
    }

    public class AnimationType {
        public static final int userDefined = 0;
        public static final int fadeIn = 1;
    }

}
