package com.example.phase3; // defines package for app and where to find app, unique location

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button instructorButton;
    private Button studentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // always runs when any screen is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // points to main screen layout
        // wiring up, where to look for buttons/text/any field
        instructorButton = findViewById(R.id.instructorButton);
        studentButton = findViewById(R.id.studentButton);

        instructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // when button is clicked
                Intent intent = new Intent(MainActivity.this, LoginActivity.class); // launch LoginActivity screen
                intent.putExtra("role", "instructor"); // send the role depending on users choice
                startActivity(intent); // actually run LoginActivity and show the page
            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentChoiceActivity.class); // no extra sent here, role is only useful for LoginActivity
                startActivity(intent);  // actually run StudentChoiceActivity and show the page
            }
        });
    }
}
