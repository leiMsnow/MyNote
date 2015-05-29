package com.ray.api;

import android.app.Application;

import com.ray.api.handler.MCrashHandler;

/**
 * Created by dangdang on 15/5/18.
 */
public class RayApplication extends Application {

    @Override
    public void onCreate() {
        MCrashHandler crashHandler = MCrashHandler.getInstance();
        crashHandler.init(this);
        super.onCreate();
    }
}
