package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class Reserve {

    public static final SimpleSerializer<Reserve> SERIALIZER = new SimpleSerializer<>(Reserve.class);

    @Expose private String firstname;
    @Expose private String lastname;
    @Expose private String motive;
    @Expose private String date;
    @Expose private String hour;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
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
}
