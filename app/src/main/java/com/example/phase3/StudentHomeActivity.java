package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentHomeActivity extends AppCompatActivity {

    private Button courseRegistrationButton, academicHistoryButton, myScheduleButton, alertsButton, studentLogoutButton;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);


        alertsButton = findViewById(R.id.gradesAndAlertsButton);
        studentLogoutButton = findViewById(R.id.studentLogoutButton);
        courseRegistrationButton = findViewById(R.id.courseRegistrationButton);
        academicHistoryButton = findViewById(R.id.academicHistoryButton);
        myScheduleButton = findViewById(R.id.myScheduleButton);



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
                Intent intent = new Intent(StudentHomeActivity.this, StudentAlertsActivity.class);
                intent.putExtra("email", userEmail); // pass the email
                startActivity(intent);
            }
        });

        academicHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openAcademicHistory();}
        });

        myScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openMySchedule();}
        });

    }

    private void openCourseRegistration() {
        Intent intent = new Intent(StudentHomeActivity.this, CourseRegistrationActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void openMySchedule() {
        Intent intent = new Intent(StudentHomeActivity.this, StudentScheduleActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void openAcademicHistory(){
        Intent intent = new Intent(StudentHomeActivity.this, StudentAcademicHistoryActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void openAcademicAlerts() {
        Intent intent = new Intent(StudentHomeActivity.this, StudentAlertsActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void logout() {
        // intent here
    }
}
