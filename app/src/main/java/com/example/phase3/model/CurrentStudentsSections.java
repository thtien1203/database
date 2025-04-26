package com.example.phase3.model;

public class CurrentStudentsSections {

    private String course_id;
    private String section_id;

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    private String student_name;

    public String getCourse_id() {
        return course_id;
    }



    public String getStudent_name() {
        return student_name;
    }


    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }



    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    @Override
    public String toString() {
        return "CurrentStudentsSections{" +
                "course_id='" + course_id + '\'' +
                ", section='" + section_id + '\'' +
                ", student_name='" + student_name + '\'' +
                '}';
    }
}
