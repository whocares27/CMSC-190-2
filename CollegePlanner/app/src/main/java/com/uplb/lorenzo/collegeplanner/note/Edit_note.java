package com.uplb.lorenzo.collegeplanner.note;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;
import com.uplb.lorenzo.collegeplanner.entity.NoteEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Edit_note extends AppCompatActivity {

    protected RelativeLayout rl;
    private static int note_id;

    String title;
    String body;
    String date;
    String coursename;
    int courseid;

    EditText title_input;
    EditText body_input;
    TextView date_input;

    Spinner spinner_course;
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
        getLayoutInflater().inflate(R.layout.activity_edit_note, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        note_id = i.getIntExtra("note_id", -1);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        spinner_list = new ArrayList<String>();
        title_input = (EditText) findViewById(R.id.title);
        body_input = (EditText) findViewById(R.id.body);
        date_input = (TextView) findViewById(R.id.notelist_date);

        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);

        SimpleDateFormat formatter = new SimpleDateFormat("M'/'d'/'y");
        date = formatter.format(curDateTime);

        date_input.setText(""+date);

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }

        });

        updateList();

    }

    public void saveEdit(){
        title = title_input.getText().toString();
        body = body_input.getText().toString();
        courseid = course_list.get(spinner_course.getSelectedItemPosition()).getID();

        ContentValues cv = new ContentValues();
        cv.put(NoteEntity.COLUMN_TITLE, title);
        cv.put(NoteEntity.COLUMN_BODY, body);
        cv.put(NoteEntity.COLUMN_DATE, date);
        cv.put(NoteEntity.COLUMN_COURSEID, courseid);

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        db.update(DatabaseHelper.TABLE_NOTE, cv, NoteEntity.COLUMN_ID + " = " + note_id, null);

        db.close();
        setResult(Activity.RESULT_OK);
        finish();

    }

    public void fillOutInput(){
        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NOTE + " WHERE " + NoteEntity.COLUMN_ID + " = " +note_id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(InstructorEntity.COLUMN_ID)) != null){
            title_input.setText(c.getString(1));
            body_input.setText(c.getString(2));
            courseid = c.getInt(4);

            for(int i = 0; i < course_list.size(); i++){
                if(course_list.get(i).getID() == courseid ) {
                    spinner_course.setSelection(i);
                    break;
                }
            }

        }

        c.close();
        db.close();
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

        db.close();
        return temp;


    }

    public void updateList(){
        course_list = fillCourseListView();
        adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, spinner_list );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        if(note_id != -1){
            fillOutInput();
        }

    }

}
