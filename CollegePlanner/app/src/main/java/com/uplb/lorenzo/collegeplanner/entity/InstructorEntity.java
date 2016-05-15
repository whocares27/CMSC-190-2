package com.uplb.lorenzo.collegeplanner.entity;

/**
 * Created by lorenzo on 3/25/2016.
 */
public class InstructorEntity {

    private int _id;
    public String firstname;
    public String lastname;
    public String email;
    public String contact;
    public String college;
    public String department;
    public String room;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_COLLEGE = "college";
    public static final String COLUMN_DEPT = "department";
    public static final String COLUMN_ROOM = "room";

    public InstructorEntity( String firstname, String lastname, String email, String contact, String college, String department, String room){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
        this.college = college;
        this.department = department;
        this.room = room;
    }


        public void setID(int id){
            this._id = id;
        }

        public int getID(){
            return this._id;
        }

    public String getFullname(){
        return lastname+", "+firstname;
    }



}
