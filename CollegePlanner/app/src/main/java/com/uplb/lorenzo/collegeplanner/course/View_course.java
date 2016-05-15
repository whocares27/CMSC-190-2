package com.uplb.lorenzo.collegeplanner.course;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.course.Edit_course;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

public class View_course extends AppCompatActivity {
    private static int course_id;
    protected RelativeLayout rl;

    TextView code;
    TextView title;
    TextView type;
    TextView section;
    TextView room;
    TextView instructor;
    TextView units;
    TextView absences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_course_white_small);
        setSupportActionBar(toolbar);

        getLayoutInflater().inflate(R.layout.activity_view_course, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        course_id = i.getIntExtra("course_id", -1);

        code = (TextView) findViewById(R.id.view_code);
        title = (TextView) findViewById(R.id.view_title);
        type = (TextView) findViewById(R.id.view_type);
        section = (TextView) findViewById(R.id.view_section);
        room = (TextView) findViewById(R.id.view_room);
        instructor = (TextView) findViewById(R.id.view_instructor);
        units = (TextView) findViewById(R.id.view_units);
        absences = (TextView) findViewById(R.id.view_absences);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit_course.class);
                i.putExtra("course_id",course_id);
                startActivityForResult(i, 1);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void createView(){

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_COURSE + " WHERE " + CourseEntity.COLUMN_ID + " = " +course_id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(CourseEntity.COLUMN_ID)) != null){
            code.setText(c.getString(1));
            title.setText(c.getString(2));
            type.setText("("+c.getString(3)+")");
            if(c.getString(4).length() == 0) section.setText("(No Section)");
            else section.setText("Section "+c.getString(4));
            room.setText(c.getString(5));
            instructor.setText(c.getString(6));
            units.setText(c.getString(7)+" unit/s");
            absences.setText(c.getString(8));
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

    @Override
    public void onResume() {
        super.onResume();
        if(course_id != -1) createView();
    }
}
