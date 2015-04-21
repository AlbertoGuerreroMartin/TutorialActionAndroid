package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by albertoguerreromartin on 21/04/15.
 */
public class TutorshipDay {

    public static final SimpleSerializer<TutorshipDay> SERIALIZER = new SimpleSerializer<>(TutorshipDay.class);

    @Expose private int timetableid;
    @Expose private String date;
    @Expose private String hour;
    @Expose private int duration;

    public int getTimetableid() {
        return timetableid;
    }

    public String getDateString() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public CalendarDay getCalendarDay() {
        CalendarDay calendarDay;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            calendarDay = new CalendarDay(format.parse(date));
        } catch (ParseException e) {
            calendarDay = null;
        }

        return calendarDay;
    }

    public int getDuration() {
        return duration;
    }
}
