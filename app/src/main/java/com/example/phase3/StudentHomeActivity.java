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

    private Button browseCoursesButton, viewCoursesButton, alertsButton, studentLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        browseCoursesButton = findViewById(R.id.browseCoursesButton);
        viewCoursesButton = findViewById(R.id.viewCoursesButton);
        alertsButton = findViewById(R.id.alertsButton);
        studentLogoutButton = findViewById(R.id.studentLogoutButton);

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

    }

    private void openBrowseCourses() {
        // intent here
    }

    private void openViewMyCourses() {
        // intent here
    }

    private void openAcademicAlerts() {
        // intent here
    }

    private void logout() {
        // intent here
    }
}
