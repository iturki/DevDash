package com.turkidroid.devdash.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.turkidroid.devdash.R;
import com.turkidroid.devdash.activity.MainActivity;
import com.turkidroid.devdash.receiver.WidgetProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by turki on 10/28/2015.
 */
public class DevAppsViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<ApplicationInfo> mAppsList;
    private Intent mIntent;
    final private PackageManager pm;

    public DevAppsViewsFactory(Context context, Intent intent) {
        Timber.i("Creating a DevAppsViewsFactory...");
        this.mContext = context;
        this.mIntent = intent;
        this.mAppsList = new ArrayList<ApplicationInfo>();
        pm = mContext.getPackageManager();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return(mAppsList.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.i("Requested view at: " + position);
        RemoteViews row=new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_row);

        ApplicationInfo app = mAppsList.get(position);

        row.setTextViewText(R.id.app_name, pm.getApplicationLabel(app));
        row.setTextViewText(R.id.app_package, app.packageName);

        Drawable d = pm.getApplicationIcon(app);
        row.setImageViewBitmap(R.id.app_logo, drawableToBitmap(d));


        Intent i;
        Bundle extras=new Bundle();
        extras.putString(WidgetProvider.EXTRA_APP_PACKAGE, app.packageName);

        i = new Intent();
        i.putExtras(extras);
        i.setAction(WidgetProvider.ACTION_OPEN);
        row.setOnClickFillInIntent(R.id.app_launcher, i);

        i = new Intent();
        i.putExtras(extras);
        i.setAction(WidgetProvider.ACTION_INFO);
        row.setOnClickFillInIntent(R.id.app_info, i);

        i = new Intent();
        i.putExtras(extras);
        i.setAction(WidgetProvider.ACTION_KILL);
        row.setOnClickFillInIntent(R.id.app_kill, i);


        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        Timber.i("onDataSetChanged() is invoked!");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String packages = pref.getString(MainActivity.PREF_KEY_PACKAGES, "");
        mAppsList.clear();
        mAppsList = getAppsList(packages);
        toast("Widget updated successfully!");
    }



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private ArrayList<ApplicationInfo> getAppsList(String regex) {
        ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

        String[] packages = regex.trim().replace(" ", "").replace("*", "").split(",");
        Timber.i("regex str: (%s)", regex);
        Timber.i("packages #: "+ packages.length);
        Timber.i("packages[0]: (%s)", packages[0]);
        if(packages.length == 0)
            return apps;

        final PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> installedAppa = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo applicationInfo: installedAppa) {
            for(int i=0; i<packages.length; i++) {
                if(!packages[i].equals("")) {
                    if(applicationInfo.packageName.contains(packages[i])) {
                        apps.add(applicationInfo);
                    }
                }
            }
        }
        return apps;
    }

    private void toast(String msg) {
        class OneShotTask implements Runnable {
            String str;
            OneShotTask(String s) { str = s; }
            public void run() {
                Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
            }
        }
        new Handler(Looper.getMainLooper()).post(new OneShotTask(msg));
    }


}
