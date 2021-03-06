package com.ray.api.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.api.R;

/**
 * 单例tost
 *
 * @author zhangleilei
 */
public class ToastUtil {

    private Context context;
    private Toast toast;
    private TextView tv;
    private static ToastUtil instance;
    //private Handler mainHandler;

    private ToastUtil(Context context) {
        this.context = context.getApplicationContext();
        createToast();
    }

    public static ToastUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {
                    instance = new ToastUtil(context);
                }
            }
        }
        return instance;
    }

    private void createToast() {
        toast = new Toast(context);
        View v = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        tv = ((TextView) v.findViewById(R.id.toast_beautiful_tv));
        toast.setView(v);
    }

    private void show(final String message, final int length) {
        tv.setText(message);
        toast.setDuration(length);
        toast.show();
    }

    public void showToast(final String message, final int length) {
        show(message, length);
    }

    public void showToast(final String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public void showToast(final int stringId) {
        show(context.getString(stringId), Toast.LENGTH_SHORT);
    }

    public void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
