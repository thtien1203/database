package com.example.phase3;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("student.php")
    Call<ApiResponse> createStudentAccount(
            @Field("student_id") String studentId,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("dept_name") String department,
            @Field("student_type") String studentType,
            @Field("submit") String submit
    );
    @FormUrlEncoded // format of request
    @POST("login.php") // the endpoint of my webserver that is recieving the request
    Call<ApiResponse> login( // the login method is called
            @Field("email") String email,
            @Field("password") String password,
            @Field("role") String role, // added role parameter to allow for both student and instructor login in one page
            @Field("submit") String submit
    );

    @FormUrlEncoded // format of request
    @POST("student_register.php") // the endpoint of my webserver that is recieving the request
    Call<ApiResponse> studentRegister( // the login method is called
                             @Field("email") String email,
                             @Field("password") String password,
                             @Field("role") String role, // added role parameter to allow for both student and instructor login in one page
                             @Field("submit") String submit
    );



    @GET("get_available_courses.php")
    Call<ApiResponse> getAvailableCourses(
            @Query("semester") String semester,
            @Query("year") int year
    );

    @GET("student.alerts.php")
    Call<List<StudentAlert>> getStudentAlerts(@Query("email") String email);

    @FormUrlEncoded
    @POST("student_alerts.php")
    Call<ApiResponse> markAlertsAsRead(
            @Field("alert_ids") String alertIdsJson,
            @Field("mark_read") String markReadFlag,
            @Field("email") String email
    );

    @GET("student_alerts.php")
    Call<List<MidtermGrade>> getStudentGrades(
            @Query("email") String email,
            @Query("type") String type);


    @GET("instructor_grade.php")
    Call<List<Section>> getInstructorSections(@Query("email")String email);

    @GET("instructor_grade.php")
    Call<List<Student>> getStudentsInSection(
            @Query("course_id") String courseId,
            @Query("section_id") String sectionId,
            @Query("semester") String semester,
            @Query("year") int year,
            @Query("instructor_email") String instructorEmail
    );
    @FormUrlEncoded
    @POST("instructor_grade.php")
    Call<ApiResponse> submitGrades(
            @Field("course_id") String courseId,
            @Field("section_id") String sectionId,
            @Field("semester") String semester,
            @Field("year") int year,
            @Field("grades") String gradesJson,
            @Field("submit_grades") String submitFlag,
            @Field("instructor_email") String instructorEmail
    );

    @FormUrlEncoded
    @POST("register_for_course.php")
    Call<ApiResponse> registerForCourse(
            @Field("email") String userEmail,
            @Field("course_id") String courseId,
            @Field("section_id") String sectionId,
            @Field("semester") String semester,
            @Field("year") int year,
            @Field("register") String submitFlag
    );

}

