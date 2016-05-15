package com.uplb.lorenzo.collegeplanner.absence;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.course.Edit_course;
import com.uplb.lorenzo.collegeplanner.entity.AbsenceEntity;
import com.uplb.lorenzo.collegeplanner.adapter.CourseAbsenceCustomAdapter;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

import java.util.ArrayList;

public class Absence extends DrawerActivity {

    private ArrayList<CourseEntity> course_list;
    CourseAbsenceCustomAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.action_absence);
        getLayoutInflater().inflate(R.layout.activity_absence_course, rl);

        lv = (ListView)findViewById(R.id.listViewAbsenceCourse);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int ID = course_list.get(position).getID();
                        Intent i = new Intent(getApplicationContext(), View_absence.class);
                        i.putExtra("course_id", ID);
                        startActivity(i);
                    }
                }
        );

        registerForContextMenu(lv);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewAbsenceCourse) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("SELECT");
            menu.add(Menu.NONE, v.getId(), 0, "EDIT ALLOWED ABSENCES");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        //  info.position will give the index of selected item

        if (item.getTitle() == "EDIT ALLOWED ABSENCES") {
            Intent i = new Intent(getApplicationContext(), Edit_course.class);
            i.putExtra("course_id", course_list.get(info.position).getID());
            startActivity(i);
        }
        return true;
    }

    public ArrayList<CourseEntity> fillCourseListView(){
        ArrayList<CourseEntity> temp = new ArrayList<CourseEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT " + CourseEntity.COLUMN_ID + ", " + CourseEntity.COLUMN_CODE + ", " + CourseEntity.COLUMN_TYPE + ", " + CourseEntity.COLUMN_ABSENCES + ", " + CourseEntity.COLUMN_CURRABSENCES + " FROM " + DatabaseHelper.TABLE_COURSE + " ORDER BY " + CourseEntity.COLUMN_ID + " DESC";

        Cursor x;
        int count;
        String countQuery;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ABSENCE + " WHERE " + AbsenceEntity.COLUMN_COURSEID + "=" + c.getInt(0);
            x = db.rawQuery(countQuery, null);
            x.moveToFirst();
            count = x.getInt(0);

            CourseEntity i = new CourseEntity(c.getString(1), c.getString(2), c.getInt(3), count);
            i.setID(c.getInt(0));
            temp.add(i);
            c.moveToNext();
            x.close();
        }

        c.close();

        db.close();
        return temp;


    }

    public void updateList(){
        course_list = fillCourseListView();
        adapter = new CourseAbsenceCustomAdapter(this, course_list);
        lv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }

}
