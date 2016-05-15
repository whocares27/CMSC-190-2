package com.uplb.lorenzo.collegeplanner.absence;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.adapter.AbsenceCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.AbsenceEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class View_absence extends AppCompatActivity {
    ArrayList<AbsenceEntity> absence_list;
    AbsenceCustomAdapter adapter;
    private static int course_id;
    protected RelativeLayout rl;
    private int DIALOG_ID = 0;
    String date;
    TextView date_output;
    int year,day,month;

    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_nodrawer);
        rl = (RelativeLayout) findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_absent_white_small);
        setSupportActionBar(toolbar);

        getLayoutInflater().inflate(R.layout.activity_view_absence, rl);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        course_id = i.getIntExtra("course_id", -1);

        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        date_output = (TextView) findViewById(R.id.date_output);
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy ccc");
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(year, month, day);
        //date = sdf.format(calendar.getTime());
        //date_btn.setText(date);
        lv = (ListView) findViewById(R.id.listViewAbsence);

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setVisibility(View.VISIBLE);
        save_btn.setText("ADD");

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAbsence();
                updateList();
                adapter.notifyDataSetChanged();
            }

        });

        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewAbsence) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE ALL");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        if (item.getTitle() == "DELETE ALL") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete all?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.DeleteAllAbsence(course_id);
                            updateList();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(View_absence.this, "All absences deleted", Toast.LENGTH_SHORT).show();
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



    public void showDatePicker(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                        year = Year;
                        month = monthOfYear;
                        day = dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy ccc");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        date = sdf.format(calendar.getTime());
                        date_output.setText(date);

                    }


                }, year, month, day);


        dpd.show();
    }

    public void addAbsence(){
        if(date == null){
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
        } else {
            AbsenceEntity i = new AbsenceEntity(date, course_id);
            long id = Home.dbHandler.addAbsence(i);
            Toast.makeText(this, "Absence added", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAbsence(View view){
        View v = (View) view.getParent();
        final int pos = lv.getPositionForView(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage("Are you sure you want to delete?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Home.dbHandler.deleteAbsence(absence_list.get(pos).getID(), 0);
                            updateList();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(View_absence.this, "Absence deleted", Toast.LENGTH_SHORT).show();
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

    public ArrayList<AbsenceEntity> fillAbsenceListView(){
        ArrayList<AbsenceEntity> temp = new ArrayList<AbsenceEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ABSENCE + " WHERE " + AbsenceEntity.COLUMN_COURSEID + "=" +course_id + " ORDER BY "+ AbsenceEntity.COLUMN_ID + " DESC";


        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            AbsenceEntity i = new AbsenceEntity(c.getString(1),c.getInt(2));
            i.setID(c.getInt(0));
            temp.add(i);
            c.moveToNext();
        }

        c.close();

        db.close();
        return temp;


    }

    public void updateList(){
        absence_list = fillAbsenceListView();
        adapter = new AbsenceCustomAdapter(this, absence_list);
        lv.setAdapter(adapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }
}
