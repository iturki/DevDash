package com.turkidroid.devdash.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.turkidroid.devdash.receiver.WidgetProvider;

import timber.log.Timber;

/**
 * Created by turki on 10/29/2015.
 */
public class ClickHandler extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public ClickHandler() {
        super("ClickHandlerService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getStringExtra(WidgetProvider.EXTRA_APP_PACKAGE);
        Timber.i("==== ClickHandler's Intent Start (%s) ====", action);
        Timber.i("==== App Package: %s ====", packageName);

        if(action.equals(WidgetProvider.ACTION_OPEN)) {
            PackageManager packageManager = getPackageManager();
            Intent i = packageManager.getLaunchIntentForPackage(packageName);
            startActivity(i);
        }
        else if(action.equals(WidgetProvider.ACTION_INFO)) {
            Intent i = new Intent();
            i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + packageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else if(action.equals(WidgetProvider.ACTION_KILL)) {
            ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(packageName);
            toast("Killed process for app: " + packageName);
        }
    }

    private void toast(String msg) {
        class OneShotTask implements Runnable {
            String str;
            OneShotTask(String s) { str = s; }
            public void run() {
                Toast.makeText(ClickHandler.this, str, Toast.LENGTH_SHORT).show();
            }
        }
        new Handler(Looper.getMainLooper()).post(new OneShotTask(msg));
    }

}