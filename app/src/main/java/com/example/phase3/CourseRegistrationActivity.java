package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseRegistrationActivity extends AppCompatActivity {

    private LinearLayout courseListLayout;

    private LinearLayout courseItemsContainer;
    private ApiService apiService;

    private Spinner semesterSpinner;
    private Spinner yearSpinner;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

        courseListLayout = findViewById(R.id.courseListLayout); // parent layout to hold course items
        courseItemsContainer = findViewById(R.id.courseItemsContainer);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        userEmail = getIntent().getStringExtra("email");

        String baseurl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseurl);
        setupSemester();
        setupYear();

        semesterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAvailableCourses(); // refresh courses when semester changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        yearSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAvailableCourses(); // refresh courses when year changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSemester(){
        List<String> semesters = new ArrayList<>(Arrays.asList(
                    "Fall",
                    "Spring"
            ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semesters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(adapter);
    }

    private void setupYear(){
        List<String> years = new ArrayList<>(Arrays.asList(
                "2023",
                "2024",
                "2025",
                "2026"
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
    }

    private void getAvailableCourses() {
        String semester = semesterSpinner.getSelectedItem().toString();
        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        Call<ApiResponse> call = apiService.getAvailableCourses(semester, year);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Course> courseList = response.body().getAvailableCourses();
                    displayCourses(courseList);
                } else {
                    Toast.makeText(CourseRegistrationActivity.this, "Failed to fetch courses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CourseRegistrationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("getAvailableCourses", t.getMessage(), t);
            }
        });
    }



    // create and add a course item
    private void displayCourses(List<Course> courses) {
        courseItemsContainer.removeAllViews(); // clear previous views if any
        TextView noCoursesMessage = findViewById(R.id.noCoursesMessage); // Find the "No courses" message TextView

        if (courses.isEmpty()) {
            noCoursesMessage.setVisibility(View.VISIBLE); // Show the "No courses" message if no courses are available
        } else {
            noCoursesMessage.setVisibility(View.GONE); // Hide the "No courses" message if courses are available
            for (Course course : courses) {
                Log.d("COURSE DEBUG:", course.toString());
                View courseView = getLayoutInflater().inflate(R.layout.item_course, null);

                TextView courseName = courseView.findViewById(R.id.courseName);
                TextView courseIdSection = courseView.findViewById(R.id.courseIdSection);
                TextView courseInstructor = courseView.findViewById(R.id.courseInstructor);
                TextView courseLocation = courseView.findViewById(R.id.courseLocation);
                TextView courseTiming = courseView.findViewById(R.id.courseTiming);
                Button registerButton = courseView.findViewById(R.id.registerButton);

                courseName.setText(course.getCourseName());
                courseInstructor.setText(course.getInstructor());
                courseLocation.setText(course.getFormattedLocation());
                courseTiming.setText(course.getFormattedDayAndTime());
                courseIdSection.setText(course.getFormattedCourseSection());

                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerForCourse(course);
                    }
                });

                courseItemsContainer.addView(courseView);
            }
        }
    }


    private void registerForCourse(Course course) {
        Log.d("REGISTER DEBUG", course.getCourseId());
        Log.d("REGISTER DEBUG", course.getSectionId());
        Log.d("REGISTER DEBUG", course.getSemester());
        Log.d("REGISTER DEBUG", String.valueOf(course.getYear()));
        Call<ApiResponse> call = apiService.registerForCourse(
                userEmail,
                course.getCourseId(),
                course.getSectionId(),
                course.getSemester(),
                course.getYear(),
                "register"
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(CourseRegistrationActivity.this,
                                "Registered Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CourseRegistrationActivity.this,
                                "Registration Failed: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CourseRegistrationActivity.this,
                            "Registration Failed: Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CourseRegistrationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
