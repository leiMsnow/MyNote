package com.ray.api.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by dangdang on 15/6/16.
 */
public class ZoomImageView extends ImageView implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener
        , ViewTreeObserver.OnGlobalLayoutListener {

    private float mWidth;
    private float mHeight;
    //最大缩放倍数
    public static final float SCALE_MAX = 4.0f;
    public static final float SCALE_MIN = 2.0f;
    //初始化时缩放比例，如果图片宽高大于屏幕，此值将小于0
    private float mInitScale = 1.0f;
    //用于存放matrix的9个值
    private final float[] mMtrixValues = new float[9];
    //防止重复计算
    private boolean mFirstInit = true;
    //检测手势缩放
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private Matrix mScaleMatrix = new Matrix();

    private int mLastPointCount = 0;
    private float mLastX = 0;
    private float mLastY = 0;
    //是否可以拖动
    private boolean isCanDrag = false;
    private double mTouchSlop = 0.0f;
    private boolean isMoveLeftAndRight = false;
    private boolean isMoveUpAndDown = false;

    private boolean isAutoScale = false;

    private final int DELAYMILLIS = 20;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();
                if (getScale() < SCALE_MIN) {
                    postDelayed(new AutoScaleRunnable(SCALE_MIN, x, y), DELAYMILLIS);
                } else if (getScale() >= SCALE_MIN && getScale() < SCALE_MAX) {
                    postDelayed(new AutoScaleRunnable(SCALE_MAX, x, y), DELAYMILLIS);
                } else {
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), DELAYMILLIS);
                }
                isAutoScale = true;
                return true;
            }
        });
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    private final float getScale() {
        mScaleMatrix.getValues(mMtrixValues);
        return mMtrixValues[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scale = getScale();
        float scaleFactor = scaleGestureDetector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        //缩放范围控制，
        if ((scale < SCALE_MAX && scaleFactor > 1.0f) ||
                (scale > mInitScale && scaleFactor < 1.0f)) {
            //最大值，最小值判断
            if (scaleFactor * scale < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            //设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            checkTouchCenterScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mGestureDetector.onTouchEvent(motionEvent))
            return true;
        mScaleGestureDetector.onTouchEvent(motionEvent);

        float x = 0;
        float y = 0;
        final int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        //触摸发生变化时，重置lastY，lastX
        if (pointerCount != mLastPointCount) {
            mLastX = x;
            mLastY = y;
            isCanDrag = false;
        }
        mLastPointCount = pointerCount;
        RectF rectF = getMatrixRectF();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > mWidth || rectF.height() > mHeight) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > mWidth || rectF.height() > mHeight) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {

                        isMoveLeftAndRight = isMoveUpAndDown = true;
                        if (rectF.width() < mWidth) {
                            dx = 0;
                            isMoveLeftAndRight = false;
                        }
                        if (rectF.height() < mHeight) {
                            dy = 0;
                            isMoveUpAndDown = false;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkMatrixBounds();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    private void checkMatrixBounds() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        if (rectF.top > 0 && isMoveUpAndDown) {
            deltaY = -rectF.top;
        }
        if (rectF.bottom < mHeight && isMoveUpAndDown) {
            deltaY = mHeight - rectF.bottom;
        }

        if (rectF.left > 0 && isMoveLeftAndRight) {
            deltaX = -rectF.left;
        }
        if (rectF.right < mWidth && isMoveLeftAndRight) {
            deltaX = mWidth - rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    private boolean isCanDrag(float dx, float dy) {

        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (mFirstInit) {

            Drawable drawable = getDrawable();
            if (drawable == null)
                return;
            mWidth = getWidth();
            mHeight = getHeight();
            //得到图片的宽和高
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            if (dw == 0 || dh == 0) {
                return;
            }
            float scale = 1.0f;
            //如果图片的宽/高大于屏幕，缩放至屏幕大小
            if (dw > mWidth && dh <= mHeight) {
                scale = mWidth * 1.0f / dw;
            }
            if (dh > mHeight && dw <= mWidth) {
                scale = mHeight * 1.0f / dh;
            }
            //如果图片的宽高都大于屏幕，按比例缩小至屏幕大小
            if (dw > mWidth && dh > mHeight) {
                scale = Math.min(mHeight * 1.0f / dh, mWidth * 1.0f / dw);
            }
            mInitScale = scale;
            //图片移动至屏幕中心
            mScaleMatrix.postTranslate((mWidth - dw) / 2, (mHeight - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);

            mFirstInit = false;
        }
    }

    //图片缩放时候，控制中心点和图片范围
    private void checkTouchCenterScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        //如果宽/高大于屏幕，控制范围
        if (rectF.width() >= mWidth) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < mWidth) {
                deltaX = mWidth - rectF.right;
            }
        }
        if (rectF.height() >= mHeight) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            if (rectF.bottom < mHeight) {
                deltaY = mHeight - rectF.bottom;
            }
        }

        //如果宽高小于屏幕，居中至屏幕
        if (rectF.width() < mWidth) {
            deltaX = mWidth * 0.5f - rectF.right + 0.5f * rectF.width();
        }
        if (rectF.height() < mHeight) {
            deltaY = mHeight * 0.5f - rectF.bottom + 0.5f * rectF.height();
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }


    /**
     * 获取图片范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    class AutoScaleRunnable implements Runnable {

        private static final float AUTO_SCALE_UP = 1.05f;
        private static final float AUTO_SCALE_DOWN = 0.95f;
        private float mTargetScale;
        private float mTempScale;

        private float x;
        private float y;

        AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                mTempScale = AUTO_SCALE_UP;
            } else {
                mTempScale = AUTO_SCALE_DOWN;
            }
        }

        @Override
        public void run() {

            mScaleMatrix.postScale(mTempScale, mTempScale, x, y);
            checkTouchCenterScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            if ((mTempScale > 1f && currentScale < mTargetScale) ||
                    (mTempScale < 1f && currentScale > mTargetScale)) {
                postDelayed(this, DELAYMILLIS);
            } else {
                float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkTouchCenterScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }
}
