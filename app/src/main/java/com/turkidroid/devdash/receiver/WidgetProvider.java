package com.turkidroid.devdash.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.turkidroid.devdash.R;
import com.turkidroid.devdash.activity.MainActivity;
import com.turkidroid.devdash.service.ClickHandler;
import com.turkidroid.devdash.service.WidgetService;

import timber.log.Timber;

/**
 * Created by turki on 10/28/2015.
 */
public class WidgetProvider extends AppWidgetProvider {
    public static String EXTRA_APP_PACKAGE = "com.turkidroid.devdash.EXTRA_APP_PACKAGE";
    public static String EXTRA_ACTION = "com.turkidroid.devdash.EXTRA_ACTION";
    public static String ACTION_EDIT = "com.turkidroid.devdash.ACTION_EDIT";
    public static String ACTION_INFO = "com.turkidroid.devdash.ACTION_INFO";
    public static String ACTION_OPEN = "com.turkidroid.devdash.ACTION_OPEN";
    public static String ACTION_KILL = "com.turkidroid.devdash.ACTION_KILL";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.i("onUpdate invoked!");

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];
            Timber.i("widgetId: " + widgetId);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
            widgetView.setEmptyView(R.id.widget_list, R.id.tv_empty_view);


            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            widgetView.setRemoteAdapter(R.id.widget_list, serviceIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.widget_list);


            Intent clickIntent = new Intent(context, ClickHandler.class);
            PendingIntent pendingClickIntent1 = PendingIntent.getService(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setPendingIntentTemplate(R.id.widget_list, pendingClickIntent1);

            widgetView.setOnClickPendingIntent(R.id.widget_button, getEditIntent(context));


            appWidgetManager.updateAppWidget(appWidgetIds[i], widgetView);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private PendingIntent getEditIntent(Context context) {
        Intent editIntent = new Intent(context, MainActivity.class);
        editIntent.setAction(ACTION_EDIT);
        return PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
    private PendingIntent getPendingIntentTemplate(Context context) {
        //Intent updateIntent = new Intent(context, WidgetService.class);
        Intent updateIntent = new Intent(context, MainActivity.class);
        return  PendingIntent.getActivity(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

}