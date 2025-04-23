package com.example.phase3;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message; // optional; may be null in a success case
    private List<Course> courses; // optional; may be null in a failure case

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Course> getAvailableCourses() {
        return courses;
    }
}

