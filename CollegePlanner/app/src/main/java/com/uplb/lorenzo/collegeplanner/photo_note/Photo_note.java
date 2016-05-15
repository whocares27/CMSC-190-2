package com.uplb.lorenzo.collegeplanner.photo_note;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.course.Add_course;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.Home;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.adapter.CustomImageGridViewAdapter;
import com.uplb.lorenzo.collegeplanner.entity.MarshMallowPermission;
import com.uplb.lorenzo.collegeplanner.entity.PhotoEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Photo_note extends DrawerActivity implements SearchView.OnQueryTextListener {
    private ArrayList<PhotoEntity> photo_list;
    private ArrayList<CourseEntity> course_list;
    private ArrayList<String> spinner_list;
    private ArrayList<String> dialog_spinner_list;
    private Spinner spinner_course;
    private ArrayAdapter<String> adapter;
    CustomImageGridViewAdapter photo_adapter;
    GridView gv;
    private String date;
    private int c_id;
    private String title;
    private String c_name;
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private Uri mCapturedImageURI;

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.ic_photo_white_small);
        getLayoutInflater().inflate(R.layout.activity_photo_note, rl);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Home.dbHandler.getCourseCount() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Photo_note.this, R.style.MyAlertDialogStyle);
                    builder.setMessage("Course list is empty.\nDo you want to add now?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivityForResult(new Intent(Photo_note.this, Add_course.class), 0);

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
                    showCustomDialog();
                }
            }
        });

        gv = (GridView) findViewById(R.id.GridViewPhoto);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                PhotoEntity image = photo_list.get(position);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + image.path), "image/*");
                startActivity(Intent.createChooser(intent, "Complete action using"));

            }
        });

        registerForContextMenu(gv);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        spinner_list = new ArrayList<String>();
        dialog_spinner_list = new ArrayList<String>();



        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);

        SimpleDateFormat formatter = new SimpleDateFormat("M'/'d'/'y");
        date = formatter.format(curDateTime);

        spinner_course.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                            querySelect(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        course_list = fillCourseListView();
        adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, spinner_list );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);
        int position = spinner_course.getSelectedItemPosition();
        querySelect(position);

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
            int position = spinner_course.getSelectedItemPosition();
            querySelect(position);
            adapter.notifyDataSetChanged();
        } else {
            int position = spinner_course.getSelectedItemPosition();
            querySelect(position);
            photo_list = searchItem(text);
            photo_adapter = new CustomImageGridViewAdapter(this, photo_list);
            gv.setAdapter(photo_adapter);
            photo_adapter.notifyDataSetChanged();
        }

        return false;
    }

    public ArrayList<PhotoEntity> searchItem(String itemToSearch){
        ArrayList<PhotoEntity> temp = new ArrayList<>();
        for(PhotoEntity i: photo_list){
            if(i.title.toLowerCase().contains(itemToSearch.toLowerCase())){
                temp.add(i);
            }

        }
        return temp;

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.GridViewPhoto) {
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
            showEditCustomDialog(photo_list.get(pos).getID());

        } else if (item.getTitle() == "DELETE") {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Home.dbHandler.deletePhoto(photo_list.get(pos).getID(), 0);
                            int position = spinner_course.getSelectedItemPosition();
                            querySelect(position);
                            Toast.makeText(Photo_note.this, "Photo deleted", Toast.LENGTH_SHORT).show();

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
                            Home.dbHandler.DeleteAllPhoto();
                            int position = spinner_course.getSelectedItemPosition();
                            querySelect(position);
                            Toast.makeText(Photo_note.this, "All photos deleted", Toast.LENGTH_SHORT).show();

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

    public void showEditCustomDialog(final int ID){
        final Dialog dialog = new Dialog(this, R.style.cust_dialog);

        dialog.setContentView(R.layout.custom_photo_edit_dialog_box);
        final Spinner sp = (Spinner) dialog.findViewById(R.id.spinner_course_dialog);
        final EditText title_input = (EditText) dialog.findViewById(R.id.title_dialog);

        ArrayAdapter<String> adptr = new ArrayAdapter<String>(this, R.layout.custom_day_spinner, dialog_spinner_list );
        adptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adptr);

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
        String query = "SELECT " + PhotoEntity.COLUMN_TITLE+ ", " + PhotoEntity.COLUMN_COURSEID + " FROM " + DatabaseHelper.TABLE_PHOTO + " WHERE " + PhotoEntity.COLUMN_ID + " = " +ID;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if(c.getString(c.getColumnIndex(PhotoEntity.COLUMN_TITLE)) != null){
            title_input.setText(c.getString(0));
            for(int i = 0; i < course_list.size(); i++){
                if(course_list.get(i).getID() == c.getInt(1) ) {
                    sp.setSelection(i);
                    break;
                }
            }
        }
        c.close();
        db.close();


        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ContentValues cv = new ContentValues();

                cv.put(PhotoEntity.COLUMN_TITLE, title_input.getText().toString());
                cv.put(PhotoEntity.COLUMN_COURSEID, course_list.get(sp.getSelectedItemPosition()).getID());

                SQLiteDatabase db = Home.dbHandler.getWritableDatabase();
                db.update(DatabaseHelper.TABLE_PHOTO, cv, PhotoEntity.COLUMN_ID + " = " + ID, null);
                db.close();
                dialog.dismiss();
                int position = spinner_course.getSelectedItemPosition();
                querySelect(position);
                Toast.makeText(Photo_note.this, "Edit saved", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }

    public void showCustomDialog(){
        final Dialog dialog = new Dialog(this, R.style.cust_dialog);

        dialog.setContentView(R.layout.custom_photo_dialog_box);
        final Spinner sp = (Spinner) dialog.findViewById(R.id.spinner_course_dialog);
        final EditText title_input = (EditText) dialog.findViewById(R.id.title_dialog);

        ArrayAdapter<String> adptr = new ArrayAdapter<String>(this, R.layout.custom_day_spinner, dialog_spinner_list );

        adptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adptr);

        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        c_id = course_list.get(sp.getSelectedItemPosition()).getID();
                        title = title_input.getText().toString();
                        c_name = dialog_spinner_list.get(sp.getSelectedItemPosition());

                        if (!marshMallowPermission.checkPermissionForReadExternalStorage()) {
                            marshMallowPermission.requestPermissionForReadExternalStorage();
                        }else {
                            callGallery();
                        }
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        //activeTakePhoto();
                        c_id = course_list.get(sp.getSelectedItemPosition()).getID();
                        title = title_input.getText().toString();
                        c_name = dialog_spinner_list.get(sp.getSelectedItemPosition());
                        askCameraPermission();
                        dialog.dismiss();
                    }
                });

        // show dialog on screen
        dialog.show();
    }

    public void askCameraPermission() {

        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        }
        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        }
        if (!marshMallowPermission.checkPermissionForReadExternalStorage()) {
            marshMallowPermission.requestPermissionForReadExternalStorage();
        }

        if(marshMallowPermission.checkPermissionForCamera() && marshMallowPermission.checkPermissionForExternalStorage() && marshMallowPermission.checkPermissionForReadExternalStorage()){
            startCamera();
        }

    }

    public void startCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }


    public void callGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 0) {
            course_list = fillCourseListView();
            adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, spinner_list );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_course.setAdapter(adapter);
            return;
        }

        switch (requestCode) {
            case CAMERA_REQUEST:

                if (requestCode == CAMERA_REQUEST &&
                        resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor =
                            managedQuery(mCapturedImageURI, projection, null,
                                    null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    File f = new File(picturePath);
                    String filename= f.getName();

                    String path = Savefile(filename,picturePath);
                    PhotoEntity p = new PhotoEntity(title, date, path, c_id);
                    long id = Home.dbHandler.addPhoto(p);
                    Toast.makeText(Photo_note.this, "Photo added", Toast.LENGTH_SHORT).show();

                    int position = spinner_course.getSelectedItemPosition();
                    querySelect(position);

                }
                break;

            case PICK_FROM_GALLERY:
                if (requestCode == PICK_FROM_GALLERY &&
                        resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String picturePath = null;
                    File img = new File(selectedImage.getPath());
                    if(img.exists()){
                        picturePath = selectedImage.getPath();
                    } else {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null, null,
                                        null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();
                    }

                    File f = new File(picturePath);
                    String filename= f.getName();
                    String path = Savefile(filename,picturePath);


                    PhotoEntity p = new PhotoEntity(title, date, path, c_id);
                    long id = Home.dbHandler.addPhoto(p);
                    Toast.makeText(Photo_note.this, "Photo added", Toast.LENGTH_SHORT).show();

                    int position = spinner_course.getSelectedItemPosition();
                    querySelect(position);
                }
        }
    }

    public String Savefile(String name, String path) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/CollegePlanner/photos");
        File file = new File(Environment.getExternalStorageDirectory() + "/CollegePlanner/photos/" + name);

        if (!direct.exists()) {
            direct.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileChannel src = new FileInputStream(path).getChannel();
                FileChannel dst = new FileOutputStream(file).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return file.getPath();
    }


    public void querySelect(int position){
        if(position==0) {
            updateList("SELECT photo_note.id, photo_note.title, photo_note.date, photo_note.path, course.code, course.type FROM " + DatabaseHelper.TABLE_PHOTO + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON photo_note.course_id = course.id" + " ORDER BY photo_note.id DESC");
        } else {
            updateList("SELECT photo_note.id, photo_note.title, photo_note.date, photo_note.path, course.code, course.type FROM " + DatabaseHelper.TABLE_PHOTO + " INNER JOIN " +DatabaseHelper.TABLE_COURSE + " ON photo_note.course_id = course.id" +  " WHERE photo_note.course_id" + "=" + course_list.get(position-1).getID()+ " ORDER BY photo_note.id DESC");
        }
        photo_adapter.notifyDataSetChanged();
    }


    public ArrayList<CourseEntity> fillCourseListView(){
        ArrayList<CourseEntity> temp = new ArrayList<CourseEntity>();

        dialog_spinner_list.clear();spinner_list.clear();
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
            dialog_spinner_list.add(c.getString(1) + " (" + c.getString(3) + ")");
            c.moveToNext();
        }

        c.close();

        db.close();
        return temp;
    }

    public ArrayList<PhotoEntity> fillPhotoGridView(String query){
        ArrayList<PhotoEntity> temp = new ArrayList<PhotoEntity>();

        SQLiteDatabase db = Home.dbHandler.getWritableDatabase();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            PhotoEntity i = new PhotoEntity(c.getString(1), c.getString(2), c.getString(3), c.getString(4) + " ("+c.getString(5)+")");
            i.setID(c.getInt(0));
            temp.add(i);
            c.moveToNext();
        }

        c.close();


        db.close();

        return temp;
    }

    public void updateList(String query){

        photo_list = fillPhotoGridView(query);
        photo_adapter = new CustomImageGridViewAdapter(this, photo_list);
        gv.setAdapter(photo_adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
