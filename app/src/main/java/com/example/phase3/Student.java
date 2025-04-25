package com.example.phase3;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String password;
    private String department;
    private String studentType;

    private String currentGrade; // added for grade submission

    // constructor for registration
    public Student(String studentId, String name, String email, String password, String department, String studentType) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.studentType = studentType;
    }

    // constructor for grade submission
    // Constructor for grade submission
    public Student(String studentId, String name, String currentGrade) {
        this.studentId = studentId;
        this.name = name;
        this.currentGrade = currentGrade;
    }

    // getters and setters
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentType() {
        return studentType;
    }
    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getCurrentGrade() {
        return currentGrade;
    }
    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }

}