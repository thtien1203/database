package com.example.phase3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnInstructor;
    Button btnStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // links to XML

        btnInstructor = findViewById(R.id.instructorAccessButton);
        btnStudent = findViewById(R.id.studentAccessButton);

        btnInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instructorIntent = new Intent(MainActivity.this, InstructorLoginActivity.class);
                startActivity(instructorIntent);
            }
        });

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentIntent = new Intent(MainActivity.this, StudentLoginActivity.class);
                startActivity(studentIntent);
            }
        });
    }
}
