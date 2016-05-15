package com.uplb.lorenzo.collegeplanner.note;

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
import com.uplb.lorenzo.collegeplanner.note.Edit_note;

public class View_note extends AppCompatActivity {
    private static int note_id;
    protected RelativeLayout rl;

    TextView title;
    TextView body;
    TextView date;
    TextView course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.action_note);
        setSupportActionBar(toolbar);

        getLayoutInflater().inflate(R.layout.activity_view_note, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        note_id = i.getIntExtra("note_id", -1);

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);
        date = (TextView) findViewById(R.id.notelist_date);
        course = (TextView) findViewById(R.id.course);



        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit_note.class);
                i.putExtra("note_id",note_id);
                startActivityForResult(i,1);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
        }
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
        if(note_id != -1) createView();
    }

    public void createView(){
        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT note.title, note.body, note.date, course.code, course.type FROM " +
                DatabaseHelper.TABLE_NOTE+ " INNER JOIN " +DatabaseHelper.TABLE_COURSE +
                " ON note.course_id = course.id WHERE note.id = " + note_id  ;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(0) != null){
            title.setText(c.getString(0));
            body.setText(c.getString(1));
            date.setText(c.getString(2));
            course.setText(c.getString(3) + " (" + c.getString(4) + ")");

        }

        c.close();


        db.close();
    }
}
