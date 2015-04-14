package com.edu.tutorialaction.entity;

import com.edu.tutorialaction.util.SimpleSerializer;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by albertoguerreromartin on 14/04/15.
 */
public class User {

    public static final SimpleSerializer<User> SERIALIZER = new SimpleSerializer<>(User.class);

    @Expose private int userID;
    @Expose private String username;
    @Expose private String firstname;
    @Expose private String lastname;
    @Expose private String email;
    @Expose private String role;
    @Expose private List<Course> courses;


    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public List<Course> getCourses() {
        return courses;
    }
}

