package com.xzh.pictrueselect;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication mApplication=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
    }

    public static MyApplication getContext() {
        return mApplication;
    }
}
