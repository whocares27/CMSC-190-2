package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/9/2016.
 */
public class NoteEntity {
    private int _id;
    public String title;
    public String body;
    public String date;
    public String course_name;
    public int course_id;


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_DATE = "date";
    //public static final String COLUMN_COURSENAME = "course_name";
    public static final String COLUMN_COURSEID = "course_id";

    public NoteEntity(String title, String body, String date, String course_name){
        this.title = title;
        this.body = body;
        this.date = date;
        this.course_name = course_name;
    }

    public NoteEntity(String title, String body, String date, int course_id){
        this.title = title;
        this.body = body;
        this.date = date;
        this.course_id = course_id;
    }

    public void setID(int id){
        this._id = id;
    }

    public int getID(){
        return this._id;
    }
}
