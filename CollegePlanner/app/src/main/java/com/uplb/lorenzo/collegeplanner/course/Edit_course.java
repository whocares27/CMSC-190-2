package com.uplb.lorenzo.collegeplanner.course;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;

import java.util.ArrayList;

public class Edit_course extends AppCompatActivity {

    protected RelativeLayout rl;

    private String code;
    private String title;
    private String type;
    private String section;
    private String room;
    private String instructor;
    private String units;
    private int absences;

    EditText code_input;
    EditText title_input;
    EditText section_input;
    EditText room_input;
    AutoCompleteTextView instructor_input;
    EditText units_input;
    EditText absences_input;

    Button lec_btn;
    Button lab_btn;
    Button rec_btn;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> instructor_list;

    private static int course_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_edit);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_edit_course, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        course_id = i.getIntExtra("course_id", -1);

        code_input = ((EditText) findViewById(R.id.code));
        title_input = ((EditText) findViewById(R.id.title));
        section_input = ((EditText) findViewById(R.id.section));
        room_input = ((EditText) findViewById(R.id.room));
        instructor_input = ((AutoCompleteTextView) findViewById(R.id.spinner_instructor));
        units_input = ((EditText) findViewById(R.id.units));
        absences_input = ((EditText) findViewById(R.id.absences));



        lec_btn = (Button) findViewById(R.id.lecture);
        rec_btn = (Button) findViewById(R.id.recit);
        lab_btn = (Button) findViewById(R.id.lab);

        updateList();
        if(course_id != -1){
            fillOutInput();
        }

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit(v);
            }

        });

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

    public void lectureControl(View view) {
        type = "Lecture";
        rec_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        rec_btn.setTextColor(Color.parseColor("#1d6289"));
        lab_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        lab_btn.setTextColor(Color.parseColor("#1d6289"));

        lec_btn.setBackgroundColor(Color.parseColor("#1d6289"));
        lec_btn.setTextColor(Color.parseColor("#f2f2f2"));
    }

    public void recitControl(View view) {
        type = "Recitation";
        lec_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        lec_btn.setTextColor(Color.parseColor("#1d6289"));
        lab_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        lab_btn.setTextColor(Color.parseColor("#1d6289"));

        rec_btn.setBackgroundColor(Color.parseColor("#1d6289"));
        rec_btn.setTextColor(Color.parseColor("#f2f2f2"));

    }

    public void labControl(View view) {
        type = "Laboratory";
        rec_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        rec_btn.setTextColor(Color.parseColor("#1d6289"));
        lec_btn.setBackgroundColor(Color.parseColor("#f2f2f2"));
        lec_btn.setTextColor(Color.parseColor("#1d6289"));

        lab_btn.setBackgroundColor(Color.parseColor("#1d6289"));
        lab_btn.setTextColor(Color.parseColor("#f2f2f2"));
    }

    public void fillOutInput(){
        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_COURSE + " WHERE " + CourseEntity.COLUMN_ID + " = " +course_id;

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_edit_course, null, false);

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(CourseEntity.COLUMN_ID)) != null){
            code_input.setText(c.getString(1));
            title_input.setText(c.getString(2));
            section_input.setText(c.getString(4));
            room_input.setText(c.getString(5));
            instructor_input.setText(c.getString(6));
            units_input.setText(c.getString(7));
            absences_input.setText(c.getString(8));

            if(c.getString(3).equalsIgnoreCase("Lecture")) lectureControl(view);
            else if (c.getString(3).equalsIgnoreCase("Laboratory")) labControl(view);
            else if(c.getString(3).equalsIgnoreCase("Recitation")) recitControl(view);
        }

        c.close();
        db.close();
    }

    public void saveEdit(View view){

        code = code_input.getText().toString();
        title = title_input.getText().toString();
        section = section_input.getText().toString();
        room = room_input.getText().toString();
        instructor = instructor_input.getText().toString();
        units = units_input.getText().toString();
        absences = Integer.parseInt(absences_input.getText().toString());


        if(validate()) {
            ContentValues cv = new ContentValues();
            cv.put(CourseEntity.COLUMN_CODE, code);
            cv.put(CourseEntity.COLUMN_TITLE, title);
            cv.put(CourseEntity.COLUMN_TYPE, type);
            cv.put(CourseEntity.COLUMN_SECTION, section);
            cv.put(CourseEntity.COLUMN_ROOM, room);
            cv.put(CourseEntity.COLUMN_INSTRUCTOR, instructor);
            cv.put(CourseEntity.COLUMN_UNITS, units);
            cv.put(CourseEntity.COLUMN_ABSENCES, absences);

            SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_COURSE, cv, CourseEntity.COLUMN_ID + " = " + course_id, null);

            db.close();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public boolean validate(){
        boolean validate = true;
        if(code.trim().equalsIgnoreCase("")) {
            code_input.setError("Course code is required");
            validate = false;
        }
        if(section.trim().equalsIgnoreCase("")) {
            section_input.setError("Section is required");
            validate = false;
        }
        return validate;
    }

    public ArrayList<String> fillInstructorListView(){
        ArrayList<String> temp = new ArrayList<String>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT "+InstructorEntity.COLUMN_FIRSTNAME+ ", "+InstructorEntity.COLUMN_LASTNAME + " FROM " + DatabaseHelper.TABLE_INSTRUCTOR + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {

            temp.add(c.getString(0)+" "+c.getString(1));
            c.moveToNext();
        }

        c.close();

        db.close();
        return temp;


    }

    public void updateList(){
        instructor_list = fillInstructorListView();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, instructor_list);
        instructor_input.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


}
