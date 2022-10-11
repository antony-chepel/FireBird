package com.app.thefirebirdoffreedom;

import android.app.Application;
import android.content.ComponentCallbacks;

public class AppsFlyer extends Application {
    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

}