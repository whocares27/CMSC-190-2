package com.uplb.lorenzo.collegeplanner.instructor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.uplb.lorenzo.collegeplanner.adapter.InstructorCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;

import java.util.ArrayList;

public class Instructor extends DrawerActivity implements SearchView.OnQueryTextListener {
    private ArrayList<InstructorEntity> instructor_list;
    //private DatabaseHelper dbHandler;
    InstructorCustomAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.action_instructor);
        getLayoutInflater().inflate(R.layout.activity_instructor, rl);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                startActivityForResult(new Intent(Instructor.this, Add_instructor.class),1);
            }
        });
        //dbHandler = new DatabaseHelper(this);

        lv = (ListView)findViewById(R.id.listViewInstructor);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int ID = instructor_list.get(position).getID();
                        Intent i = new Intent(getApplicationContext(), View_instructor.class);
                        i.putExtra("instructor_id", ID);
                        startActivity(i);
                    }
                }
        );

        registerForContextMenu(lv);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Instructor added", Toast.LENGTH_SHORT).show();
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
            updateList();
            adapter.notifyDataSetChanged();
        } else {
            updateList();
            instructor_list = searchItem(text);
            adapter = new InstructorCustomAdapter(this, instructor_list);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        return false;
    }

    public ArrayList<InstructorEntity> searchItem(String itemToSearch){
        ArrayList<InstructorEntity> temp = new ArrayList<>();
        for(InstructorEntity i: instructor_list){
            if(i.getFullname().toLowerCase().contains(itemToSearch.toLowerCase())){
                temp.add(i);
            }

        }
        return temp;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewInstructor) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            menu.add(Menu.NONE, v.getId(), 0, "VIEW SCHEDULE");
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
        if (item.getTitle() == "VIEW SCHEDULE") {
            Intent i = new Intent(getApplicationContext(), View_instructor_schedule.class);
            i.putExtra("instructor_id", instructor_list.get(info.position).getID());
            startActivity(i);
        } else if (item.getTitle() == "EDIT") {
            Intent i = new Intent(getApplicationContext(), Edit_instructor.class);
            i.putExtra("instructor_id", instructor_list.get(info.position).getID());
            startActivityForResult(i,2);
        } else if (item.getTitle() == "DELETE") {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deleteInstructor(instructor_list.get(pos).getID());
                            updateList();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Instructor.this, "Instructor deleted", Toast.LENGTH_SHORT).show();
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
                            Home.dbHandler.DeleteAllInstructor();
                            updateList();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Instructor.this, "All instructors deleted", Toast.LENGTH_SHORT).show();

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

    public ArrayList<InstructorEntity> fillInstructorListView(){
        ArrayList<InstructorEntity> temp = new ArrayList<InstructorEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_INSTRUCTOR + " ORDER BY " + InstructorEntity.COLUMN_ID + " DESC";


        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

            while (c.isAfterLast() == false) {
                InstructorEntity i = new InstructorEntity(c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6), c.getString(7));
                i.setID(c.getInt(0));
                temp.add(i);
                c.moveToNext();
            }

        c.close();

        db.close();
        return temp;


    }

    public void updateList(){
        instructor_list = fillInstructorListView();
        adapter = new InstructorCustomAdapter(this, instructor_list);
        lv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }

}
