package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class StudentAcademicHistoryActivity extends AppCompatActivity {

    private LinearLayout academicHistoryLayout;
    private LinearLayout academicHistoryContainer;
    private ApiService apiService;

    private Spinner semesterSpinner;
    private Spinner yearSpinner;

    private String userEmail;

    private TextView totalCreditsTextView;
    private TextView gpaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_academic_history);

        academicHistoryLayout = findViewById(R.id.academicHistoryLayout); // parent layout
        academicHistoryContainer = findViewById(R.id.academicHistoryContainer);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        totalCreditsTextView = findViewById(R.id.totalCredits); // TextView for total credits
        gpaTextView = findViewById(R.id.gpa); // TextView for GPA

        userEmail = getIntent().getStringExtra("email");

        String baseurl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseurl);
        setupSemester();
        setupYear();

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getStudentAcademicHistory(); // fetch course history when semester changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getStudentAcademicHistory(); // fetch course history when year changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                "2025"
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
    }

    private void getStudentAcademicHistory() {
        String semester = semesterSpinner.getSelectedItem().toString();
        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        Call<ApiResponse> call = apiService.getStudentAcademicHistory(userEmail, semester, year);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) { // will be mapped correctly by Retrofit
                    List<Grade> academicHistory = response.body().getAcademicHistory();
                    int totalCredits = response.body().getTotalCredits();
                    double gpa = response.body().getGpa();

                    Log.d("GPA AND CREDITS DEBUG", String.valueOf(totalCredits));
                    Log.d("GPA AND CREDITS DEBUG", String.valueOf(gpa));

                    displayStudentHistory(academicHistory);
                    totalCreditsTextView.setText(String.valueOf(totalCredits));
                    gpaTextView.setText(String.format("%.2f", gpa));
                } else {
                    Toast.makeText(StudentAcademicHistoryActivity.this, "Failed to fetch course history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(StudentAcademicHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("getStudentCourseHistory", t.getMessage(), t);
            }
        });
    }

    // create and add a course item for course history
    private void displayStudentHistory(List<Grade> grades) {
        academicHistoryContainer.removeAllViews(); // clear previous views

        if (grades == null || grades.isEmpty()) {
            // Show the "No courses" message and hide the course list
            findViewById(R.id.noCoursesMessage).setVisibility(View.VISIBLE);
            academicHistoryContainer.setVisibility(View.GONE);
        } else {
            // Hide the "No courses" message and show the course list
            findViewById(R.id.noCoursesMessage).setVisibility(View.GONE);
            academicHistoryContainer.setVisibility(View.VISIBLE);

            // Populate the academic history with course items
            for (Grade grade : grades) {
                Log.d("GRADE", grade.toString());
                View gradeView = getLayoutInflater().inflate(R.layout.item_student_history, null);

                TextView courseIdSection = gradeView.findViewById(R.id.courseIdSection);
                TextView courseName = gradeView.findViewById(R.id.courseName);
                TextView courseInstructor = gradeView.findViewById(R.id.courseInstructor);
                TextView courseGrade = gradeView.findViewById(R.id.finalGrade);

                courseGrade.setText(grade.getCurrentGrade()); // display the student's grade in this course

                Course course = grade.getCourse();
                courseIdSection.setText(course.getFormattedCourseSection());
                courseName.setText(course.getCourseName());
                courseInstructor.setText(course.getInstructor());

                academicHistoryContainer.addView(gradeView);
            }
        }
    }

}
