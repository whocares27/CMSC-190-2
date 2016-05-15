package com.uplb.lorenzo.collegeplanner.task_widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/30/2016.
 *
 */

public class Task_widget_service extends RemoteViewsService {


    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);

    }

}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<TaskEntity> task_list;
    private int appWidgetId;

    public ListViewRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    // Initialize the data set.

    public void onCreate() {

        task_list = fillTaskListView();

    }

    public RemoteViews getViewAt(int position) {


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.task_widget_row);

        TaskEntity data= task_list.get(position);

        rv.setTextViewText(R.id.row_title, data.title);
        rv.setTextViewText(R.id.row_date, data.due_date);
        rv.setTextViewText(R.id.row_course, data.course_name);
        rv.setTextViewText(R.id.row_weight, data.weight);

        return rv;

    }

    public int getCount(){

        return task_list.size();

    }

    public void onDataSetChanged(){


    }

    public int getViewTypeCount(){

        return 1;

    }

    public long getItemId(int position) {

        return position;

    }

    public void onDestroy(){

        task_list.clear();

    }

    public boolean hasStableIds() {

        return true;

    }

    public RemoteViews getLoadingView() {

        return null;

    }

    public ArrayList<TaskEntity> fillTaskListView(){
        ArrayList<TaskEntity> temp = new ArrayList<TaskEntity>();
        DatabaseHelper dbHandler = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String query = "SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+"Upcoming"+"\"" + " ORDER BY task.id DESC";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            TaskEntity i = new TaskEntity(c.getString(1), c.getString(2), c.getString(3), c.getString(4)+" ("+ c.getString(5)+")");
            i.setID(c.getInt(0));
            temp.add(i);
            //Log.d("database", c.getString(2));
            c.moveToNext();
        }

        c.close();
        //Log.d("Database Operation", dbString);

        db.close();

        return temp;
    }

}

