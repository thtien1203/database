package com.example.phase3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class StudentAlertsActivity extends AppCompatActivity {
    private TabHost tabHost;
    private ListView alertsListView;
    private ListView gradesListView;
    private Button markReadButton;
    private Button backButton;
    private ProgressBar progressBar;
    private TextView noAlertsTextView;
    private TextView noGradesTextView;
    private ApiService apiService;
    private String studentEmail;
    private List<StudentAlert> alerts = new ArrayList<>();
    private List<Integer> selectedAlertIds = new ArrayList<>();
    private List<MidtermGrade> grades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_alerts);

        tabHost = findViewById(R.id.tabHost);
        alertsListView = findViewById(R.id.alertsListView);
        gradesListView = findViewById(R.id.gradesListView);
        markReadButton = findViewById(R.id.markReadButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        noAlertsTextView = findViewById(R.id.noAlertsTextView);
        noGradesTextView = findViewById(R.id.noGradesTextView);

        // Get student email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        studentEmail = prefs.getString("email", "");

        // set up tabs
        tabHost.setup();
        TabHost.TabSpec alertsTab = tabHost.newTabSpec("alerts");
        alertsTab.setIndicator("Academic Alerts");
        alertsTab.setContent(R.id.alertsTab);
        tabHost.addTab(alertsTab);

        TabHost.TabSpec gradesTab = tabHost.newTabSpec("grades");
        gradesTab.setIndicator("Midterm Grades");
        gradesTab.setContent(R.id.gradesTab);
        tabHost.addTab(gradesTab);

        studentEmail = getIntent().getStringExtra("email");
        // initialize api service
        String baseUrl = getString(R.string.url);
        apiService = RetrofitClient.getApiService(baseUrl);

        // set up button listeners
        markReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAlertsAsRead();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // load data
        loadAlerts();
        loadGrades();
    }
    private void loadAlerts() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<StudentAlert>> call = apiService.getStudentAlerts(studentEmail);
        call.enqueue(new Callback<List<StudentAlert>>() {
            @Override
            public void onResponse(Call<List<StudentAlert>> call, Response<List<StudentAlert>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    alerts = response.body();
                    if (alerts.isEmpty()) {
                        noAlertsTextView.setVisibility(View.VISIBLE);
                        alertsListView.setVisibility(View.GONE);
                        markReadButton.setEnabled(false);
                    } else {
                        noAlertsTextView.setVisibility(View.GONE);
                        alertsListView.setVisibility(View.VISIBLE);
                        displayAlerts();
                    }

                } else {
                    Toast.makeText(StudentAlertsActivity.this,
                            "Failed to load alerts",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StudentAlert>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StudentAlertsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadGrades() {
        Call<List<MidtermGrade>> call = apiService.getStudentGrades(studentEmail, "grades");
        call.enqueue(new Callback<List<MidtermGrade>>() {
            @Override
            public void onResponse(Call<List<MidtermGrade>> call, Response<List<MidtermGrade>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    grades = response.body();

                    if (grades.isEmpty()) {
                        noGradesTextView.setVisibility(View.VISIBLE);
                        gradesListView.setVisibility(View.GONE);
                    } else {
                        noGradesTextView.setVisibility(View.GONE);
                        gradesListView.setVisibility((View.VISIBLE));
                        displayGrades();
                    }
                } else {
                    Toast.makeText(StudentAlertsActivity.this,
                            "Failed to load grades",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MidtermGrade>> call, Throwable t) {
                Toast.makeText(StudentAlertsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAlerts() {
        AlertAdapter adapter = new AlertAdapter(this, alerts, selectedAlertIds);
        alertsListView.setAdapter(adapter);

        // check if there are any unread alert
        boolean hasUnreadAlerts = false;
        for (StudentAlert alert : alerts) {
            if (!alert.isRead()) {
                hasUnreadAlerts = true;
                break;
            }
        }
        markReadButton.setEnabled(hasUnreadAlerts);
    }
    private void displayGrades() {
        GradeAdapter adapter = new GradeAdapter(this, grades);
        gradesListView.setAdapter(adapter);
    }

    private void markAlertsAsRead() {
        if (selectedAlertIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one alert", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        markReadButton.setEnabled(false);

        Gson gson = new Gson();
        String alertIdsJson = gson.toJson(selectedAlertIds);

        Call<ApiResponse> call = apiService.markAlertsAsRead(alertIdsJson, "mark_read", studentEmail);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(StudentAlertsActivity.this,
                                "Alerts marked as read",
                                Toast.LENGTH_SHORT).show();

                        // Update local alert data
                        for (Integer alertId : selectedAlertIds) {
                            for (StudentAlert alert : alerts) {
                                if (alert.getAlertId() == alertId) {
                                    alert.setRead(true);
                                }
                            }
                        }

                        selectedAlertIds.clear();
                        displayAlerts();
                    } else {
                        markReadButton.setEnabled(true);
                        Toast.makeText(StudentAlertsActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    markReadButton.setEnabled(true);
                    Toast.makeText(StudentAlertsActivity.this,
                            "Failed to mark alerts as read",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                markReadButton.setEnabled(true);
                Toast.makeText(StudentAlertsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
