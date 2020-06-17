package com.recore.sterattendancev3.Volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.recore.sterattendancev3.Networking.NetworkSingleton;

public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = getRequestQueue();
    }

    public static VolleySingleton getInstance(){
        if (sInstance == null){
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(NetworkSingleton.getAppContext());
        }
        return mRequestQueue;
    }

    public void addRequestQueue(Request request) {
        mRequestQueue.add(request);
    }
}
