package com.uplb.lorenzo.collegeplanner.task_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.task.Task;

/**
 * Created by lorenzo on 4/30/2016.
 */
public class Task_widget_provider extends AppWidgetProvider {

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";


    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {

            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context,Task_widget_provider.class));

            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewUpcomingTask);


        }

        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {



        for (int i = 0; i < appWidgetIds.length; i++) {


            Intent intent = new Intent(context, Task_widget_service.class);


            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_task);


            rv.setRemoteAdapter(appWidgetIds[i], R.id.listViewUpcomingTask, intent);

            rv.setEmptyView(R.id.listViewUpcomingTask, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
}


