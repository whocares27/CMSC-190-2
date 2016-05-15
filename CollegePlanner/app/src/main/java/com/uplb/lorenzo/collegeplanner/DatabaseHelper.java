package com.uplb.lorenzo.collegeplanner;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.uplb.lorenzo.collegeplanner.entity.AbsenceEntity;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;
import com.uplb.lorenzo.collegeplanner.entity.InstructorScheduleEntity;
import com.uplb.lorenzo.collegeplanner.entity.NoteEntity;
import com.uplb.lorenzo.collegeplanner.entity.PhotoEntity;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;
import com.uplb.lorenzo.collegeplanner.notification.NotificationReceiver;
import com.uplb.lorenzo.collegeplanner.notification.NotificationService;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lorenzo on 3/25/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "planner.db";
    public static final String TABLE_INSTRUCTOR = "instructor";
    public static final String TABLE_COURSE = "course";
    public static final String TABLE_NOTE = "note";
    public static final String TABLE_ABSENCE = "absence";
    public static final String TABLE_INSTRUCTOR_SCHEDULE = "instructor_schedule";
    public static final String TABLE_PHOTO = "photo_note";
    public static final String TABLE_TASK = "task";
    Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("Database Operation", "Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_INSTRUCTOR + "(" +
                        InstructorEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        InstructorEntity.COLUMN_FIRSTNAME + " TEXT," +
                        InstructorEntity.COLUMN_LASTNAME + " TEXT," +
                        InstructorEntity.COLUMN_EMAIL + " TEXT," +
                        InstructorEntity.COLUMN_CONTACT + " TEXT," +
                        InstructorEntity.COLUMN_COLLEGE + " TEXT," +
                        InstructorEntity.COLUMN_DEPT + " TEXT," +
                        InstructorEntity.COLUMN_ROOM + " TEXT" +
                        ");";

        db.execSQL(query);
        Log.d("Database Operation", "Instructor table Created");

        db.execSQL("CREATE TABLE " + TABLE_COURSE + "(" +
                        CourseEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CourseEntity.COLUMN_CODE + " TEXT," +
                        CourseEntity.COLUMN_TITLE + " TEXT," +
                        CourseEntity.COLUMN_TYPE + " TEXT," +
                        CourseEntity.COLUMN_SECTION + " TEXT," +
                        CourseEntity.COLUMN_ROOM + " TEXT," +
                        CourseEntity.COLUMN_INSTRUCTOR + " TEXT," +
                        CourseEntity.COLUMN_UNITS + " TEXT," +
                        CourseEntity.COLUMN_ABSENCES + " INTEGER," +
                        CourseEntity.COLUMN_CURRABSENCES + " INTEGER" +
                        ");"

        );
        Log.d("Database Operation", "Course table Created");

        db.execSQL("CREATE TABLE " + TABLE_NOTE + "(" +
                        NoteEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NoteEntity.COLUMN_TITLE + " TEXT," +
                        NoteEntity.COLUMN_BODY + " TEXT," +
                        NoteEntity.COLUMN_DATE + " TEXT," +
                        NoteEntity.COLUMN_COURSEID + " INTEGER," +
                        "FOREIGN KEY (" + NoteEntity.COLUMN_COURSEID + ") REFERENCES " +
                        TABLE_COURSE + "(id)" +
                        ");"

        );
        Log.d("Database Operation", "Note table Created");

        db.execSQL("CREATE TABLE " + TABLE_ABSENCE + "(" +
                AbsenceEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AbsenceEntity.COLUMN_DATE + " TEXT," +
                AbsenceEntity.COLUMN_COURSEID + " INTEGER," +
                "FOREIGN KEY (" + AbsenceEntity.COLUMN_COURSEID + ") REFERENCES " +
                TABLE_COURSE + "(id)" +
                ");"

        );
        Log.d("Database Operation", "Absence table Created");

        db.execSQL("CREATE TABLE " + TABLE_INSTRUCTOR_SCHEDULE + "(" +
                InstructorScheduleEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InstructorScheduleEntity.COLUMN_DAY + " TEXT," +
                InstructorScheduleEntity.COLUMN_DESC + " TEXT," +
                InstructorScheduleEntity.COLUMN_STARTHOUR + " INTEGER," +
                InstructorScheduleEntity.COLUMN_STARTMINUTE + " INTEGER," +
                InstructorScheduleEntity.COLUMN_ENDHOUR + " INTEGER," +
                InstructorScheduleEntity.COLUMN_ENDMINUTE + " INTEGER," +
                InstructorScheduleEntity.COLUMN_INSTRUCTORID + " INTEGER," +
                "FOREIGN KEY (" + InstructorScheduleEntity.COLUMN_INSTRUCTORID + ") REFERENCES " +
                TABLE_INSTRUCTOR + "(id)" +
                ");"

        );
        Log.d("Database Operation", "Instructor schedule table Created");

        db.execSQL("CREATE TABLE " + TABLE_PHOTO + "(" +
                PhotoEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PhotoEntity.COLUMN_TITLE + " TEXT," +
                PhotoEntity.COLUMN_DATE + " TEXT," +
                PhotoEntity.COLUMN_PATH + " TEXT," +
                PhotoEntity.COLUMN_COURSEID + " INTEGER," +
                "FOREIGN KEY (" + NoteEntity.COLUMN_COURSEID + ") REFERENCES " +
                TABLE_COURSE + "(id)" +
                ");"

        );
        Log.d("Database Operation", "Photo Note table Created");

        db.execSQL("CREATE TABLE " + TABLE_TASK + "(" +
                TaskEntity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TaskEntity.COLUMN_TITLE + " TEXT," +
                TaskEntity.COLUMN_DESC + " TEXT," +
                TaskEntity.COLUMN_DUEDATE + " TEXT," +
                TaskEntity.COLUMN_NOTIFYDATE + " TEXT," +
                TaskEntity.COLUMN_HOUR + " INTEGER," +
                TaskEntity.COLUMN_MINUTE + " INTEGER," +
                TaskEntity.COLUMN_NOTIFY + " INTEGER," +
                TaskEntity.COLUMN_WEIGHT + " TEXT," +
                TaskEntity.COLUMN_PRIORITY + " TEXT," +
                TaskEntity.COLUMN_TYPE + " TEXT," +
                TaskEntity.COLUMN_COURSEID + " INTEGER," +
                "FOREIGN KEY (" + TaskEntity.COLUMN_COURSEID + ") REFERENCES " +
                TABLE_COURSE + "(id)" +
                ");"

        );
        Log.d("Database Operation", "Task table Created");

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTOR_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ABSENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        Log.d("Database Operation", "Table upgraded");
        onCreate(db);
    }

    public long addInstructor(InstructorEntity instructor){
        ContentValues cv = new ContentValues();
        cv.put(InstructorEntity.COLUMN_FIRSTNAME, instructor.firstname);
        cv.put(InstructorEntity.COLUMN_LASTNAME, instructor.lastname);
        cv.put(InstructorEntity.COLUMN_EMAIL, instructor.email);
        cv.put(InstructorEntity.COLUMN_CONTACT, instructor.contact);
        cv.put(InstructorEntity.COLUMN_COLLEGE, instructor.college);
        cv.put(InstructorEntity.COLUMN_DEPT, instructor.department);
        cv.put(InstructorEntity.COLUMN_ROOM, instructor.room);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_INSTRUCTOR, null, cv);

        //Log.d("Database Operation", "Instructor added");
        db.close();
        return id;
    }

    public long addCourse(CourseEntity course){
        ContentValues cv = new ContentValues();
        cv.put(CourseEntity.COLUMN_CODE, course.code);
        cv.put(CourseEntity.COLUMN_TITLE, course.title);
        cv.put(CourseEntity.COLUMN_TYPE, course.type);
        cv.put(CourseEntity.COLUMN_SECTION, course.section);
        cv.put(CourseEntity.COLUMN_ROOM, course.room);
        cv.put(CourseEntity.COLUMN_INSTRUCTOR, course.instructor);
        cv.put(CourseEntity.COLUMN_UNITS, course.units);
        cv.put(CourseEntity.COLUMN_ABSENCES, course.absences);
        cv.put(CourseEntity.COLUMN_CURRABSENCES, course.current_absences);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_COURSE, null, cv);

        //Log.d("Database Operation", "Instructor added");
        db.close();
        return id;
    }

    public long addNote(NoteEntity note){
        ContentValues cv = new ContentValues();
        cv.put(NoteEntity.COLUMN_TITLE, note.title);
        cv.put(NoteEntity.COLUMN_BODY, note.body);
        cv.put(NoteEntity.COLUMN_DATE, note.date);
        cv.put(NoteEntity.COLUMN_COURSEID, note.course_id);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NOTE, null, cv);

        db.close();

        return id;
    }

    public long addAbsence(AbsenceEntity absence){
        ContentValues cv = new ContentValues();
        cv.put(AbsenceEntity.COLUMN_DATE, absence.date);
        cv.put(AbsenceEntity.COLUMN_COURSEID, absence.course_id);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_ABSENCE, null, cv);

        db.close();

        return id;
    }

    public long addSchedule(InstructorScheduleEntity schedule){
        ContentValues cv = new ContentValues();
        cv.put(InstructorScheduleEntity.COLUMN_DAY, schedule.day);
        cv.put(InstructorScheduleEntity.COLUMN_DESC, schedule.desc);
        cv.put(InstructorScheduleEntity.COLUMN_STARTHOUR, schedule.start_hour);
        cv.put(InstructorScheduleEntity.COLUMN_STARTMINUTE, schedule.start_minute);
        cv.put(InstructorScheduleEntity.COLUMN_ENDHOUR, schedule.end_hour);
        cv.put(InstructorScheduleEntity.COLUMN_ENDMINUTE, schedule.end_minute);
        cv.put(InstructorScheduleEntity.COLUMN_INSTRUCTORID, schedule.instructor_id);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_INSTRUCTOR_SCHEDULE, null, cv);

        db.close();

        return id;
    }

    public long addPhoto(PhotoEntity photo){
        ContentValues cv = new ContentValues();
        cv.put(PhotoEntity.COLUMN_TITLE, photo.title);
        cv.put(PhotoEntity.COLUMN_DATE, photo.date);
        cv.put(PhotoEntity.COLUMN_PATH, photo.path);
        cv.put(PhotoEntity.COLUMN_COURSEID, photo.course_id);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_PHOTO, null, cv);

        db.close();

        return id;
    }

    public long addTask(TaskEntity task){
        ContentValues cv = new ContentValues();
        cv.put(TaskEntity.COLUMN_TITLE, task.title);
        cv.put(TaskEntity.COLUMN_DESC, task.description);
        cv.put(TaskEntity.COLUMN_DUEDATE, task.due_date);
        cv.put(TaskEntity.COLUMN_NOTIFYDATE, task.notify_date);
        cv.put(TaskEntity.COLUMN_HOUR, task.notify_hour);
        cv.put(TaskEntity.COLUMN_MINUTE, task.notify_minute);
        cv.put(TaskEntity.COLUMN_NOTIFY, task.notify);
        cv.put(TaskEntity.COLUMN_WEIGHT, task.weight);
        cv.put(TaskEntity.COLUMN_PRIORITY, task.priority);
        cv.put(TaskEntity.COLUMN_TYPE, task.type);
        cv.put(TaskEntity.COLUMN_COURSEID, task.course_id);


        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_TASK, null, cv);

        db.close();

        return id;
    }


    public void deleteInstructor(int ID){
        deleteInstructorSchedule(ID, 1);
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_INSTRUCTOR + " WHERE " + InstructorEntity.COLUMN_ID + " = " + ID + ";";
        Log.d("Database Operation", "Instructor deleted");
        db.execSQL(query);

        db.close();

    }

    public void deleteCourse(int ID){
        deleteNote(ID, 1);
        deleteAbsence(ID, 1);
        deletePhoto(ID, 1);
        deleteTask(ID, 1);
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_COURSE + " WHERE " + CourseEntity.COLUMN_ID + " = " + ID + ";";
        Log.d("Database Operation", "Course deleted");
        db.execSQL(query);

        db.close();
    }

    public void deleteNote(int ID, int flag){
        SQLiteDatabase db = getWritableDatabase();
        if(flag == 0) {
            String query = "DELETE FROM " + TABLE_NOTE + " WHERE " + NoteEntity.COLUMN_ID + " = " + ID + ";";
            db.execSQL(query);
        } else {
            String query = "DELETE FROM " + TABLE_NOTE + " WHERE " + NoteEntity.COLUMN_COURSEID + " = " + ID + ";";
            db.execSQL(query);
        }
        Log.d("Database Operation", "Note deleted");


        db.close();
    }

    public void deleteAbsence(int ID, int flag){
        SQLiteDatabase db = getWritableDatabase();
        if(flag == 0) {
            String query = "DELETE FROM " + TABLE_ABSENCE + " WHERE " + AbsenceEntity.COLUMN_ID + " = " + ID + ";";
            db.execSQL(query);
        } else {
            String query = "DELETE FROM " + TABLE_ABSENCE + " WHERE " + AbsenceEntity.COLUMN_COURSEID + " = " + ID + ";";
            db.execSQL(query);
        }
        Log.d("Database Operation", "Absence deleted");


        db.close();
    }

    public void deleteInstructorSchedule(int ID, int flag){
        SQLiteDatabase db = getWritableDatabase();
        if(flag == 0) {
            String query = "DELETE FROM " + TABLE_INSTRUCTOR_SCHEDULE + " WHERE " + InstructorScheduleEntity.COLUMN_ID + " = " + ID + ";";
            db.execSQL(query);
        } else {
            String query = "DELETE FROM " + TABLE_INSTRUCTOR_SCHEDULE + " WHERE " + InstructorScheduleEntity.COLUMN_INSTRUCTORID + " = " + ID + ";";
            db.execSQL(query);
        }
        Log.d("Database Operation", "Schedule deleted");


        db.close();
    }

    public void deletePhoto(int ID, int flag){
        SQLiteDatabase db = getWritableDatabase();
        if(flag == 0) {
            String query = "DELETE FROM " + TABLE_PHOTO + " WHERE " + PhotoEntity.COLUMN_ID + " = " + ID + ";";
            db.execSQL(query);
        } else {
            String query = "DELETE FROM " + TABLE_PHOTO + " WHERE " + PhotoEntity.COLUMN_COURSEID + " = " + ID + ";";
            db.execSQL(query);
        }
        Log.d("Database Operation", "Photo deleted");


        //File file= new File(path);
        //{
          //  file.delete();
            //Log.d("Database Operation", "Photo storage deleted");
        //}
        db.close();
    }

    public void deleteTask(int ID, int flag){

        if(flag == 0) {
            SQLiteDatabase db = getWritableDatabase();
            String query = "DELETE FROM " + TABLE_TASK + " WHERE " + TaskEntity.COLUMN_ID + " = " + ID + ";";
            db.execSQL(query);
            db.close();
        } else {

            cancelIDNotification(ID);
            SQLiteDatabase db = getWritableDatabase();
            String query = "DELETE FROM " + TABLE_TASK + " WHERE " + TaskEntity.COLUMN_COURSEID + " = " + ID + ";";
            db.execSQL(query);
            db.close();

        }
        Log.d("Database Operation", "Task deleted");


    }

    public void cancelIDNotification(int ID){


        for(int id : getTaskIDs(ID)){
            cancelNotif(id, NotificationService.CANCEL);
        }


    }

    public void cancelNotif(int ID, String flag){
        Intent service = new Intent(context, NotificationService.class);
        service.putExtra("task_id", ID);
        service.setAction(flag);
        context.startService(service);

    }

    public void DeleteAllInstructor(){
        DeleteAllSchedule();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_INSTRUCTOR, null, null);
        Log.d("Database Operation", "All instructor rows deleted");
        db.close();


    }

    public void DeleteAllSchedule(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_INSTRUCTOR_SCHEDULE, null, null);
        Log.d("Database Operation", "All schedule rows deleted");
        db.close();
    }

    public void DeleteAllCourse(){
            DeleteAllAbsence();
            DeleteAllNote();
            DeleteAllPhoto();
            DeleteAllTask();
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_COURSE, null, null);
            Log.d("Database Operation", "All course rows deleted");
            db.close();
    }

    public void DeleteAllPhoto(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PHOTO, null, null);
        Log.d("Database Operation", "All photo rows deleted");
        db.close();
    }

    public void DeleteAllNote(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, null, null);
        Log.d("Database Operation", "All note rows deleted");
        db.close();
    }

    public void DeleteAllAbsence(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ABSENCE, null, null);
        Log.d("Database Operation", "All absence rows deleted");
        db.close();

    }

    public void DeleteAllAbsence(int ID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ABSENCE, AbsenceEntity.COLUMN_COURSEID + "=" + ID , null);
        Log.d("Database Operation", "All absence rows deleted");
        db.close();

    }

    public void DeleteAllTask(){
        cancelAllNotification();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASK, null, null);
        Log.d("Database Operation", "All task rows deleted");
        db.close();

    }

    public void cancelAllNotification(){

        for(int id : getTaskIDs()){
            cancelNotif(id, NotificationService.CANCEL);
        }

    }

    public void DeleteAllUpcomingTask(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASK, TaskEntity.COLUMN_TYPE + "= \"Upcoming\"", null);
        Log.d("Database Operation", "All upcoming task rows deleted");
        db.close();

    }

    public void DeleteAllCompletedTask(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASK, TaskEntity.COLUMN_TYPE + "= \"Completed\"", null);
        Log.d("Database Operation", "All completed task rows deleted");
        db.close();

    }

    public ArrayList<Integer> getTaskIDs() {
        ArrayList<Integer> id_list = new ArrayList<Integer>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + TaskEntity.COLUMN_ID + " FROM " + DatabaseHelper.TABLE_TASK + " WHERE type = " + "\"" + "Upcoming" + "\"" + " AND notify = 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            id_list.add(c.getInt(0));
            c.moveToNext();
        }
        c.close();

        db.close();

        return id_list;
    }

    public ArrayList<Integer> getTaskIDs(int ID) {
        ArrayList<Integer> id_list = new ArrayList<Integer>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + TaskEntity.COLUMN_ID +", "+ TaskEntity.COLUMN_NOTIFY + " FROM " + DatabaseHelper.TABLE_TASK +" WHERE type = " + "\""+"Upcoming"+"\"" + " AND " + TaskEntity.COLUMN_COURSEID + "=" + ID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            id_list.add(c.getInt(0));
            c.moveToNext();
        }

        c.close();
        db.close();
        return id_list;
    }

    public int getCourseCount(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT count(*) FROM " + DatabaseHelper.TABLE_COURSE +" WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        db.close();
        return  count;
    }


}
