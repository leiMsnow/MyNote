package com.ray.bitmap.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * 默认显示器
 *
 * @author zhangleilei
 *
 */
public class DefaultDisplayer implements IDisplayer {

    @Override
    public void loadComplete(View view, Bitmap bitmap,
                             BitmapDisplayConfig config) {
        if (bitmap != null) {
            switch (config.getAnimationType()) {
                case BitmapDisplayConfig.AnimationType.fadeIn:
                    displayFadeIn(view, bitmap);
                    break;
                case BitmapDisplayConfig.AnimationType.userDefined:
                    displayUserDefined(view, bitmap, config.getAnimation());
                    break;
            }
        }
    }

    @Override
    public void loadFail(View view, Bitmap bitmap) {
        if (bitmap != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            } else {
                view.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }
    }

    // 显示图片-淡入动画
    private void displayFadeIn(View view, Bitmap bitmap) {
        Drawable[] drawable = new Drawable[] {
                new ColorDrawable(android.R.color.transparent),
                new BitmapDrawable(view.getResources(), bitmap) };

        TransitionDrawable td = new TransitionDrawable(drawable);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(td);
        } else {
            view.setBackgroundDrawable(td);
        }
        td.startTransition(300);
    }

    // /显示图片-用户自定义动画
    private void displayUserDefined(View view, Bitmap bitmap,
                                    Animation animation) {
        animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bitmap);
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        view.startAnimation(animation);
    }

}
