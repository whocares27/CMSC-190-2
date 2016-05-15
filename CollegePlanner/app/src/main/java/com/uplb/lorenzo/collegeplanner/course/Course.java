package com.uplb.lorenzo.collegeplanner.course;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.adapter.CourseCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

import java.util.ArrayList;

public class Course extends DrawerActivity implements SearchView.OnQueryTextListener {
    private ArrayList<CourseEntity> course_list;
    CourseCustomAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.action_course);
        getLayoutInflater().inflate(R.layout.activity_course, rl);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_course);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                startActivityForResult(new Intent(Course.this, Add_course.class), 1);
            }
        });

        lv = (ListView)findViewById(R.id.listViewCourse);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int ID = course_list.get(position).getID();
                        Intent i = new Intent(getApplicationContext(), View_course.class);
                        i.putExtra("course_id", ID);
                        startActivity(i);
                    }
                }
        );


        registerForContextMenu(lv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.list_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public boolean onQueryTextChange(String text){
        if(text.equals("")){
            updateList();
            adapter.notifyDataSetChanged();
        } else {
            updateList();
            course_list = searchItem(text);
            adapter = new CourseCustomAdapter(this, course_list);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        return false;
    }

    public ArrayList<CourseEntity> searchItem(String itemToSearch){
        ArrayList<CourseEntity> temp = new ArrayList<>();
        for(CourseEntity i: course_list){
            if(i.code.toLowerCase().contains(itemToSearch.toLowerCase())){
                temp.add(i);
            }

        }
        return temp;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewCourse) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            //menu.setHeaderIcon(R.drawable.ic_dropdown);
            menu.add(Menu.NONE, v.getId(), 0, "EDIT");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE ALL");

        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        //  info.position will give the index of selected item

        if (item.getTitle() == "EDIT") {
            Intent i = new Intent(getApplicationContext(), Edit_course.class);
            i.putExtra("course_id", course_list.get(info.position).getID());
            startActivityForResult(i, 2);
        } else if (item.getTitle() == "DELETE") {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deleteCourse(course_list.get(pos).getID());
                            updateList();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Course.this, "Course deleted", Toast.LENGTH_SHORT).show();

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
        } else if(item.getTitle() == "DELETE ALL"){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.DeleteAllCourse();
                            updateList();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Course.this, "All courses deleted", Toast.LENGTH_SHORT).show();

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



    public ArrayList<CourseEntity> fillCourseListView(){
        ArrayList<CourseEntity> temp = new ArrayList<CourseEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT " + CourseEntity.COLUMN_ID + ", " + CourseEntity.COLUMN_CODE + ", " + CourseEntity.COLUMN_TYPE  + ", " + CourseEntity.COLUMN_SECTION + " FROM " + DatabaseHelper.TABLE_COURSE + " ORDER BY " + CourseEntity.COLUMN_ID + " DESC";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            CourseEntity i = new CourseEntity(c.getString(1), c.getString(2), c.getString(3));
            i.setID(c.getInt(0));
            temp.add(i);
            c.moveToNext();
        }

        c.close();
        //Log.d("Database Operation", dbString);

        db.close();
        return temp;


    }

    public void updateList(){
        course_list = fillCourseListView();
        adapter = new CourseCustomAdapter(this, course_list);
        lv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }


}
