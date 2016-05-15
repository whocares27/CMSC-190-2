package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/17/2016.
 */
public class PhotoEntity {
    private int _id;
    public String title;
    public String path;
    public String date;
    public String course_name;
    public int course_id;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COURSEID = "course_id";


    public PhotoEntity(String title, String date, String path, int course_id){
        this.title = title;
        this.date = date;
        this.path = path;
        this.course_id = course_id;
    }

    public PhotoEntity(String title, String date, String path, String course_name){
        this.title = title;
        this.date = date;
        this.path = path;
        this.course_name = course_name;
    }


    public void setID(int id){
        this._id = id;
    }

    public int getID(){
        return this._id;
    }
}
