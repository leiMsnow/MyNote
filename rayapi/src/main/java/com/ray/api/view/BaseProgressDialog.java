package com.ray.api.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.ray.api.R;

/**
 * 自定义加载框
 * <p/>
 * Created by dangdang on 15/6/15.
 */
public class BaseProgressDialog extends Dialog {


    /**
     * message view
     */
    private TextView mMessage;
    private Context mContext;

    /**
     * constructor
     *
     * @param context
     */
    public BaseProgressDialog(Context context) {
        this(context, context.getString(R.string.pd_loading), true);
    }

    /**
     * constructor 传参数的构造方法
     *
     * @param context   context
     * @param message   msg
     * @param cancalble can be canceled
     */
    public BaseProgressDialog(Context context, String message, boolean cancalble) {
        super(context, R.style.ProgressDialog);
        this.mContext = context;
        this.init(context);
        this.setMessage(message);
        this.setCancelable(cancalble);
    }

    /**
     * init 初始化控件
     *
     * @param context context
     */
    private void init(final Context context) {
        this.setContentView(R.layout.view_process_dialog);
        this.mMessage = (TextView) findViewById(R.id.progress_dialog_message);
        this.setCanceledOnTouchOutside(false);
    }

    /**
     * set message
     *
     * @param string message
     */
    public void setMessage(String string) {
        this.mMessage.setText(string);
    }

    public void setMessage(int string) {
        this.mMessage.setText(mContext.getResources().getString(string));
    }

}

