package com.edu.tutorialaction.entity;

/**
 * Created by albertoguerreromartin on 16/04/15.
 */
public enum TutorshipType {
    TEACHING ("Docente"),
    ACADEMICS ("Académica");

    private final String rawValue;

    TutorshipType(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }
}
