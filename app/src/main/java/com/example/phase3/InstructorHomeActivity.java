package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InstructorHomeActivity extends AppCompatActivity {

    private Button viewSectionsButton, submitGradesButton, currentStudentsButton, instructorLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home);

        viewSectionsButton = findViewById(R.id.viewSectionsButton);
        submitGradesButton = findViewById(R.id.submitGradesButton);
        currentStudentsButton = findViewById(R.id.currentStudentsButton);
        instructorLogoutButton = findViewById(R.id.instructorLogoutButton);

        // set up onClick listeners below
        submitGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubmitGrades();
            }
        });
    }


    private void openViewTaughtSections() {
        // intent here
    }

    private void openSubmitGrades() {
        Intent intent = new Intent(InstructorHomeActivity.this, InstructorGradeActivity.class);
        startActivity(intent);
    }

    private void openViewCurrentStudents() {
        // intent here
    }

    private void logout() {
        // intent here
    }
}
