package com.uplb.lorenzo.collegeplanner.custom;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.absence.Absence;
import com.uplb.lorenzo.collegeplanner.course.Course;
import com.uplb.lorenzo.collegeplanner.instructor.Instructor;
import com.uplb.lorenzo.collegeplanner.note.Note;
import com.uplb.lorenzo.collegeplanner.photo_note.Photo_note;
import com.uplb.lorenzo.collegeplanner.task.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected RelativeLayout rl;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private static final int REQUEST_FILECHOOSER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rl = (RelativeLayout) findViewById(R.id.content_frame);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i;
        if (id == R.id.nav_home) {
            i = new Intent(this, Home.class);
            startActivity(i);
        } else if (id == R.id.nav_instructor) {
            i = new Intent(this, Instructor.class);
            startActivity(i);
        } else if (id == R.id.nav_course) {
            i = new Intent(this, Course.class);
            startActivity(i);
        } else if (id == R.id.nav_task) {
            i = new Intent(this, Task.class);
            startActivity(i);
        } else if (id == R.id.nav_absent) {
            i = new Intent(this, Absence.class);
            startActivity(i);
        } else if (id == R.id.nav_note) {
            i = new Intent(this, Note.class);
            startActivity(i);
        } else if (id == R.id.nav_photo) {
            i = new Intent(this, Photo_note.class);
            startActivity(i);
        }else if (id == R.id.nav_share) {
            sendViaBluetooth();
        }else if (id == R.id.nav_backup) {
            exportDB();
        }else if (id == R.id.nav_import) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("This will overwrite your existing data. Do you want to continue?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openFileChooser();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_FILECHOOSER);
    }

    public void sendViaBluetooth(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(this, "Bluetooth is not supported!", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }

    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);

        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.uplb.lorenzo.collegeplanner" +"/databases/"+ DatabaseHelper.DATABASE_NAME;
        File directory = new File(Environment.getExternalStorageDirectory()  +  "/CollegePlanner/backup" );

        if(!directory.exists()){
            directory.mkdirs();
        }

        String backupDBPath = "/CollegePlanner/backup/"+DatabaseHelper.DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Database Exported!\nLocation: "+ backupDB.getPath(), Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            exportDB();
            File db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CollegePlanner/backup/" + DatabaseHelper.DATABASE_NAME);

            if(db.exists()){
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("*/*");
                //intent.setPackage("com.android.bluetooth");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(db));
                intent.setComponent(
                        new ComponentName("com.android.bluetooth",
                                "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
                startActivity(Intent.createChooser(intent, "Select bluetooth option"));
            }
            else {
                Toast.makeText(this, "Failed to retrieve backup",Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == REQUEST_FILECHOOSER && data != null) {
            Uri currFileURI = data.getData();
            String path=currFileURI.getPath();

            if(currFileURI.getLastPathSegment().equals("planner.db")){
                importDB(path);
            }
            else{
                Toast.makeText(this, "File is invalid",Toast.LENGTH_SHORT).show();

            }


        }
    }

    public void importDB(String path){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/"+ "com.uplb.lorenzo.collegeplanner" +"/databases/"+ DatabaseHelper.DATABASE_NAME;
                String backupDBPath = path; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}
