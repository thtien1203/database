package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InstructorHomeActivity extends AppCompatActivity {

    private Button viewSectionsButton, submitGradesButton, currentStudentsButton, previousStudentsButton, instructorLogoutButton;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home);

        email = getIntent().getStringExtra("email");

        viewSectionsButton = findViewById(R.id.viewSectionsButton);
        submitGradesButton = findViewById(R.id.submitGradesButton);
        currentStudentsButton = findViewById(R.id.currentStudentsButton);
        previousStudentsButton = findViewById(R.id.previousStudentsButton);
        instructorLogoutButton = findViewById(R.id.instructorLogoutButton);

        // set up onClick listeners below
        viewSectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewTaughtSections();
            }
        });
        submitGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubmitGrades();
            }
        });
        currentStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewCurrentStudents();
            }
        });
        previousStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewPreviousStudents();
            }
        });
        instructorLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


    }

    private void openViewTaughtSections() {
        Intent myIntent = new Intent(InstructorHomeActivity.this, InstructorTaughtCourses.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);
    }


    private void openSubmitGrades() {
        Intent intent = new Intent(InstructorHomeActivity.this, InstructorGradeActivity.class);
        startActivity(intent);
    }

    private void openViewCurrentStudents() {
        Intent myIntent = new Intent(InstructorHomeActivity.this, InstructorCurrentStudents.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);
    }

    private void openViewPreviousStudents() {
        Intent myIntent = new Intent(InstructorHomeActivity.this, InstructorPreviousStudents.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);
    }

    private void logout() {
        Intent myIntent = new Intent(InstructorHomeActivity.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // cant use back button
        startActivity(myIntent);
        finish();
    }

}
