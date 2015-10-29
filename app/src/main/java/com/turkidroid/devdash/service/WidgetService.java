package com.turkidroid.devdash.service;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by turki on 10/28/2015.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.i("in WidgetService -> onGetViewFactory()");
//        ArrayList<ApplicationInfo> widgetApps = new ArrayList<ApplicationInfo>();;
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        String packages = pref.getString(MainActivity.PREF_KEY_PACKAGES, "");
//        Timber.i("PREF_KEY_PACKAGES: %s", packages);
//        if(!packages.isEmpty()) {
//            Timber.i("PREF_KEY_PACKAGES not empty!");
//            widgetApps = getAppsList(packages);
//        }
        return(new DevAppsViewsFactory(this.getApplicationContext(), intent));
    }

    private ArrayList<ApplicationInfo> getAppsList(String regex) {
        ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

        String[] packages = regex.trim().replace(" ", "").replace("*", "").split(",");
        Timber.i("packages #: "+ packages.length);

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedAppa = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo applicationInfo: installedAppa) {
            for(int i=0; i<packages.length; i++) {
                if(applicationInfo.packageName.contains(packages[i])) {
                    apps.add(applicationInfo);
                }
            }
        }
        return apps;
    }
}

