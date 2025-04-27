package com.example.phase3;

import com.google.gson.annotations.SerializedName;

    public class Grade {

        @SerializedName("student_id")
        private int studentId;
        private Course course;
        @SerializedName("current_grade")
        private String currentGrade;

    public Grade(int studentId, Course course, String currentGrade) {
        this.studentId = studentId;
        this.course = course;
        this.currentGrade = currentGrade;
    }

    // Getters and Setters
    public Course getCourse(){
        return course;
    }

    public void setCourse(Course course){
        this.course = course;
    }
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }


    @Override
    public String toString() {
        return "Grade{" +
                "studentId='" + studentId + '\'' +
                ", courseId='" + course.getCourseId()+ '\'' +
                ", courseName='" + course.getCourseName() + '\'' +
                ", sectionId='" + course.getSectionId() + '\'' +
                ", semester='" + course.getSemester() + '\'' +
                ", year=" + course.getYear() +
                ", instructor='" + course.getInstructor()+ '\'' +
                ", currentGrade='" + currentGrade + '\'' +
                ", credits=" + course.getCredits() +
                '}';
    }


}

