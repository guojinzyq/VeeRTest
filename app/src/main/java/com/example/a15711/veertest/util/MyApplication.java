package com.example.a15711.veertest.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by 15711 on 2018/11/16.
 * 返回一个Context
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
