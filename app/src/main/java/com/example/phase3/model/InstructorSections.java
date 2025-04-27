package com.example.phase3.model;

public class InstructorSections {
    private String course_id;
    private String section_id;
    private String semester;
    private String year;

    @Override
    public String toString() {
        return "InstructorSections{" +
                "course_id='" + course_id + '\'' +
                ", section_id='" + section_id + '\'' +
                ", semester='" + semester + '\'' +
                ", year='" + year + '\'' +
                '}';
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getSection_id() {
        return section_id;
    }

    public String getSemester() {
        return semester;
    }

    public String getYear() {
        return year;
    }

}
