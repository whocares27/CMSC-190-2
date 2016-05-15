package com.uplb.lorenzo.collegeplanner.task;

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

public class View_task extends AppCompatActivity {

    private static int task_id;
    protected RelativeLayout rl;

    TextView title;
    TextView desc;
    TextView course;
    TextView weight;
    TextView priority;
    TextView due_date;
    TextView notify_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_task_white_small);
        setSupportActionBar(toolbar);

        getLayoutInflater().inflate(R.layout.activity_view_task, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        task_id = i.getIntExtra("task_id", -1);

        title = (TextView) findViewById(R.id.view_title);
        desc = (TextView) findViewById(R.id.view_desc);
        course = (TextView) findViewById(R.id.view_course);
        weight = (TextView) findViewById(R.id.view_weight);
        priority = (TextView) findViewById(R.id.view_priority);
        due_date = (TextView) findViewById(R.id.view_due);
        notify_date = (TextView) findViewById(R.id.view_notify);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit_task.class);
                i.putExtra("task_id",task_id);
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
        String query = "SELECT task.id, task.title, task.description, task.due_date, task.notify_date, task.notify_hour, task.notify_minute, " +
                "task.weight, task.priority, course.code, course.type, task.notify FROM " +
                DatabaseHelper.TABLE_TASK+ " INNER JOIN " +DatabaseHelper.TABLE_COURSE +
                " ON task.course_id = course.id WHERE task.id = " + task_id  ;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getCount() != 0){
            title.setText(c.getString(1));
            desc.setText(c.getString(2));
            due_date.setText(c.getString(3));
            if(c.getInt(11) == 1) {
                String notify = c.getString(4) + " at " + c.getInt(5) + ":" + c.getInt(6);
                notify_date.setText(notify);
            }
            weight.setText(c.getString(7));
            priority.setText(c.getString(8));
            String crse = c.getString(9) + " (" + c.getString(10)+")";
            course.setText(crse);

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
        if(task_id != -1) createView();
    }
}
