package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/16/2016.
 */
public class InstructorScheduleEntity {
    private int _id;
    public String day;
    public String desc;
    public int start_hour;
    public int start_minute;
    public int end_hour;
    public int end_minute;
    public int instructor_id;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_STARTHOUR = "starthour";
    public static final String COLUMN_STARTMINUTE = "startminute";
    public static final String COLUMN_ENDHOUR = "endhour";
    public static final String COLUMN_ENDMINUTE = "endminute";
    public static final String COLUMN_INSTRUCTORID = "instructorid";


    public InstructorScheduleEntity(String day, String desc, int start_hour, int start_minute, int end_hour, int end_minute, int instructor_id){
        this.day = day;
        this.desc = desc;
        this.start_hour = start_hour;
        this.start_minute = start_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.instructor_id = instructor_id;
    }


    public void setID(int id){
        this._id = id;
    }

    public int getID(){
        return this._id;
    }
}
