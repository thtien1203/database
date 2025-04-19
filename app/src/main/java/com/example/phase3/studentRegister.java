package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class studentRegister extends AppCompatActivity {
    private EditText studentIdEditText, nameEditText, emailEditText, passwordEditText;
    private Spinner departmentS, studentTypeS;
    private Button createAccountButton;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle saveState) {
        super.onCreate(saveState);
        setContentView(R.layout.student_register);

        studentIdEditText = findViewById(R.id.studentIdEditText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        departmentS= findViewById(R.id.departmentSpinner);
        studentTypeS = findViewById(R.id.studentTypeSpinner);
        createAccountButton = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.progressBar);

        String baseurl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseurl);
        setupDepartment();
        setupStudentType();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createStudentAcc();
            }
        });
    }
    private void setupDepartment() {
        List<String> departments = new ArrayList<>(Arrays.asList(
                "Department of Biology",
                "Department of Electrical Engineering",
                "Department of Physics",
                "Miner School of Computer & Information Sciences"
        ));
        // create an adapter to display the departments in the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                departments
        );
        // dropdown the items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // apply the adapter to spinner
        departmentS.setAdapter(adapter);
    }

    private void setupStudentType() {
        List<String> studentTypes = new ArrayList<>(Arrays.asList(
                "Undergraduate",
                "Master",
                "PhD"
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                studentTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentTypeS.setAdapter(adapter);
    }

    private void createStudentAcc() {
        String studentId = studentIdEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String department = departmentS.getSelectedItem().toString();
        String studentType = studentTypeS.getSelectedItem().toString();

        // validate input
        if (studentId.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || department.isEmpty() || studentType.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // show progress bar
        progressBar.setVisibility(View.VISIBLE);
        createAccountButton.setEnabled(false);

        // make api call
        Call<ApiResponse> call = apiService.createStudentAccount(
                studentId,
                name,
                email,
                password,
                department,
                studentType,
                "Create Account"
        );

        // add response handler
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                createAccountButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(
                                studentRegister.this,
                                "Account created successfully",
                                Toast.LENGTH_SHORT
                        ).show();;
                        finish();;
                    } else {
                        Toast.makeText(
                                studentRegister.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    Toast.makeText(
                            studentRegister.this,
                            "Failed to create account",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                createAccountButton.setEnabled(true);
                Toast.makeText(
                        studentRegister.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

}
