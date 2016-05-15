package com.uplb.lorenzo.collegeplanner.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.course.Add_course;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Task extends DrawerActivity implements SearchView.OnQueryTextListener {
    private Upcoming_task_fragment upcoming_task_fragment;
    private Late_task_fragment late_task_fragment;
    private Completed_task_fragment completed_task_fragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Dialog dialog;
    private Spinner spinner;
    private Spinner spinner_weight;
    private ArrayList<CourseEntity> course_list;
    private ArrayList<String> dialog_spinner_list;
    String priority = "Low";

    StringBuilder filter_course = new StringBuilder();
    StringBuilder filter_today = new StringBuilder();
    StringBuilder filter_date = new StringBuilder();
    StringBuilder filter_weight = new StringBuilder();
    StringBuilder filter_priority = new StringBuilder();

    CheckBox course_cb;
    CheckBox today_cb;
    CheckBox date_cb;
    CheckBox weight_cb;
    CheckBox priority_cb;

    Button filter_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_task_drawer);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.ic_task_white_small);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter_btn = (Button) findViewById(R.id.filter_btn);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Home.dbHandler.getCourseCount() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Task.this, R.style.MyAlertDialogStyle);
                    builder.setMessage("Course list is empty.\nDo you want to add now?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Task.this, Add_course.class));

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
                    startActivityForResult(new Intent(Task.this, Add_task.class),1);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);

        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(true);
                searchView.setIconified(true);
                filter_btn.setVisibility(View.VISIBLE);
            }
        });

        View searchButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                filter_btn.setVisibility(View.GONE);
            }
        });;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public boolean onQueryTextChange(String text){
        if(viewPager.getCurrentItem() == 0) {
            upcoming_task_fragment.onSearchItem(text);
        }

        if(viewPager.getCurrentItem() == 1) {
            late_task_fragment.onSearchItem(text);
        }

        if(viewPager.getCurrentItem() == 2) {
            completed_task_fragment.onSearchItem(text);
        }

        return false;
    }




    private void setupViewPager(ViewPager viewPager) {
        upcoming_task_fragment = new Upcoming_task_fragment();
        late_task_fragment = new Late_task_fragment();
        completed_task_fragment = new Completed_task_fragment();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment( upcoming_task_fragment, "Upcoming");
        adapter.addFragment(late_task_fragment, "Late");
        adapter.addFragment(completed_task_fragment, "Completed");

        viewPager.setAdapter(adapter);

        createFilterDialog();


    }

    public void createFilterDialog(){
        dialog = new Dialog(this, R.style.cust_dialog);
        dialog.setContentView(R.layout.filter_dialog_box);
        dialog.setTitle("Filter By");
        dialog_spinner_list = new ArrayList<String>();
        course_list = fillCourseListView();

        course_cb = (CheckBox) dialog.findViewById(R.id.course_cb);
        today_cb = (CheckBox) dialog.findViewById(R.id.today_cb);
        date_cb = (CheckBox) dialog.findViewById(R.id.date_cb);
        weight_cb = (CheckBox) dialog.findViewById(R.id.weight_cb);
        priority_cb = (CheckBox) dialog.findViewById(R.id.priority_cb);

        final Button btn_low = (Button) dialog.findViewById(R.id.btn_low);
        final Button btn_high = (Button) dialog.findViewById(R.id.btn_high);

        filter_btn = (Button) dialog.findViewById(R.id.filter_btn);
        final Button close_btn = (Button) dialog.findViewById(R.id.close_btn);
        final Button clear_btn = (Button) dialog.findViewById(R.id.clear_btn);

        spinner_weight = (Spinner) dialog.findViewById(R.id.spinner_weight_dialog);
        spinner = (Spinner) dialog.findViewById(R.id.spinner_course_dialog);
        ArrayAdapter<String> adptr = new ArrayAdapter<String>(this, R.layout.custom_filter_spinner, dialog_spinner_list );
        spinner.setAdapter(adptr);



        btn_low.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                priority = "Low";
                btn_low.setBackgroundColor(Color.parseColor("#1d6289"));
                btn_low.setTextColor(Color.parseColor("#f0f0f0"));

                btn_high.setBackgroundColor(Color.parseColor("#f0f0f0"));
                btn_high.setTextColor(Color.parseColor("#1d6289"));

            }
        });
        btn_high.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                priority = "High";
                btn_high.setBackgroundColor(Color.parseColor("#1d6289"));
                btn_high.setTextColor(Color.parseColor("#f0f0f0"));

                btn_low.setBackgroundColor(Color.parseColor("#f0f0f0"));
                btn_low.setTextColor(Color.parseColor("#1d6289"));
            }
        });

        today_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    date_cb.setChecked(false);
                }

            }
        });

        date_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    today_cb.setChecked(false);
                }

            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.cancel();
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                course_cb.setChecked(false);
                today_cb.setChecked(false);
                date_cb.setChecked(false);
                weight_cb.setChecked(false);
                priority_cb.setChecked(false);
            }
        });

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                    onFilter();
                dialog.cancel();
                            }
        });



    }

    public void onFilter(){
        filter_course.setLength(0);
        filter_today.setLength(0);
        filter_date.setLength(0);
        filter_weight.setLength(0);
        filter_priority.setLength(0);

        if(course_cb.isChecked()){
            filter_course.append("AND task.course_id = " + course_list.get(spinner.getSelectedItemPosition()).getID());
        }
        if(today_cb.isChecked()){
            long date = System.currentTimeMillis();
            SimpleDateFormat sd = new SimpleDateFormat("M'/'d'/'y");
            String today = sd.format(date);
            filter_today.append(" AND task.due_date = "+ "\""+ today + "\"");
        }
        if(date_cb.isChecked()){
            filter_date.append(" ORDER BY task.due_date ASC");
        }
        if(!date_cb.isChecked() && !today_cb.isChecked()){
            filter_date.append(" ORDER BY task.id DESC");
        }
        if(weight_cb.isChecked()){
            filter_weight.append(" AND task.weight = " + "\""+ spinner_weight.getSelectedItem().toString()+"\"");
        }
        if(priority_cb.isChecked()){
            filter_priority.append(" AND task.priority = " + "\""+ priority +"\"");
        }

        if(viewPager.getCurrentItem() == 0) {
            upcoming_task_fragment.onFilterClicked(filter_course, filter_today, filter_date, filter_weight, filter_priority);
        }

        if(viewPager.getCurrentItem() == 2) {
            completed_task_fragment.onFilterClicked(filter_course, filter_today, filter_date, filter_weight, filter_priority);
        }

        if(viewPager.getCurrentItem() == 1) {
            late_task_fragment.onFilterClicked(filter_course, filter_today, filter_date, filter_weight, filter_priority);
        }




    }

    public ArrayList<CourseEntity> fillCourseListView(){
        ArrayList<CourseEntity> temp = new ArrayList<CourseEntity>();

        dialog_spinner_list.clear();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT " + CourseEntity.COLUMN_ID + ", " + CourseEntity.COLUMN_CODE + ", " + CourseEntity.COLUMN_SECTION + ", " + CourseEntity.COLUMN_TYPE + " FROM " + DatabaseHelper.TABLE_COURSE + " WHERE 1";


        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            CourseEntity i = new CourseEntity(c.getString(1), c.getString(2), c.getString(3));
            i.setID(c.getInt(0));
            temp.add(i);
            dialog_spinner_list.add(c.getString(1) + " (" + c.getString(3) + ")");
            c.moveToNext();
        }

        c.close();

        db.close();
        return temp;
    }

    public void showFilterDialog(View view){
        dialog.show();
    }

    public interface OnFilterListener{
        public void onFilterClicked(StringBuilder filter_course, StringBuilder filter_today, StringBuilder filter_date, StringBuilder filter_weight, StringBuilder filter_priority);
        public void onSearchItem(String text);
    }

    @Override
    public void onResume(){
        super.onResume();

    }

}





class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
