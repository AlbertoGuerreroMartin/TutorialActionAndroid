package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by albertoguerreromartin on 14/04/15.
 */
public class Course {

    public static final SimpleSerializer<List<Course>> LIST_SERIALIZER = new SimpleSerializer<>(new TypeToken<List<Course>>(){} );

    @Expose private int courseID;
    @Expose private String courseName;
    @Expose private List<User> teachers;

    public Course(int courseID, String courseName, List<User> teachers) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.teachers = teachers;
    }

    public int getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<User> getTeachers() {
        return teachers;
    }

}
