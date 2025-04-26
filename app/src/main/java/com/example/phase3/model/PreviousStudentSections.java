package com.example.phase3.model;

public class PreviousStudentSections {
    //"section_id":"33","course_id":"COMP1010","semester":"Fall","year":"2024","student_name":"Thomas Martin","grade":"A"
    private String section_id;
    private String course_id;
    private String semester;
    private String year;
    private String student_name;
    private String grade;

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    @Override
    public String toString() {
        return "PastStudentsSections{" +
                "section_id='" + section_id + '\'' +
                ", course_id='" + course_id + '\'' +
                ", semester='" + semester + '\'' +
                ", year='" + year + '\'' +
                ", student_name='" + student_name + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }

}
