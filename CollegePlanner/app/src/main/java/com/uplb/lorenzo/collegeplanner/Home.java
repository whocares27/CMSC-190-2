package com.uplb.lorenzo.collegeplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import com.uplb.lorenzo.collegeplanner.absence.Absence;
import com.uplb.lorenzo.collegeplanner.course.Course;
import com.uplb.lorenzo.collegeplanner.custom.DrawerActivity;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;
import com.uplb.lorenzo.collegeplanner.instructor.Instructor;
import com.uplb.lorenzo.collegeplanner.note.Note;
import com.uplb.lorenzo.collegeplanner.photo_note.Photo_note;
import com.uplb.lorenzo.collegeplanner.task.Task;

import java.io.File;
import java.text.SimpleDateFormat;

public class Home extends DrawerActivity {

    public static DatabaseHelper dbHandler;

    TextView upcoming_task;
    TextView quiz;
    TextView seatwork;
    TextView exam;
    TextView meeting;
    TextView project;
    TextView assignment;

    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        dbHandler = new DatabaseHelper(this);
        dbHandler.getReadableDatabase();

        ((Toolbar) findViewById(R.id.toolbar)).setLogo(R.drawable.action_home);
        getLayoutInflater().inflate(R.layout.activity_home, rl);

        long date = System.currentTimeMillis();

        TextView tvDisplayDate = (TextView) findViewById(R.id.todayDate);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy ccc");
        SimpleDateFormat sd = new SimpleDateFormat("M'/'d'/'y");
        today = sd.format(date);
        String dateString = sdf.format(date);
        tvDisplayDate.setText("  Today - "+ dateString);

        upcoming_task = (TextView) findViewById(R.id.upcoming_task);
        quiz = (TextView) findViewById(R.id.quiz);
        seatwork = (TextView) findViewById(R.id.seatwork);
        exam = (TextView) findViewById(R.id.exam);
        meeting = (TextView) findViewById(R.id.meeting);
        project = (TextView) findViewById(R.id.project);
        assignment = (TextView) findViewById(R.id.assignment);


        File direct = new File(Environment.getExternalStorageDirectory() + "/CollegePlanner/photos");
        if(!direct.exists()){
            direct.mkdirs();
        }

    }

    public void startInstructorActivity(View view){
        Intent i = new Intent(this, Instructor.class);
        startActivity(i);
    }

    public void startCourseActivity(View view){
        Intent i = new Intent(this, Course.class);
        startActivity(i);
    }

    public void startNoteActivity(View view){
        Intent i = new Intent(this, Note.class);
        startActivity(i);
    }

    public void startAbsenceActivity(View view){
        Intent i = new Intent(this, Absence.class);
        startActivity(i);
    }

    public void startPhotoActivity(View view){
        Intent i = new Intent(this, Photo_note.class);
        startActivity(i);
    }

    public void startTaskActivity(View view){
        Intent i = new Intent(this, Task.class);
        startActivity(i);
    }

    public void fillOverview(){
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Cursor c;

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\"", null);
        c.moveToFirst();
        if(c.getInt(0) == 1) upcoming_task.setText(c.getInt(0)+ " Upcoming task");
        else upcoming_task.setText(c.getInt(0)+ " Upcoming tasks");

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Quiz\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();
        if(c.getInt(0) == 1) quiz.setText(c.getInt(0)+ " Quiz");
        else quiz.setText(c.getInt(0)+ " Quizzes");

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Seatwork\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();

        if(c.getInt(0) == 1) seatwork.setText(c.getInt(0)+ " Seatwork");
        else seatwork.setText(c.getInt(0)+ " Seatworks");

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Exam\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();
        if(c.getInt(0) == 1) exam.setText(c.getInt(0)+ " Exam");
        else exam.setText(c.getInt(0)+ " Exams");

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Meeting\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();
        if(c.getInt(0) == 1) meeting.setText(c.getInt(0)+ " Meeting");
        else meeting.setText(c.getInt(0)+ " Meetings");
        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Project\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();
        if(c.getInt(0) == 1) project.setText(c.getInt(0)+ " Project");
        else project.setText(c.getInt(0)+ " Projects");

        c = db.rawQuery( "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + TaskEntity.COLUMN_TYPE + "= \"Upcoming\""+ " AND "+ TaskEntity.COLUMN_WEIGHT + "= \"Assignment\"" + " AND " + TaskEntity.COLUMN_DUEDATE + "=" + "\""+today+"\"" , null);
        c.moveToFirst();
        if(c.getInt(0) == 1) assignment.setText(c.getInt(0)+ " Assignment");
        else assignment.setText(c.getInt(0)+ " Assignments");

        c.close();

        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillOverview();
    }
}
