package com.ray.api.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ray.api.R;


/**
 * 底部dialog
 *
 * @author zhangleilei
 */
public class BottomDialog extends Dialog {

    public BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BottomDialog(Context context, int theme) {
        super(context, theme);
    }

    public BottomDialog(Context context) {
        super(context);
    }

    // 先调用构造方法在调用oncreate方法
    private static boolean isShow = true;
    private static boolean mCancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        super.show();
    }

    public static class Builder {
        private Context mContext;
        private String mTopButtonText;
        private String mCenterButtonText;
        private String mBottomButtonText;

        private DialogInterface.OnClickListener mTopButtonClickListener,
                mCenterButtonClickListener, mBottomButtonClickListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTopButton(int topButtonText,
                                    DialogInterface.OnClickListener listener) {
            this.mTopButtonText = (String) mContext.getText(topButtonText);
            this.mTopButtonClickListener = listener;
            return this;
        }

        public Builder setTopButton(String buttonText,
                                    DialogInterface.OnClickListener listener) {
            this.mTopButtonText = buttonText;
            this.mTopButtonClickListener = listener;
            return this;
        }

        public Builder setCenterButton(int buttonText,
                                       DialogInterface.OnClickListener listener) {
            this.mCenterButtonText = (String) mContext.getText(buttonText);
            this.mCenterButtonClickListener = listener;
            return this;
        }

        public Builder setCenterButton(String buttonText,
                                    DialogInterface.OnClickListener listener) {
            this.mCenterButtonText = buttonText;
            this.mCenterButtonClickListener = listener;
            return this;
        }

        public Builder setBottomButton(int buttonText,
                                       DialogInterface.OnClickListener listener) {
            this.mBottomButtonText = (String) mContext.getText(buttonText);
            this.mBottomButtonClickListener = listener;
            return this;
        }

        public Builder setBottomButton(String buttonText,
                                             DialogInterface.OnClickListener listener) {
            this.mBottomButtonText = buttonText;
            this.mBottomButtonClickListener = listener;
            return this;
        }

        public boolean setCancelable(boolean cancelable) {
            isShow = cancelable;
            return isShow;
        }

        public boolean setCanceledOnTouchOutside(boolean cancel) {
            mCancel = cancel;
            return mCancel;
        }

        public BottomDialog show() {
            BottomDialog dialog = createDialog();
            dialog.show();
            initWindow(dialog);
            return dialog;
        }

        private BottomDialog createDialog() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.dialog_bottom, null);

            final BottomDialog dialog = new BottomDialog(mContext);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(isShow);

            //隐藏标题栏,必须在setContentView()前调用
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            ;

            // 设置top按钮
            TextView tvTop = (TextView) layout.findViewById(R.id.tv_top);
            if (mTopButtonText != null) {
                tvTop.setText(mTopButtonText);

                if (mTopButtonClickListener != null) {
                    tvTop.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            mTopButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                tvTop.setVisibility(View.GONE);
            }

            // 设置center按钮
            TextView tvCenter = (TextView) layout.findViewById(R.id.tv_center);
            if (mCenterButtonText != null) {
                tvCenter.setText(mCenterButtonText);
                if (mCenterButtonClickListener != null) {
                    tvCenter.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mCenterButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                tvCenter.setVisibility(View.GONE);
            }

            // 设置bottom按钮
            TextView tvBottom = (TextView) layout.findViewById(R.id.tv_bottom);
            if (mBottomButtonText != null) {
                tvBottom.setText(mBottomButtonText);
                if (mBottomButtonClickListener != null) {
                    tvBottom.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mBottomButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEUTRAL);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                tvBottom.setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }

        private void initWindow(Dialog dialog) {

            /*
             * 获取框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
             * 对象,这样这可以以同样的方式改变这个Activity的属性.
             */
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            lp.gravity = Gravity.CENTER;
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.height = WindowManager.LayoutParams.MATCH_PARENT; // 高度
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 高度
            dialogWindow.setWindowAnimations(R.style.DialogAnimationStyle);
//            lp.alpha = 0.7f; // 透明度

            // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
            dialogWindow.setAttributes(lp);
        }
    }
}

