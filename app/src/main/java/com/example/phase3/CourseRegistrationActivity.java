package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseRegistrationActivity extends AppCompatActivity {

    private LinearLayout courseListLayout;
    private ApiService apiService;

    private String semesterSelected;

    private int yearSelected;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

        courseListLayout = findViewById(R.id.courseListLayout); // parent layout to hold course items

        userEmail = getIntent().getStringExtra("email");

        String baseurl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseurl);
        setupSemester();
        setupYear();

        // get courses to populate scroll view
        getAvailableCourses();
    }

    private void setupSemester(){
    }

    private void setupYear(){
    }

    private void getAvailableCourses() {
        String semester = "Fall"; // semesterSelected.getSelectedItem().toString();
        int year = 2023; // yearSelected.getSelectedItem().toString();

        Call<ApiResponse> call = apiService.getAvailableCourses(semester, year, "Get Available Courses");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Course> courses = response.body().getAvailableCourses(); // Assuming response contains list of courses
                    if (courses != null) {
                        for (Course course : courses) {
                            // Debug logs to check fields
                            Log.d("COURSE_DEBUG", "Course Id: " + course.getCourseId());
                            Log.d("COURSE_DEBUG", "Instructor: " + course.getInstructor());
                            Log.d("COURSE_DEBUG", "Building: " + course.getBuilding());
                            Log.d("COURSE_DEBUG", "Room: " + course.getRoomNumber());
                            Log.d("COURSE_DEBUG", "Days: " + course.getDay());
                            Log.d("COURSE_DEBUG", "Start Time: " + course.getStartTime());
                            Log.d("COURSE_DEBUG", "End Time: " + course.getEndTime());

                            addCourseItem(course);
                        }
                    } else {
                        Toast.makeText(CourseRegistrationActivity.this, "No courses found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CourseRegistrationActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("COURSE_DEBUG", "Failure: " + t.getMessage());
                Toast.makeText(CourseRegistrationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to dynamically create and add a course item
    private void addCourseItem(Course course) {
        // Inflate course item layout
        View courseView = getLayoutInflater().inflate(R.layout.course_item, courseListLayout, false);

        // Get references to views in course item layout
        TextView courseName = courseView.findViewById(R.id.courseName);
        TextView courseInstructor = courseView.findViewById(R.id.courseInstructor);
        TextView courseLocation = courseView.findViewById(R.id.courseLocation);
        TextView courseTiming = courseView.findViewById(R.id.courseTiming);
        Button registerButton = courseView.findViewById(R.id.registerButton);

        // Set course title
        courseName.setText(course.getFormattedCourseSection()); // Assuming Course object has a getTitle() method
        courseInstructor.setText(course.getInstructor());
        courseLocation.setText(course.getFormattedLocation());
        courseTiming.setText(course.getFormattedTime());


        // Set up the register button (you can set an OnClickListener to handle registration)
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForCourse(course);
            }
        });

        // Add the new course item to the parent layout
        courseListLayout.addView(courseView);
    }

    // Handle course registration (this could involve another API call)
    private void registerForCourse(Course course) {

        //Call<ApiResponse> call = apiService.registerForCourse(userEmail, course.getSection() , "Get Available Courses");

        // For simplicity, let's just show a toast here
        Toast.makeText(this, "Registered for: " + course.getCourseId(), Toast.LENGTH_SHORT).show();
    }
}
