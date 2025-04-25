package com.example.phase3;

public class Section {
    private String courseId;
    private String courseName;
    private String sectionId;
    private String semester;
    private int year;

    public Section(String courseId, String courseName, String sectionId, String semester, int year) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.sectionId = sectionId;
        this.semester = semester;
        this.year = year;
    }

    // Getters and setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}