package com.uplb.lorenzo.collegeplanner.task;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.adapter.TaskCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;
import com.uplb.lorenzo.collegeplanner.notification.NotificationService;

import java.util.ArrayList;


public class Late_task_fragment extends Fragment implements Task.OnFilterListener {


    private ArrayList<TaskEntity> task_list;
    TaskCustomAdapter adapter;
    ListView lv;
    String type ="Late";

    StringBuilder filter_course = new StringBuilder();
    StringBuilder filter_today = new StringBuilder();
    StringBuilder filter_date = new StringBuilder(" ORDER BY task.id DESC");
    StringBuilder filter_weight = new StringBuilder();
    StringBuilder filter_priority = new StringBuilder();


    public Late_task_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_late_task, container, false);
        lv = (ListView) v.findViewById(R.id.listViewLateTask);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int ID = task_list.get(position).getID();
                        Intent i = new Intent(getActivity(), View_task.class);
                        i.putExtra("task_id", ID);
                        startActivity(i);
                    }
                }
        );

        registerForContextMenu(lv);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewLateTask) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            menu.add(Menu.NONE, v.getId(), 0, "MARK AS DONE");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE");
            menu.add(Menu.NONE, v.getId(), 0, "DELETE ALL");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;

        if(getUserVisibleHint()) {
            //  info.position will give the index of selected item
            if (item.getTitle() == "MARK AS DONE") {
                ContentValues cv = new ContentValues();
                cv.put(TaskEntity.COLUMN_TYPE, "Completed");
                SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
                db.update(DatabaseHelper.TABLE_TASK, cv, TaskEntity.COLUMN_ID + " = " + task_list.get(pos).getID(), null);
                db.close();
                updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " + DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\"" + type + "\"" + filter_course.toString() + filter_today.toString() + filter_weight.toString() + filter_priority.toString() + filter_date.toString());
                adapter.notifyDataSetChanged();
            } else if (item.getTitle() == "DELETE") {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Are you sure you want to delete?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Home.dbHandler.deleteTask(task_list.get(pos).getID(), 0);
                                updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " + DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\"" + type + "\"" + filter_course.toString() + filter_today.toString() + filter_weight.toString() + filter_priority.toString() + filter_date.toString());
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT).show();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Are you sure you want to delete all?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Home.dbHandler.DeleteAllUpcomingTask();
                                updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " + DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\"" + type + "\"" + filter_course.toString() + filter_today.toString() + filter_weight.toString() + filter_priority.toString() + filter_date.toString());
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "All upcoming tasks deleted", Toast.LENGTH_SHORT).show();

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
        return false;
    }

    public ArrayList<TaskEntity> fillTaskListView(String query){
        ArrayList<TaskEntity> temp = new ArrayList<TaskEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        //String query = "SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+type+"\"" + " ORDER BY task.id DESC";        Cursor c = db.rawQuery(query, null);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            TaskEntity i = new TaskEntity(c.getString(1), c.getString(2), c.getString(3), c.getString(4)+" ("+ c.getString(5)+")");
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
        task_list = fillTaskListView(query);
        adapter = new TaskCustomAdapter(getActivity(), task_list);
        lv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+type+"\"" + filter_course.toString()+filter_today.toString()+filter_weight.toString()+filter_priority.toString()+filter_date.toString());
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onFilterClicked(StringBuilder filter_course, StringBuilder filter_today, StringBuilder filter_date, StringBuilder filter_weight, StringBuilder filter_priority){
        this.filter_course = filter_course;
        this.filter_today = filter_today;
        this.filter_date = filter_date;
        this.filter_weight = filter_weight;
        this.filter_priority = filter_priority;


        updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+type+"\"" + filter_course.toString()+filter_today.toString()+filter_weight.toString()+filter_priority.toString()+filter_date.toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchItem(String text) {
        if(text.equals("")){
            updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+type+"\"" + filter_course.toString()+filter_today.toString()+filter_weight.toString()+filter_priority.toString()+filter_date.toString());

            adapter.notifyDataSetChanged();
        } else {
            updateList("SELECT task.id, task.title, task.due_date, task.weight, course.code, course.type FROM " + DatabaseHelper.TABLE_TASK + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON task.course_id = course.id" + " WHERE task.type = " + "\""+type+"\"" + filter_course.toString()+filter_today.toString()+filter_weight.toString()+filter_priority.toString()+filter_date.toString());
            task_list = searchItem(text);
            adapter = new TaskCustomAdapter(getActivity(), task_list);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    public ArrayList<TaskEntity> searchItem(String itemToSearch){
        ArrayList<TaskEntity> temp = new ArrayList<>();
        for(TaskEntity i: task_list){
            if(i.title.toLowerCase().contains(itemToSearch.toLowerCase())){
                temp.add(i);
            }

        }
        return temp;

    }

}
