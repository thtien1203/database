package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentChoiceActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choice);

        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(StudentChoiceActivity.this, LoginActivity.class);
            intent.putExtra("role", "student");
            startActivity(intent);
        });

        createAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(StudentChoiceActivity.this, studentRegister.class); // rename to CreateStudentAccountActivity
            startActivity(intent);
        });

    }
}
