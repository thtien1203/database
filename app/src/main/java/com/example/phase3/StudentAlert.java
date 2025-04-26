package com.example.phase3;

public class StudentAlert {
    private int alertId;
    private String studentId;
    private String courseId;
    private String courseName;
    private String sectionId;
    private String semester;
    private int year;
    private String alertType;
    private String alertMessage;
    private String alertDate;
    private boolean isRead;

    public StudentAlert(int alertId, String studentId, String courseId, String courseName,
                        String sectionId, String semester, int year, String alertType,
                        String alertMessage, String alertDate, boolean isRead) {
        this.alertId = alertId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.sectionId = sectionId;
        this.semester = semester;
        this.year = year;
        this.alertType = alertType;
        this.alertMessage = alertMessage;
        this.alertDate = alertDate;
        this.isRead = isRead;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

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

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public String getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(String alertDate) {
        this.alertDate = alertDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
