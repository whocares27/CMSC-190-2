package com.uplb.lorenzo.collegeplanner.instructor;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;

public class Add_instructor extends AppCompatActivity {

    protected RelativeLayout rl;

    private String fname;
    private String lname;
    private String email;
    private String contact;
    private String college;
    private String dept;
    private String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.add_instructor);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.activity_add_instructor, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInstructor(v);
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

    public void addInstructor(View view){

        fname = ((EditText) findViewById(R.id.fname)).getText().toString();
        lname = ((EditText) findViewById(R.id.lname)).getText().toString();
        email = ((EditText) findViewById(R.id.email)).getText().toString();
        contact = ((EditText) findViewById(R.id.contact)).getText().toString();
        college = ((EditText) findViewById(R.id.college)).getText().toString();
        dept = ((EditText) findViewById(R.id.department)).getText().toString();
        room = ((EditText) findViewById(R.id.room)).getText().toString();


        if(validate()) {
            InstructorEntity i = new InstructorEntity(fname, lname, email, contact, college, dept, room);
            long id = Home.dbHandler.addInstructor(i);

            if (id != -1) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    public boolean validate(){
        boolean validate = true;
        if(fname.trim().equalsIgnoreCase("")){
            ((EditText) findViewById(R.id.fname)).setError("First name is required");
            validate = false;
        }
        if(lname.trim().equalsIgnoreCase("")){
            ((EditText) findViewById(R.id.lname)).setError("Last name is required");
            validate = false;
        }
        if(college.trim().equalsIgnoreCase("")){
            ((EditText) findViewById(R.id.college)).setError("College is required");
            validate = false;
        }
        return validate;
    }

}
