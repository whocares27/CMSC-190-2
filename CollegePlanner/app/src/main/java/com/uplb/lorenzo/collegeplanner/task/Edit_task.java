package com.uplb.lorenzo.collegeplanner.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;
import com.uplb.lorenzo.collegeplanner.notification.NotificationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Edit_task extends AppCompatActivity {

    RelativeLayout rl;
    private static int task_id;

    private String title;
    private String description;
    private String due_date;
    private String notify_date;
    private int notify_hour;
    private int notify_minute;
    private int notify;
    private String weight;
    private String priority;
    private int course_id;
    private String type;

    int year,day,month;
    int nyear,nday,nmonth;
    Calendar cal;
    TextView due_date_tv;
    TextView notify_date_tv;
    TextView notify_time_tv;
    CheckBox notify_tv;

    ImageButton notify_btn;
    ImageButton time_btn;

    Button low;
    Button high;

    Spinner spinner_course;
    Spinner spinner_weight;
    private ArrayList<CourseEntity> course_list;
    private ArrayList<String> spinner_list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_edit);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_edit_task, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        spinner_weight = (Spinner) findViewById(R.id.spinner_weight);
        spinner_list = new ArrayList<String>();
        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        task_id = i.getIntExtra("task_id", -1);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }

        });

        due_date_tv = (TextView) findViewById(R.id.date_output);
        notify_date_tv = (TextView) findViewById(R.id.notify_output);
        notify_time_tv = (TextView) findViewById(R.id.time_output);
        notify_tv = (CheckBox) findViewById(R.id.notify_tv);

        notify_btn = (ImageButton) findViewById(R.id.btn_notify_date);
        time_btn = (ImageButton) findViewById(R.id.btn_time_date);
        notify_btn.setEnabled(false); time_btn.setEnabled(false);

        notify_tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    notify = 1;
                    notify_btn.setEnabled(true);
                    time_btn.setEnabled(true);
                }
                else
                {
                    notify = 0;
                    notify_btn.setEnabled(false);
                    time_btn.setEnabled(false);
                }

            }
        });

        low = (Button) findViewById(R.id.btn_low);
        high = (Button) findViewById(R.id.btn_high);

        cal = Calendar.getInstance();
        nyear = year = cal.get(Calendar.YEAR);
        nmonth = month = cal.get(Calendar.MONTH);
        nday = day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDueDateDialog(View view){

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                        year = Year;
                        month = monthOfYear;
                        day = dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("M'/'d'/'y");
                        Calendar calendar = Calendar.getInstance();
                        cal.set(year, month, day);
                        due_date = sdf.format(cal.getTime());
                        due_date_tv.setText(due_date);

                    }


                }, year, month, day);


        dpd.show();
    }

    public void showNotifyDateDialog(View view){

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                        nyear = Year;
                        nmonth = monthOfYear;
                        nday = dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("M'/'d'/'y");

                        cal.set(nyear, nmonth, nday);
                        notify_date = sdf.format(cal.getTime());
                        notify_date_tv.setText(notify_date);

                    }


                }, nyear, nmonth, nday);


        dpd.show();
    }

    public void showTimePickerDialog(View v){
        new TimePickerDialog(this,
                t1,
                notify_hour,
                notify_minute,
                false).show();
    }

    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            notify_hour = hourOfDay;
            notify_minute = minute;

            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            notify_time_tv.setText(notify_hour+ ":" +notify_minute);
            //updateLabel();
        }
    };

    public void priorityControl(View view) {
        switch (view.getId()) {
            case R.id.btn_low:
                priority = "Low";
                low.setBackgroundColor(Color.parseColor("#1d6289"));
                low.setTextColor(Color.parseColor("#f0f0f0"));

                high.setBackgroundColor(Color.parseColor("#f0f0f0"));
                high.setTextColor(Color.parseColor("#1d6289"));
                break;

            case R.id.btn_high:
                priority = "High";
                high.setBackgroundColor(Color.parseColor("#1d6289"));
                high.setTextColor(Color.parseColor("#f0f0f0"));

                low.setBackgroundColor(Color.parseColor("#f0f0f0"));
                low.setTextColor(Color.parseColor("#1d6289"));
                break;

        }
    }

    public void saveEdit(){
        title = ((EditText) findViewById(R.id.title)).getText().toString();
        description = ((EditText) findViewById(R.id.description)).getText().toString();
        weight = spinner_weight.getSelectedItem().toString();
        course_id = course_list.get(spinner_course.getSelectedItemPosition()).getID();

        ContentValues cv = new ContentValues();

        cv.put(TaskEntity.COLUMN_TITLE, title);
        cv.put(TaskEntity.COLUMN_DESC, description);
        cv.put(TaskEntity.COLUMN_DUEDATE, due_date);
        cv.put(TaskEntity.COLUMN_NOTIFYDATE, notify_date);
        cv.put(TaskEntity.COLUMN_HOUR, notify_hour);
        cv.put(TaskEntity.COLUMN_MINUTE, notify_minute);
        cv.put(TaskEntity.COLUMN_NOTIFY, notify);
        cv.put(TaskEntity.COLUMN_WEIGHT, weight);
        cv.put(TaskEntity.COLUMN_PRIORITY, priority);
        cv.put(TaskEntity.COLUMN_TYPE, type);
        cv.put(TaskEntity.COLUMN_COURSEID, course_id);

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        db.update(DatabaseHelper.TABLE_TASK, cv, TaskEntity.COLUMN_ID + " = " + task_id, null);
        db.close();

        if(notify == 1) {
            cal.set(nyear, nmonth, nday);
            cal.set(Calendar.HOUR_OF_DAY, notify_hour);
            cal.set(Calendar.MINUTE, notify_minute);
            cal.set(Calendar.SECOND, 0);


            Intent service = new Intent(this, NotificationService.class);
            service.putExtra("task_id", task_id);
            service.putExtra("task_weight", weight);
            service.putExtra("task_title", title);
            service.putExtra("task_time", cal.getTimeInMillis());
            service.setAction(NotificationService.CREATE);
            startService(service);
        }
        else {
            Intent service = new Intent(this, NotificationService.class);
            service.putExtra("task_id", task_id);
            service.setAction(NotificationService.CANCEL);
            startService(service);
        }
        setResult(Activity.RESULT_OK);
        finish();
    }

    public ArrayList<CourseEntity> fillCourseListView(){
        ArrayList<CourseEntity> temp = new ArrayList<CourseEntity>();

        spinner_list.clear();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT " + CourseEntity.COLUMN_ID + ", " + CourseEntity.COLUMN_CODE + ", " + CourseEntity.COLUMN_SECTION + ", " + CourseEntity.COLUMN_TYPE + " FROM " + DatabaseHelper.TABLE_COURSE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {

            CourseEntity i = new CourseEntity(c.getString(1), c.getString(2), c.getString(3));
            i.setID(c.getInt(0));
            temp.add(i);
            spinner_list.add(c.getString(1) + " (" + c.getString(3) + ")");
            c.moveToNext();
        }

        c.close();
        //Log.d("Database Operation", dbString);

        db.close();
        return temp;


    }

    public void fillOutInput(){
        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_ID + " = " +task_id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(0) != null){
            ((EditText) findViewById(R.id.title)).setText(c.getString(1));
            ((EditText) findViewById(R.id.description)).setText(c.getString(2));
            due_date_tv.setText(c.getString(3)); due_date = c.getString(3);
            if(c.getString(3) != null) {
                String[] x = c.getString(3).split("/");
                month = Integer.parseInt(x[0]) - 1;
                day = Integer.parseInt(x[1]);
                year = Integer.parseInt(x[2]);
            }

            if(c.getInt(7) == 1) {
                notify_date_tv.setText(c.getString(4));
                notify_date = c.getString(4);
                String[] s = c.getString(4).split("/");
                nmonth = Integer.parseInt(s[0])-1;
                nday = Integer.parseInt(s[1]);
                nyear = Integer.parseInt(s[2]);
                notify_hour = c.getInt(5);
                notify_minute = c.getInt(6);
                notify_time_tv.setText(notify_hour + ":" + notify_minute);
                notify_tv.setChecked(true);
                notify_btn.setEnabled(true);
                time_btn.setEnabled(true);

            }

            notify = c.getInt(7);

            for(int i = 0; i < spinner_weight.getCount(); i++){
                if(spinner_weight.getItemAtPosition(i).toString().equalsIgnoreCase(c.getString(8)) ) {
                    spinner_weight.setSelection(i);
                    break;
                }
            }

            if(c.getString(9).equalsIgnoreCase("High")) priorityControl(findViewById(R.id.btn_high));
            else if(c.getString(9).equalsIgnoreCase("Low")) priorityControl(findViewById(R.id.btn_low));

            type = c.getString(10);

            for(int i = 0; i < course_list.size(); i++){
                if(course_list.get(i).getID() == c.getInt(11) ) {
                    spinner_course.setSelection(i);
                    break;
                }
            }
        }

        c.close();
        db.close();
    }

    public void updateList(){
        course_list = fillCourseListView();

        adapter = new ArrayAdapter<String>(this, R.layout.custom_day_spinner, spinner_list );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);

        if(task_id != -1){
            fillOutInput();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }
}
