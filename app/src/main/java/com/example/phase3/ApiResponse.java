package com.example.phase3;

import com.google.gson.annotations.SerializedName;

import com.example.phase3.model.CurrentStudentsSections;
import com.example.phase3.model.InstructorSections;
import com.example.phase3.model.PreviousStudentSections;
import java.util.List;

public class ApiResponse {
    private boolean success;

    private String message; // optional; may be null in a success case

    // optional parameters required for registration, academic history and schedule
    private List<Course> courses; // optional; may be null in a failure case
    private List<Grade> grades;
    private List<Course> schedule;
    private List<CurrentStudentsSections> currentstudentsections;
    private List<InstructorSections> instructorsections;

    @SerializedName("total_credits")
    private int totalCredits;

    private double gpa;
    private List<PreviousStudentSections> previousStudentSections;

    public List<PreviousStudentSections> getPreviousStudentSections() {
        return previousStudentSections;
    }

    public void setPastStudentsSections(List<PreviousStudentSections> previousStudentSections) {
        this.previousStudentSections = previousStudentSections;
    }

    public List<InstructorSections> getInstructorsections() {
        return instructorsections;
    }

    public void setInstructorsections(List<InstructorSections> instructorsections) {
        this.instructorsections = instructorsections;
    }

    public List<CurrentStudentsSections> getCurrentstudentsections() {
        return currentstudentsections;
    }

    public void setCurrentstudentsections(List<CurrentStudentsSections> currentstudentsections) {
        this.currentstudentsections = currentstudentsections;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public List<Course> getAvailableCourses() { return courses; }
    public List<Grade> getAcademicHistory() {return grades; }
    public List<Course> getStudentSchedule() {return schedule; }
    public int getTotalCredits() { return totalCredits; }

    public double getGpa() { return gpa; }

}
