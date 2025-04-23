package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentHomeActivity extends AppCompatActivity {

    private Button courseRegistrationButton, myScheduleButton, alertsButton, studentLogoutButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        courseRegistrationButton = findViewById(R.id.courseRegistrationButton);
        myScheduleButton = findViewById(R.id.myScheduleButton);
        alertsButton = findViewById(R.id.alertsButton);
        studentLogoutButton = findViewById(R.id.studentLogoutButton);

        userEmail = getIntent().getStringExtra("email");

        courseRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCourseRegistration();// when button is clicked
            }
        });

    }

    private void openCourseRegistration() {
        Intent intent = new Intent(StudentHomeActivity.this, CourseRegistrationActivity.class); // launch Course Registration screen
        intent.putExtra("email", userEmail); // send the role depending on users choice
        startActivity(intent); // actually run the activity
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
