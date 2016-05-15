package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class CourseEntity {

    private int _id;
    public String code;
    public String title;
    public String type;
    public String section;
    public String room;
    public String instructor;
    public String units;
    public int absences;
    public int current_absences = 0;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SECTION = "section";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_INSTRUCTOR = "instructor";
    public static final String COLUMN_UNITS = "units";
    public static final String COLUMN_ABSENCES = "absences";
    public static final String COLUMN_CURRABSENCES = "current_absences";

    public CourseEntity(String code, String title, String type, String section, String room, String instructor, String units, int absences){

        this.code = code;
        this.title = title;
        this.type = type;
        this.section = section;
        this.room = room;
        this.instructor = instructor;
        this.units = units;
        this.absences = absences;
    }

    public CourseEntity(String code, String type, String section){
        this.code = code;
        this.type = type;
        this.section = section;

    }

    public CourseEntity(String code, String type, int absences, int current_absences){
        this.code = code;
        this.type = type;
        this.absences = absences;
        this.current_absences = current_absences;

    }

    public void setID(int id){
        this._id = id;
    }

    public int getID(){
        return this._id;
    }
}
