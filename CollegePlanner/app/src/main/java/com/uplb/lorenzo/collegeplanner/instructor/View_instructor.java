package com.uplb.lorenzo.collegeplanner.instructor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;
import com.uplb.lorenzo.collegeplanner.entity.MarshMallowPermission;

public class View_instructor extends AppCompatActivity {
    private static int instructor_id;
    protected RelativeLayout rl;

    TextView name;
    TextView email;
    TextView contact;
    TextView college;
    TextView dept;
    TextView room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_instructor_white_small);
        setSupportActionBar(toolbar);

        getLayoutInflater().inflate(R.layout.activity_view_instructor, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        instructor_id = i.getIntExtra("instructor_id", -1);

        name = (TextView) findViewById(R.id.view_name);
        email = (TextView) findViewById(R.id.view_email);
        contact = (TextView) findViewById(R.id.view_contact);
        college = (TextView) findViewById(R.id.view_college);
        dept = (TextView) findViewById(R.id.view_dept);
        room = (TextView) findViewById(R.id.view_room);

        if (instructor_id != -1) createView();

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit_instructor.class);
                i.putExtra("instructor_id", instructor_id);
                startActivityForResult(i, 1);
            }

        });
    }

    public void startCall(View view) {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);


        if (marshMallowPermission.checkPermissionForCallPhone()) {

            String number = "tel:" + contact.getText().toString().trim();
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            try{
                startActivity(callIntent);
            }catch (SecurityException e){

            }


        } else{
            marshMallowPermission.requestPermissionForCallPhone();
        }

    }

    public void startMessage(View view){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", contact.getText().toString().trim());
        startActivity(smsIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void createView(){

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_INSTRUCTOR + " WHERE " + InstructorEntity.COLUMN_ID + " = " +instructor_id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(InstructorEntity.COLUMN_ID)) != null){
            name.setText(c.getString(1) + " " + c.getString(2));
            email.setText(c.getString(3));
            contact.setText(c.getString(4));
            college.setText(c.getString(5));
            dept.setText(c.getString(6));
            room.setText(c.getString(7));
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
        if(instructor_id != -1) createView();
    }

    public void viewSchedule(View view){
        Intent i = new Intent(this, View_instructor_schedule.class);
        i.putExtra("instructor_id",instructor_id);
        startActivity(i);
    }
}


