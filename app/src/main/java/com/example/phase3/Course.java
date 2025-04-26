package com.example.phase3;

import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("course_id")
    private String courseId;
    @SerializedName("course_name")
    private String courseName;
    private String semester;
    private int year;
    @SerializedName("section_id")
    private String sectionId;
    @SerializedName("instructor_name")
    private String instructor;
    private String building;
    @SerializedName("room_number")
    private String roomNumber;
    private String day;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    private int capacity;
    @SerializedName("students_enrolled")
    private int studentsEnrolled;
    private int credits;

    // Constructor
    public Course(String courseId, String courseName, String semester, int year, String sectionId, String instructor,
                  String building, String roomNumber, String day, String startTime, String endTime,
                  int capacity, int studentsEnrolled, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.semester = semester;
        this.year = year;
        this.sectionId = sectionId;
        this.instructor = instructor;
        this.building = building;
        this.roomNumber = roomNumber;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.studentsEnrolled = studentsEnrolled;
        this.credits = credits;
    }

    // Getters and Setters

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

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getFormattedCourseSection(){
        return courseId + " - " + sectionId;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    // Getters and setters for LocalTime fields
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public void setStudentsEnrolled(int studentsEnrolled) {
        this.studentsEnrolled = studentsEnrolled;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    // Convenience methods
    public String getFormattedLocation() {
        return building + " " + roomNumber;
    }

    public String getFormattedTime() {
        return day + " " + startTime + " - " + endTime;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", semester='" + semester + '\'' +
                ", year='" + year + '\'' +
                ", section='" + sectionId + '\'' +
                ", instructor='" + instructor + '\'' +
                ", building='" + building + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", capacity=" + capacity +
                ", studentsEnrolled=" + studentsEnrolled +
                ", credits=" + credits +
                '}';
    }
}
