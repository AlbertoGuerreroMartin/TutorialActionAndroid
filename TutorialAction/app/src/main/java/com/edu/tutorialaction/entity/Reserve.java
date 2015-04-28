package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class Reserve {

    public static final SimpleSerializer<Reserve> SERIALIZER = new SimpleSerializer<>(Reserve.class);

    @Expose private int reserveid;
    @Expose private String firstname;
    @Expose private String lastname;
    @Expose private String email;
    @Expose private int tutorshipType;
    @Expose private String motive;
    @Expose private String date;
    @Expose private String hour;
    @Expose private String courseName;


    public int getReserveid() {
        return reserveid;
    }

    public String getUserFullname() { return  firstname + " " + lastname; }

    public String getEmail() {
        return email;
    }

    public int getTutorshipType() {
        return tutorshipType;
    }

    public String getMotive() {
        return motive;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getCourseName() {
        return courseName;
    }

}
