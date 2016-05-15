package com.uplb.lorenzo.collegeplanner.instructor;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;

public class Edit_instructor extends AppCompatActivity {

    protected RelativeLayout rl;

    private String fname;
    private String lname;
    private String email;
    private String contact;
    private String college;
    private String dept;
    private String room;

    EditText fname_input;
    EditText lname_input;
    EditText email_input;
    EditText contact_input;
    EditText college_input;
    EditText dept_input;
    EditText room_input;

    private static int instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_nodrawer);

        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_edit);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_edit_instructor, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        instructor_id = i.getIntExtra("instructor_id", -1);

        fname_input = ((EditText) findViewById(R.id.fname));
        lname_input = ((EditText) findViewById(R.id.lname));
        email_input = ((EditText) findViewById(R.id.email));
        contact_input = ((EditText) findViewById(R.id.contact));
        college_input = ((EditText) findViewById(R.id.college));
        dept_input = ((EditText) findViewById(R.id.department));
        room_input = ((EditText) findViewById(R.id.room));

        if(instructor_id != -1){
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

    public void fillOutInput(){
        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_INSTRUCTOR + " WHERE " + InstructorEntity.COLUMN_ID + " = " +instructor_id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(InstructorEntity.COLUMN_ID)) != null){
            fname_input.setText(c.getString(1));
            lname_input.setText(c.getString(2));
            email_input.setText(c.getString(3));
            contact_input.setText(c.getString(4));
            college_input.setText(c.getString(5));
            dept_input.setText(c.getString(6));
            room_input.setText(c.getString(7));
        }

        c.close();
        db.close();
    }

    public void saveEdit(View view){

        fname = fname_input.getText().toString();
        lname = lname_input.getText().toString();
        email = email_input.getText().toString();
        contact = contact_input.getText().toString();
        college = college_input.getText().toString();
        dept = dept_input.getText().toString();
        room = room_input.getText().toString();

        if(validate()) {

            ContentValues cv = new ContentValues();
            cv.put(InstructorEntity.COLUMN_FIRSTNAME, fname);
            cv.put(InstructorEntity.COLUMN_LASTNAME, lname);
            cv.put(InstructorEntity.COLUMN_EMAIL, email);
            cv.put(InstructorEntity.COLUMN_CONTACT, contact);
            cv.put(InstructorEntity.COLUMN_COLLEGE, college);
            cv.put(InstructorEntity.COLUMN_DEPT, dept);
            cv.put(InstructorEntity.COLUMN_ROOM, room);

            SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_INSTRUCTOR, cv, InstructorEntity.COLUMN_ID + " = " + instructor_id, null);

            db.close();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public boolean validate(){
        boolean validate = true;
        if(fname.trim().equalsIgnoreCase("")){
            fname_input.setError("First name is required");
            validate = false;
        }
        if(lname.trim().equalsIgnoreCase("")){
            lname_input.setError("Last name is required");
            validate = false;
        }
        if(college.trim().equalsIgnoreCase("")){
            college_input.setError("College is required");
            validate = false;
        }
        return validate;
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
}
