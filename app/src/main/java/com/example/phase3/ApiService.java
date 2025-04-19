package com.example.phase3;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
}

