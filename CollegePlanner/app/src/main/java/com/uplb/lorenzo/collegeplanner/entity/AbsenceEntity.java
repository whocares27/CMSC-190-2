package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/11/2016.
 */
public class AbsenceEntity {
    private int _id;
    public String date;
    public int course_id;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COURSEID = "course_id";

    public AbsenceEntity(String date, int course_id){
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
