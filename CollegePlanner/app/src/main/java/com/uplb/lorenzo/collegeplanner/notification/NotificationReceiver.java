package com.uplb.lorenzo.collegeplanner.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.util.Log;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;
import com.uplb.lorenzo.collegeplanner.task.View_task;

public class NotificationReceiver extends BroadcastReceiver {

    NotificationManager mManager;
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        int id = intent.getIntExtra("task_id", -1);
        String task_weight = intent.getStringExtra("task_weight");
        String task_title = intent.getStringExtra("task_title");
        int cancel = intent.getIntExtra("cancel", -1);

        mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if(cancel == 1) {
            mManager.cancel(id);

        }  else{

            Log.d("id", id + "");

            Intent i = new Intent(context, View_task.class);
            i.putExtra("task_id", id);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);


            Notification.Builder mBuilder =
                    new Notification.Builder(context)
                            .setSmallIcon(R.mipmap.splash_img)
                            .setContentTitle("Upcoming " + task_weight)
                            .setContentText(task_title)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{500})
                            .setContentIntent(pendingNotificationIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setWhen(System.currentTimeMillis());

            mManager.notify(id, mBuilder.build());

            ContentValues cv = new ContentValues();
            cv.put(TaskEntity.COLUMN_NOTIFY, 0);
            DatabaseHelper dbHandler = new DatabaseHelper(context);
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_TASK, cv, TaskEntity.COLUMN_ID + " = " + id, null);
            db.close();
        }

    }
}
