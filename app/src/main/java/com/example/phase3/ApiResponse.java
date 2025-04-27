package com.example.phase3;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message; // optional; may be null in a success case

    // optional parameters required for registration, academic history and schedule
    private List<Course> courses; // optional; may be null in a failure case
    private List<Grade> grades;

    @SerializedName("total_credits")
    private int totalCredits;

    private double gpa;
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public List<Course> getAvailableCourses() {
        return courses;
    }
    public List<Grade> getAcademicHistory() {return grades; }

    public int getTotalCredits() { return totalCredits; }

    public double getGpa() { return gpa; }

}

