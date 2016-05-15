package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 4/21/2016.
 */
public class TaskEntity {
    private int _id;
    public String title;
    public String description;
    public String due_date;
    public String notify_date;
    public int notify_hour;
    public int notify_minute;
    public int notify;
    public String weight;
    public String priority;
    public String type;
    public int course_id;
    public String course_name;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_DUEDATE = "due_date";
    public static final String COLUMN_NOTIFYDATE = "notify_date";
    public static final String COLUMN_HOUR = "notify_hour";
    public static final String COLUMN_MINUTE = "notify_minute";
    public static final String COLUMN_NOTIFY = "notify";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_COURSEID = "course_id";

    public TaskEntity(String title, String description, String due_date, String notify_date, int notify_hour, int notify_minute, int notify, String weight, String priority, String type, int course_id){
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.notify_date = notify_date;
        this.notify_hour = notify_hour;
        this.notify_minute = notify_minute;
        this.notify = notify;
        this.weight = weight;
        this.priority = priority;
        this.type = type;
        this.course_id = course_id;
    }

    public TaskEntity(String title, String due_date, String weight, String course_name){
        this.title = title;
        this.due_date = due_date;
        this.weight = weight;
        this.course_name = course_name;
        }

    public void setID(int id){
        this._id = id;
    }

    public int getID(){
        return this._id;
    }


}
