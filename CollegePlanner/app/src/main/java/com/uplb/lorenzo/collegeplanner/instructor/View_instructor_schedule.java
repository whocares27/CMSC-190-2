package com.uplb.lorenzo.collegeplanner.instructor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.InstructorScheduleEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class View_instructor_schedule extends AppCompatActivity {
    private static int instructor_id;
    int schedule_id;
    LinkedList<Integer> id_list;

    final static int TYPE_ADD = 0;
    final static int TYPE_EDIT = 1;
    Context context;
    int height;
    int width;
    Button ref;

    HorizontalScrollView sv1;
    HorizontalScrollView sv2;
    ImageButton btn_start;
    ImageButton btn_end;

    TextView start_tv;
    TextView end_tv;

    RelativeLayout layout;
    RelativeLayout rl;

    Calendar dateAndTime=Calendar.getInstance();
    int start_hour, start_minute;
    int end_hour, end_minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nodrawer);

        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_event_white_24dp);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_instructor_schedule, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        layout = (RelativeLayout) findViewById(R.id.main_layout);

        sv1 = (HorizontalScrollView) findViewById(R.id.sv1);
        sv2 = (HorizontalScrollView) findViewById(R.id.sv2);

        sv2.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollX = sv2.getScrollX(); //for horizontalScrollView
                int scrollY = sv2.getScrollY(); //for verticalScrollView
                //DO SOMETHING WITH THE SCROLL COORDINATES
                sv1.setScrollX(scrollX);
                sv1.setScrollY(scrollY);

            }
        });

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);
        save_btn.setText("ADD");

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpAdd(v);
            }

        });

        ref = (Button) findViewById(R.id.t1);

        Intent intent = getIntent();
        instructor_id = intent.getIntExtra("instructor_id", -1);

        ref.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                //now we can retrieve the width and height
                width = ref.getWidth();
                height = ref.getHeight()/2;
                int i = 0;
                id_list = new LinkedList<Integer>();

                addStartBorder(R.id.time1,layout);
                addColumnBorder(R.id.time1,layout);
                addColumnBorder(R.id.monday1,layout);
                addColumnBorder(R.id.tuesday1,layout);
                addColumnBorder(R.id.wednesday1,layout);
                addColumnBorder(R.id.thursday1,layout);
                addColumnBorder(R.id.friday1,layout);

                addRowBorder(R.id.t1, layout);
                addRowBorder(R.id.t2, layout);
                addRowBorder(R.id.t3, layout);
                addRowBorder(R.id.t4, layout);
                addRowBorder(R.id.t5, layout);
                addRowBorder(R.id.t6, layout);
                addRowBorder(R.id.t7, layout);
                addRowBorder(R.id.t8, layout);
                addRowBorder(R.id.t9, layout);
                addRowBorder(R.id.t10, layout);
                addRowBorder(R.id.t11, layout);



                SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
                String query = "SELECT * FROM " + DatabaseHelper.TABLE_INSTRUCTOR_SCHEDULE + " WHERE " + InstructorScheduleEntity.COLUMN_INSTRUCTORID + "=" + instructor_id;


                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();

                while (c.isAfterLast() == false) {

                    int multiplier = ( (c.getInt(5) * 60 + c.getInt(6)) - (c.getInt(3) * 60 + c.getInt(4)) ) /30;

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width-2, height * multiplier + (multiplier-1));

                    Button btnTag = new Button(View_instructor_schedule.this);

                    if(c.getString(1).equalsIgnoreCase("Monday")){
                        //params.addRule(RelativeLayout.ALIGN_START, R.id.monday1);
                        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.monday1);
                    }
                    else if(c.getString(1).equalsIgnoreCase("Tuesday")){
                        //params.addRule(RelativeLayout.ALIGN_START, R.id.tuesday1);
                        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.tuesday1);
                    }
                    else if(c.getString(1).equalsIgnoreCase("Wednesday")){
                        //params.addRule(RelativeLayout.ALIGN_START, R.id.wednesday1);
                        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.wednesday1);
                    }
                    else if(c.getString(1).equalsIgnoreCase("Thursday")){
                        //params.addRule(RelativeLayout.ALIGN_START, R.id.thursday1);
                        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.thursday1);
                    }
                    else if(c.getString(1).equalsIgnoreCase("Friday")){
                        //params.addRule(RelativeLayout.ALIGN_START, R.id.friday1);
                        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.friday1);
                    }

                    if(c.getInt(3) == 7) params.addRule(RelativeLayout.BELOW, R.id.time);
                    else if(c.getInt(3) == 8) params.addRule(RelativeLayout.BELOW, R.id.t1);
                    else if(c.getInt(3) == 9) params.addRule(RelativeLayout.BELOW, R.id.t2);
                    else if(c.getInt(3) == 10) params.addRule(RelativeLayout.BELOW, R.id.t3);
                    else if(c.getInt(3) == 11) params.addRule(RelativeLayout.BELOW, R.id.t4);
                    else if(c.getInt(3) == 12) params.addRule(RelativeLayout.BELOW, R.id.t5);
                    else if(c.getInt(3) == 13) params.addRule(RelativeLayout.BELOW, R.id.t6);
                    else if(c.getInt(3) == 14) params.addRule(RelativeLayout.BELOW, R.id.t7);
                    else if(c.getInt(3) == 15) params.addRule(RelativeLayout.BELOW, R.id.t8);
                    else if(c.getInt(3) == 16) params.addRule(RelativeLayout.BELOW, R.id.t9);
                    else if(c.getInt(3) == 17) params.addRule(RelativeLayout.BELOW, R.id.t10);
                    else if(c.getInt(3) == 18) params.addRule(RelativeLayout.BELOW, R.id.t11);

                    params.setMargins(2,(height*c.getInt(4)/30),0,0);

                    btnTag.setLayoutParams(params);
                    btnTag.setText(c.getString(2));
                    btnTag.setTextSize(9);
                    btnTag.setBackgroundColor(Color.parseColor("#963019"));
                    btnTag.setTextColor(Color.WHITE);
                    btnTag.setTag(Integer.toString(i));
                    btnTag.setOnLongClickListener(btnclick);
                    registerForContextMenu(btnTag);
                    id_list.add(c.getInt(0));

                    layout.addView(btnTag);

                    i++;
                    c.moveToNext();
                }

                c.close();

                db.close();



                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    ref.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    ref.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        context = this;

    }

    private void addColumnBorder(int day, RelativeLayout layout) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(2, layout.getHeight());
        params.addRule(RelativeLayout.RIGHT_OF, day);
        View view = new View(View_instructor_schedule.this);
        view.setBackgroundColor(Color.parseColor("#1d6289"));
        view.setLayoutParams(params);
        layout.addView(view);

    }

    private void addStartBorder(int day, RelativeLayout layout) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(2, layout.getHeight());
        params.addRule(RelativeLayout.LEFT_OF, day);

        View view = new View(View_instructor_schedule.this);
        view.setBackgroundColor(Color.parseColor("#1d6289"));
        view.setLayoutParams(params);
        layout.addView(view);

    }

    private void addRowBorder(int day, RelativeLayout layout) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layout.getWidth(), 2);
        params.setMargins(0,-2,0,0);
        params.addRule(RelativeLayout.BELOW, day);
        View view = new View(View_instructor_schedule.this);
        view.setBackgroundColor(Color.parseColor("#1d6289"));
        view.setLayoutParams(params);
        layout.addView(view);

    }


    View.OnLongClickListener btnclick = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            String v = view.getTag()+"";
            schedule_id = id_list.get(Integer.parseInt(v));
            return false;
        }
    };

    public void showCustomDialog(int type){
        final int TYPE = type;
        final Dialog dialog = new Dialog(this, R.style.cust_dialog);
        dialog.setContentView(R.layout.popup);
        final Spinner sp = (Spinner) dialog.findViewById(R.id.spinner_day);
        Button btn_close = (Button) dialog.findViewById(R.id.popupClose);
        Button btn_submit = (Button) dialog.findViewById(R.id.popupSave);
        start_tv = (TextView) dialog.findViewById(R.id.start_tv);
        end_tv = (TextView) dialog.findViewById(R.id.end_tv);
        btn_start = (ImageButton) dialog.findViewById(R.id.start_time);
        btn_end = (ImageButton) dialog.findViewById(R.id.end_time);
        final EditText desc = (EditText) dialog.findViewById(R.id.desc);

        ArrayList<String> day = new ArrayList<String>();
        day.add("Monday");
        day.add("Tuesday");
        day.add("Wednesday");
        day.add("Thursday");
        day.add("Friday");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.custom_day_spinner, day );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        dialog.setTitle("Add Schedule");

        if(TYPE == TYPE_EDIT){
            dialog.setTitle("Edit Schedule");
            SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_INSTRUCTOR_SCHEDULE+ " WHERE " + InstructorScheduleEntity.COLUMN_ID + " = " + schedule_id;

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            if(c.getString(c.getColumnIndex(InstructorScheduleEntity.COLUMN_ID)) != null){
                desc.setText(c.getString(2));
                start_hour = c.getInt(3);
                start_minute = c.getInt(4);
                end_hour = c.getInt(5);
                end_minute = c.getInt(6);

                if(c.getString(1).equalsIgnoreCase("Monday")) sp.setSelection(0);
                else if(c.getString(1).equalsIgnoreCase("Tuesday")) sp.setSelection(1);
                else if(c.getString(1).equalsIgnoreCase("Wednesday")) sp.setSelection(2);
                else if(c.getString(1).equalsIgnoreCase("Thursday")) sp.setSelection(3);
                else if(c.getString(1).equalsIgnoreCase("Friday")) sp.setSelection(4);

                start_tv.setText(start_hour+ ":" +start_minute);
                end_tv.setText(end_hour+ ":" +end_minute);
                dateAndTime.set(Calendar.HOUR_OF_DAY, start_hour);
                dateAndTime.set(Calendar.MINUTE, start_minute);
            }

            c.close();
            db.close();
        }


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if( TYPE == TYPE_ADD) Add(sp.getSelectedItem().toString(), desc.getText().toString(), start_hour, start_minute, end_hour, end_minute);
                else Edit(sp.getSelectedItem().toString(), desc.getText().toString(), start_hour, start_minute, end_hour, end_minute);
                dialog.dismiss();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                new TimePickerDialog(context,
                        t1,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        false).show();
            }

        });

        btn_end.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                new TimePickerDialog(context,
                        t2,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        false).show();
            }

        });


        // show dialog on screen
        dialog.show();
    }

    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            start_hour = hourOfDay;
            start_minute = minute;

            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            start_tv.setText(start_hour+ ":" +start_minute);
            //updateLabel();
        }
    };

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            end_hour = hourOfDay;
            end_minute = minute;
            end_tv.setText(end_hour+ ":" +end_minute);
            //updateLabel();
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("SELECT");
        menu.add(Menu.NONE, v.getId(), 0, "EDIT");
        menu.add(Menu.NONE, v.getId(), 0, "DELETE");
        menu.add(Menu.NONE, v.getId(), 0, "DELETE ALL");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        if (item.getTitle() == "EDIT") {

            showCustomDialog(TYPE_EDIT);

        } else if (item.getTitle() == "DELETE") {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deleteInstructorSchedule(schedule_id, 0);
                            recreate();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();


        } else if(item.getTitle() == "DELETE ALL"){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete all?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deleteInstructorSchedule(instructor_id, 1);
                            recreate();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!


    }

    public void showPopUpAdd(View view){

        showCustomDialog(TYPE_ADD);


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


    public void Add(String day, String desc, int start_hour, int start_minute, int end_hour, int end_minute) {


        if(end_hour < start_hour || start_hour < 7 || start_hour >= 19 || end_hour > 19) {
            Toast.makeText(context,"Unsuccessful to add!", Toast.LENGTH_LONG ).show();
            return;
        }

        if(end_hour == 19 && end_minute > 30) {
            Toast.makeText(context,"Unsuccessful to add!", Toast.LENGTH_LONG ).show();
            return;
        }


        InstructorScheduleEntity i = new InstructorScheduleEntity(day, desc, start_hour, start_minute, end_hour, end_minute, instructor_id);
        long id = Home.dbHandler.addSchedule(i);

        if(id != -1){
            Toast.makeText(context,"Successfully Added!", Toast.LENGTH_LONG ).show();
            recreate();
        }

        //Toast.makeText(context,name+ " "+start_hour+ " " + end_hour, Toast.LENGTH_LONG ).show();
    }


    public void Edit(String day, String desc, int start_hour, int start_minute, int end_hour, int end_minute) {

        if(end_hour < start_hour || start_hour < 7 || start_hour >= 19 || end_hour > 19) {
            Toast.makeText(context,"Unsuccessful to edit!", Toast.LENGTH_LONG ).show();
            return;
        }

        if(end_hour == 19 && end_minute > 30) {
            Toast.makeText(context,"Unsuccessful to edit!", Toast.LENGTH_LONG ).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(InstructorScheduleEntity.COLUMN_DAY, day);
        cv.put(InstructorScheduleEntity.COLUMN_DESC, desc);
        cv.put(InstructorScheduleEntity.COLUMN_STARTHOUR, start_hour);
        cv.put(InstructorScheduleEntity.COLUMN_STARTMINUTE, start_minute);
        cv.put(InstructorScheduleEntity.COLUMN_ENDHOUR, end_hour);
        cv.put(InstructorScheduleEntity.COLUMN_ENDMINUTE, end_minute);
        cv.put(InstructorScheduleEntity.COLUMN_INSTRUCTORID, instructor_id);

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        long id = db.update(DatabaseHelper.TABLE_INSTRUCTOR_SCHEDULE, cv, InstructorScheduleEntity.COLUMN_ID + " = " + schedule_id, null);

        db.close();

        if(id != -1){
            Toast.makeText(context,"Successfully Edited!", Toast.LENGTH_LONG ).show();
            recreate();
        }

    }
}

class CustomTimePickerDialog extends TimePickerDialog {

    private final static int TIME_PICKER_INTERVAL = 30;
    private TimePicker timePicker;
    private final OnTimeSetListener callback;

    private int lastHour = -1;
    private int lastMinute = -1;

    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL,
                is24HourView);
        lastHour = hourOfDay;
        lastMinute = minute;
        this.callback = callBack;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (callback != null && timePicker != null) {
            timePicker.clearFocus();
            callback.onTimeSet(timePicker, timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
        }
    }

    @Override
    protected void onStop() {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker.findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);
        if (lastHour != hourOfDay && lastMinute != minute) {
            view.setCurrentHour(lastHour);
            lastMinute = minute;
        } else {
            lastHour = hourOfDay;
            lastMinute = minute;
        }
    }
}

