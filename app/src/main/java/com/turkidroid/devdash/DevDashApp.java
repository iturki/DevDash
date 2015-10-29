package com.turkidroid.devdash;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by turki on 10/28/2015.
 */
public class DevDashApp extends Application {
    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
