package com.uplb.lorenzo.collegeplanner.task;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import com.uplb.lorenzo.collegeplanner.notification.NotificationReceiver;
import com.uplb.lorenzo.collegeplanner.notification.NotificationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Add_task extends AppCompatActivity {
    RelativeLayout rl;

    private String title;
    private String description;
    private String due_date = null;
    private String notify_date;
    private int notify_hour;
    private int notify_minute;
    private int notify;
    private String weight;
    private String priority = "Low";
    private int course_id;

    int year,day,month;
    int nyear,nday,nmonth;
    Calendar cal;
    TextView due_date_tv;
    TextView notify_date_tv;
    TextView notify_time_tv;
    CheckBox notify_tv;
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
        toolbar.setLogo(R.drawable.ic_task_add_small);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_add_task, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        spinner_weight = (Spinner) findViewById(R.id.spinner_weight);
        spinner_list = new ArrayList<String>();
        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();

            }

        });

        due_date_tv = (TextView) findViewById(R.id.date_output);
        notify_date_tv = (TextView) findViewById(R.id.notify_output);
        notify_time_tv = (TextView) findViewById(R.id.time_output);
        notify_tv = (CheckBox) findViewById(R.id.notify_tv);

        final ImageButton notify_btn = (ImageButton) findViewById(R.id.btn_notify_date);
        final ImageButton time_btn = (ImageButton) findViewById(R.id.btn_time_date);
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
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

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
        cal.set(year, month, day);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                        year = Year;
                        month = monthOfYear;
                        day = dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("M'/'d'/'y");

                        cal.set(year, month, day);
                        due_date = sdf.format(cal.getTime());
                        due_date_tv.setText(due_date);

                    }


                }, year, month, day);


        dpd.show();
    }

    public void showNotifyDateDialog(View view){
        cal.set(year, month, day);

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


                }, year, month, day);


        dpd.show();
    }

    public void showTimePickerDialog(View v){
        new TimePickerDialog(this,
                t1,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
        switch (view.getId()){
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

    public void addTask(){
        title = ((EditText) findViewById(R.id.title)).getText().toString();
        description = ((EditText) findViewById(R.id.description)).getText().toString();
        weight = spinner_weight.getSelectedItem().toString();
        course_id = course_list.get(spinner_course.getSelectedItemPosition()).getID();

        if(validate()) {

            TaskEntity i = new TaskEntity(title, description, due_date, notify_date, notify_hour, notify_minute, notify, weight, priority, "Upcoming", course_id);

            long id = Home.dbHandler.addTask(i);

            if (id != -1) {

                if (notify == 1) {
                    cal.set(nyear, nmonth, nday);
                    cal.set(Calendar.HOUR_OF_DAY, notify_hour);
                    cal.set(Calendar.MINUTE, notify_minute);
                    cal.set(Calendar.SECOND, 0);

                    Intent service = new Intent(this, NotificationService.class);
                    service.putExtra("task_id", (int) id);
                    service.putExtra("task_weight", weight);
                    service.putExtra("task_title", title);
                    service.putExtra("task_time", cal.getTimeInMillis());
                    service.setAction(NotificationService.CREATE);
                    startService(service);
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    public boolean validate(){
        boolean validate = true;
        if(title.trim().equalsIgnoreCase("")){
            ((EditText) findViewById(R.id.title)).setError("Title is required");
            validate = false;
        }
        if(due_date == null){
            due_date_tv.setError("Date is required");
            validate = false;
        }
        return validate;
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

    public void updateList(){
        course_list = fillCourseListView();

        adapter = new ArrayAdapter<String>(this, R.layout.custom_day_spinner, spinner_list );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }


}
