package com.example.phase3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class InstructorGradeActivity extends AppCompatActivity{
    private Spinner sectionSpinner;
    private ListView studentListView;
    private Button submitGradesButton;
    private Button backButton;
    private ProgressBar progressBar;
    private TextView noSectionsTextView;

    private ApiService apiService;
    private String instructorEmail;
    private List<Section> sections = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private Map<String, String> studentGrades = new HashMap<>();
    private Section selectedSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_grade);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        studentListView = findViewById(R.id.studentListView);
        submitGradesButton = findViewById(R.id.submitGradesButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        noSectionsTextView = findViewById(R.id.noSectionsTextView);

        // get instructor email from shared preferences
        instructorEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("email", "");
        String baseUrl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseUrl);

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>parent, View view, int position, long id) {
                if (position >= 0 && position < sections.size()) {
                    selectedSection = sections.get(position);
                    loadStudentsInSection(selectedSection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        submitGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGrades();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // LOAD SECTIONS TAUGHT BY THIS INSTRUCTOR
        loadSections();
    }

    private void loadSections() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Section>> call = apiService.getInstructorSections(instructorEmail);
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(Call<List<Section>> call, Response<List<Section>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    sections = response.body();

                    if (sections.isEmpty()) {
                        noSectionsTextView.setVisibility(View.VISIBLE);
                        sectionSpinner.setVisibility(View.GONE);
                        studentListView.setVisibility(View.GONE);
                        submitGradesButton.setEnabled(false);
                    } else {
                        noSectionsTextView.setVisibility(View.GONE);
                        sectionSpinner.setVisibility(View.VISIBLE);
                        displaySections();
                    }
                } else {
                    Toast.makeText(InstructorGradeActivity.this,
                            "Failed to load sections",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InstructorGradeActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySections() {
        List<String> sectionNames = new ArrayList<>();
        for (Section section : sections) {
            sectionNames.add(section.getCourseId() + " - " + section.getCourseName() +
                    " (Section: " + section.getSectionId() + ", " +
                    section.getSemester() + " " + section.getYear() + ")");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sectionNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(adapter);

        // select first section by default
        if (!sections.isEmpty()) {
            sectionSpinner.setSelection(0);
        }
    }

    private void loadStudentsInSection(Section section) {
        progressBar.setVisibility(View.VISIBLE);
        studentGrades.clear();  // clear previous grades

        Call<List<Student>> call = apiService.getStudentsInSection(
                section.getCourseId(),
                section.getSectionId(),
                section.getSemester(),
                section.getYear(),
                instructorEmail
        );
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    students = response.body();

                    if (students.isEmpty()) {
                        Toast.makeText(InstructorGradeActivity.this,
                                "No students found in this section",
                                Toast.LENGTH_SHORT).show();
                        studentListView.setVisibility(View.GONE);
                        submitGradesButton.setEnabled(false);
                    } else {
                        studentListView.setVisibility(View.VISIBLE);
                        submitGradesButton.setEnabled(true);
                        displayStudentsList();
                    }
                } else {
                    Toast.makeText(InstructorGradeActivity.this,
                            "Failed to load students",
                            Toast.LENGTH_SHORT).show();
                    studentListView.setVisibility(View.GONE);
                    submitGradesButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InstructorGradeActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                studentListView.setVisibility(View.GONE);
                submitGradesButton.setEnabled(false);
            }
        });
    }

    private void displayStudentsList() {
        // create a custom adapter to display students with grade selection
        StudentGradeAdapter adapter = new StudentGradeAdapter(this, students, studentGrades);
        studentListView.setAdapter(adapter);
    }
    private void submitGrades() {
        if (selectedSection == null) {
            Toast.makeText(this, "Please select a section first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentGrades.isEmpty()) {
            Toast.makeText(this, "Please assign grades to at least one student", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        submitGradesButton.setEnabled(false);

        Gson gson = new Gson();
        String gradesJson = gson.toJson(studentGrades);

        Call<ApiResponse> call = apiService.submitGrades(
                selectedSection.getCourseId(),
                selectedSection.getSectionId(),
                selectedSection.getSemester(),
                selectedSection.getYear(),
                gradesJson,
                "submit_grades",
                instructorEmail
        );
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(InstructorGradeActivity.this,
                                "Grades submitted successfully",
                                Toast.LENGTH_SHORT).show();
                        studentGrades.clear();
                        loadStudentsInSection(selectedSection); // Refresh the student list
                    } else {
                        submitGradesButton.setEnabled(true);
                        Toast.makeText(InstructorGradeActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    submitGradesButton.setEnabled(true);
                    Toast.makeText(InstructorGradeActivity.this,
                            "Failed to submit grades",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                submitGradesButton.setEnabled(true);
                Toast.makeText(InstructorGradeActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
