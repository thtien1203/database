package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentScheduleActivity extends AppCompatActivity {

    private LinearLayout studentScheduleLayout;
    private LinearLayout studentScheduleContainer;
    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);

        studentScheduleLayout = findViewById(R.id.studentScheduleLayout); // parent layout
        studentScheduleContainer = findViewById(R.id.studentScheduleContainer);

        userEmail = getIntent().getStringExtra("email");

        String baseurl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseurl);

        getStudentSchedule();
    }
    private void getStudentSchedule() {
        String semester = "Fall"; // change to Spring
        int year = 2023; // change to 2025

        Call<ApiResponse> call = apiService.getStudentSchedule(userEmail, semester, year);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) { // will be mapped correctly by Retrofit
                    List<Course> studentSchedule = response.body().getStudentSchedule();

                    displayStudentSchedule(studentSchedule);

                } else {
                    Toast.makeText(StudentScheduleActivity.this, "Failed to fetch course history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(StudentScheduleActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("getStudentCourseHistory", t.getMessage(), t);
            }
        });
    }

    // create and add a course item for schedule
    private void displayStudentSchedule(List<Course> courses) {
        studentScheduleContainer.removeAllViews(); // clear previous views if there

        for (Course course : courses) {
            Log.d("COURSE DEBUG", courses.toString());
            View scheduleView = getLayoutInflater().inflate(R.layout.item_student_schedule, null);

            TextView courseIdSection = scheduleView.findViewById(R.id.courseIdSection);
            TextView daysOfWeek = scheduleView.findViewById(R.id.daysOfWeek);
            TextView courseName = scheduleView.findViewById(R.id.courseName);
            TextView timeOfDay = scheduleView.findViewById(R.id.timeOfDay);
            TextView courseInstructor = scheduleView.findViewById(R.id.courseInstructor);
            TextView courseLocation = scheduleView.findViewById(R.id.courseLocation);

            courseIdSection.setText(course.getFormattedCourseSection());
            daysOfWeek.setText(course.getDay());
            courseName.setText(course.getCourseName());
            timeOfDay.setText(course.getFormattedTiming());
            courseInstructor.setText(course.getInstructor());
            courseLocation.setText(course.getFormattedLocation());

            // Create LayoutParams with only top margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Only set top margin
            int topMarginInPx = (int) (getResources().getDisplayMetrics().density * 12); // 12dp top margin
            params.topMargin = topMarginInPx;

            scheduleView.setLayoutParams(params);

            studentScheduleContainer.addView(scheduleView);
        }
    }

}


