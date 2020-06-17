package com.recore.sterattendancev3.Networking;

import android.app.Application;
import android.content.Context;

public class NetworkSingleton extends Application {
    private static  NetworkSingleton sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public static NetworkSingleton getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
