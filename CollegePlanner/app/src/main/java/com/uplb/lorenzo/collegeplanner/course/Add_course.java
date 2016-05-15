package com.uplb.lorenzo.collegeplanner.course;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class Add_course extends AppCompatActivity {
    protected RelativeLayout rl;
    AutoCompleteTextView spinner;
    NumberPicker np_units;
    NumberPicker np_absences;

    private ArrayAdapter<String> adapter;

    Button lec_btn;
    Button lab_btn;
    Button rec_btn;

    String code;
    String title;
    String section;
    String room;
    String instructor;
    String unit;
    String absence;
    String type = "Lecture";


    private ArrayList<String> instructor_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.add_course);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_add_course, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinner = (AutoCompleteTextView) findViewById(R.id.spinner_instructor);


        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse(v);
            }

        });

        lec_btn = (Button) findViewById(R.id.lecture);
        rec_btn = (Button) findViewById(R.id.recit);
        lab_btn = (Button) findViewById(R.id.lab);

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

    public void addCourse(View view) {


        code = ((EditText) findViewById(R.id.code)).getText().toString();
        title = ((EditText) findViewById(R.id.title)).getText().toString();
        section = ((EditText) findViewById(R.id.section)).getText().toString();
        room = ((EditText) findViewById(R.id.room)).getText().toString();
        instructor = ((AutoCompleteTextView) findViewById(R.id.spinner_instructor)).getText().toString();
        unit = ((EditText) findViewById(R.id.units)).getText().toString();
        absence = ((EditText) findViewById(R.id.absences)).getText().toString();

        if(validate()) {
            CourseEntity i = new CourseEntity(code, title, type, section, room, instructor, unit, Integer.parseInt(absence));
            long id = Home.dbHandler.addCourse(i);

            if (id != -1) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }

    }

    public boolean validate(){
        boolean validate = true;
        if(code.trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.code)).setError("Course code is required");
            validate = false;
        }
        if(section.trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.section)).setError("Section is required");
            validate = false;
        }
        if(absence.trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.absences)).setError("Absence is required");
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
        spinner.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }

}
