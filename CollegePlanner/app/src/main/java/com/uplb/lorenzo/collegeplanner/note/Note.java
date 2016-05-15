package com.uplb.lorenzo.collegeplanner.note;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.course.Add_course;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.adapter.NoteCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.NoteEntity;

import java.util.ArrayList;

public class Note extends DrawerActivity implements SearchView.OnQueryTextListener {

    ListView lv;
    Spinner spinner_course;
    private ArrayList<CourseEntity> course_list;
    private ArrayList<NoteEntity> note_list;
    private ArrayList<String> spinner_list;
    ArrayAdapter<String> adapter;
    NoteCustomAdapter note_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.action_note);
        getLayoutInflater().inflate(R.layout.activity_note, rl);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                if (Home.dbHandler.getCourseCount() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Note.this, R.style.MyAlertDialogStyle);
                    builder.setMessage("Course list is empty.\nDo you want to add now?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Note.this, Add_course.class));

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
                }else {
                    startActivityForResult(new Intent(Note.this, Add_note.class),1);
                }
            }
        });

        lv = (ListView)findViewById(R.id.listViewNote);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int ID = note_list.get(position).getID();
                        Intent i = new Intent(getApplicationContext(), View_note.class);
                        i.putExtra("note_id", ID);
                        startActivity(i);
                    }
                }
        );

        registerForContextMenu(lv);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        spinner_list = new ArrayList<String>();

        course_list = fillCourseListView();
        adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, spinner_list );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);

        spinner_course.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                            querySelect(position);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
        }
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
            int pos = spinner_course.getSelectedItemPosition();
            querySelect(pos);
            adapter.notifyDataSetChanged();
        } else {
            int pos = spinner_course.getSelectedItemPosition();
            querySelect(pos);
            note_list = searchItem(text);
            note_adapter = new NoteCustomAdapter(this, note_list);
            lv.setAdapter(note_adapter);
            note_adapter.notifyDataSetChanged();
        }

        return false;
    }

    public ArrayList<NoteEntity> searchItem(String itemToSearch){
        ArrayList<NoteEntity> temp = new ArrayList<>();
        for(NoteEntity i: note_list){
            if(i.title.toLowerCase().contains(itemToSearch.toLowerCase())){
                temp.add(i);
            }

        }
        return temp;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewNote) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            menu.add(Menu.NONE, v.getId(), 0, "EDIT");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE ALL");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        //  info.position will give the index of selected item

        if (item.getTitle() == "EDIT") {
            Intent i = new Intent(getApplicationContext(), Edit_note.class);
            i.putExtra("note_id", note_list.get(info.position).getID());
            startActivityForResult(i,2);
        }
        else if (item.getTitle() == "DELETE") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deleteNote(note_list.get(pos).getID(), 0);
                            int p = spinner_course.getSelectedItemPosition();
                            querySelect(p);
                            Toast.makeText(Note.this, "Note deleted", Toast.LENGTH_SHORT).show();


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
        } else if (item.getTitle() == "DELETE ALL") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete all?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.DeleteAllNote();
                            int p = spinner_course.getSelectedItemPosition();
                            querySelect(p);
                            Toast.makeText(Note.this, "All notes deleted", Toast.LENGTH_SHORT).show();


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

        spinner_list.clear();
        spinner_list.add("ALL");


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

    public void querySelect(int position){
        if(position==0) {
            updateList("SELECT note.id, note.title, note.body, note.date, course.code, course.type FROM " + DatabaseHelper.TABLE_NOTE + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON note.course_id = course.id" + " ORDER BY note.id DESC");
        } else {
            updateList("SELECT note.id, note.title, note.body, note.date, course.code, course.type FROM " + DatabaseHelper.TABLE_NOTE + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON note.course_id = course.id" + " WHERE note.course_id" + "=" + course_list.get(position-1).getID()+ " ORDER BY note.id DESC");
        }
        note_adapter.notifyDataSetChanged();
    }

    public ArrayList<NoteEntity> fillNoteListView(String query){
        ArrayList<NoteEntity> temp = new ArrayList<NoteEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            NoteEntity i = new NoteEntity(c.getString(1), c.getString(2), c.getString(3), c.getString(4)+" ("+ c.getString(5)+")");
            i.setID(c.getInt(0));
            temp.add(i);
            //Log.d("database", c.getString(2));
            c.moveToNext();
        }

        c.close();
        //Log.d("Database Operation", dbString);

        db.close();

        return temp;
    }


    public void updateList(String query){

        note_list = fillNoteListView(query);
        note_adapter = new NoteCustomAdapter(this, note_list);
        lv.setAdapter(note_adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        int pos = spinner_course.getSelectedItemPosition();
        querySelect(pos);
    }
}
