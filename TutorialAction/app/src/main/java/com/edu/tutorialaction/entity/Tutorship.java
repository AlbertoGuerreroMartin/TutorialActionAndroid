package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;

/**
 * Created by albertoguerreromartin on 25/04/15.
 */
public class Tutorship {

    public static final SimpleSerializer<Tutorship> SERIALIZER = new SimpleSerializer<>(Tutorship.class);

    @Expose private int tutorshipid;
    @Expose private String firstname;
    @Expose private String lastname;
    @Expose private String email;
    @Expose private String motive;
    @Expose private String date;
    @Expose private String hour;
    @Expose private String minutes;
    @Expose private int duration;
    @Expose private String courseName;


    public int getTutorshipid() {
        return tutorshipid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUserFullname() { return  firstname + " " + lastname; }

    public String getEmail() {
        return email;
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

    public String getMinutes() {
        return minutes;
    }

    public int getDuration() {
        return duration;
    }

    public String getCourseName() {
        return courseName;
    }
}
