package com.example.phase3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentHomeActivity extends AppCompatActivity {

    private Button courseRegistrationButton, academicHistoryButton, alertsButton, studentLogoutButton;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);


        alertsButton = findViewById(R.id.alertsButton);
        studentLogoutButton = findViewById(R.id.studentLogoutButton);
        courseRegistrationButton = findViewById(R.id.courseRegistrationButton);
        academicHistoryButton = findViewById(R.id.academicHistoryButton);


        userEmail = getIntent().getStringExtra("email");

        courseRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCourseRegistration();// when button is clicked
            }
        });
        // set up onClick listeners below
        alertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAcademicAlerts();
            }
        });

        academicHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openAcademicHistory();}
        });

    }

    private void openCourseRegistration() {
        Intent intent = new Intent(StudentHomeActivity.this, CourseRegistrationActivity.class);
        intent.putExtra("email", userEmail); // pass user email to the next activity
        startActivity(intent);
    }

    private void openMySchedule() {
        // intent here
    }

    private void openAcademicHistory(){
        Intent intent = new Intent(StudentHomeActivity.this, StudentAcademicHistoryActivity.class);
        intent.putExtra("email", userEmail); // pass user email to the next activity
        startActivity(intent);
    }

    private void openAcademicAlerts() {
        Intent intent = new Intent(StudentHomeActivity.this, StudentAlertsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        // intent here
    }
}
