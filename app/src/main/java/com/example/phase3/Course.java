package com.example.phase3;

import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("section_id")
    private String section;

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

    // Constructor
    public Course(String courseId, String section, String instructor,
                  String building, String roomNumber,
                  String day, String startTime, String endTime,
                  int capacity, int studentsEnrolled) {
        this.courseId = courseId;
        this.section = section;
        this.instructor = instructor;
        this.building = building;
        this.roomNumber = roomNumber;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.studentsEnrolled = studentsEnrolled;
    }

    // Getters and Setters

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getFormattedCourseSection(){
        return courseId + " - " + section;
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
                ", section='" + section + '\'' +
                ", instructor='" + instructor + '\'' +
                ", building='" + building + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", capacity=" + capacity +
                ", studentsEnrolled=" + studentsEnrolled +
                '}';
    }
}
