package com.example.phase3;

import com.example.phase3.model.CurrentStudentsSections;
import com.example.phase3.model.InstructorSections;
import com.example.phase3.model.PreviousStudentSections;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message;
    private List<CurrentStudentsSections> currentstudentsections;
    private List<InstructorSections> instructorsections;

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

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }
    public  String getMessage() {
        return message;
    }

}