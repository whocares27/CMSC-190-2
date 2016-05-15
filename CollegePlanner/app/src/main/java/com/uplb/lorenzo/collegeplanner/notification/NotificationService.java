package com.uplb.lorenzo.collegeplanner.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;

import java.util.Calendar;

public class NotificationService extends IntentService {

    private static final String TAG = "NotificationService";
    public static final String RESCHEDULE = "RESCHEDULE";
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final String CANCELALL = "CANCELALL";
    public static final String CANCELID = "CANCELID";

    AlarmManager alarmManager;

    private IntentFilter matcher;

    public NotificationService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(RESCHEDULE);
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
        matcher.addAction(CANCELALL);
        matcher.addAction(CANCELID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(matcher.matchAction(action)){
            if(CREATE.equals(action)){
                int id = intent.getIntExtra("task_id", -1);
                String task_weight = intent.getStringExtra("task_weight");
                String task_title = intent.getStringExtra("task_title");
                long task_time = intent.getLongExtra("task_time", -1);

                createNotification(id,task_weight,task_title, task_time);
            }
            else if(CANCEL.equals(action)){
                int id = intent.getIntExtra("task_id", -1);
                cancelNotification(id);
            }
            else if(RESCHEDULE.equals(action)){
                reschedNotification();
            }
        }
    }

    public void cancelNotification(int id){
        Intent myIntent = new Intent(this.getApplicationContext(), NotificationReceiver.class);
        myIntent.putExtra("task_id", id);
        myIntent.putExtra("cancel", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

    }


    public void createNotification(int id, String task_weight, String task_title, long task_time){
        Intent myIntent = new Intent(this.getApplicationContext(), NotificationReceiver.class);
        myIntent.putExtra("task_id", id);
        myIntent.putExtra("task_weight", task_weight );
        myIntent.putExtra("task_title", task_title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC, task_time, pendingIntent);


    }

    public void reschedNotification(){
        DatabaseHelper dbHandler = new DatabaseHelper(this);

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String query = "SELECT " + TaskEntity.COLUMN_ID + "," + TaskEntity.COLUMN_WEIGHT + "," + TaskEntity.COLUMN_TITLE + ","+ TaskEntity.COLUMN_NOTIFYDATE + "," + TaskEntity.COLUMN_HOUR + "," + TaskEntity.COLUMN_MINUTE + "," + TaskEntity.COLUMN_NOTIFY + " FROM " + DatabaseHelper.TABLE_TASK +" WHERE type = " + "\""+"Upcoming"+"\"" + " ORDER BY task.id DESC";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        int id;


        while (!c.isAfterLast()) {
            if(c.getInt(6) == 1) {

                String[] s = c.getString(3).split("/");

                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(s[2]), Integer.parseInt(s[0])-1, Integer.parseInt(s[1]));
                cal.set(Calendar.HOUR_OF_DAY, c.getInt(4));
                cal.set(Calendar.MINUTE, c.getInt(5));
                cal.set(Calendar.SECOND, 0);

                id = c.getInt(0);

                Intent myIntent = new Intent(this.getApplicationContext(), NotificationReceiver.class);
                myIntent.putExtra("task_id", id);
                myIntent.putExtra("task_weight", c.getString(1));
                myIntent.putExtra("task_title", c.getString(2));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);

            }
            c.moveToNext();
        }

        c.close();
        db.close();

    }


}

