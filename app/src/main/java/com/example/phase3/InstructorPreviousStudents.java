package com.example.phase3;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phase3.model.PreviousStudentSections;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorPreviousStudents extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inst_prev_students);
        tableLayout = findViewById(R.id.tableLayout);

        ApiService apiService = RetrofitClient.getClient(getString(R.string.url)).create(ApiService.class);
        String email = getIntent().getStringExtra("email");

        Call<List<PreviousStudentSections>> call = apiService.getPreviousStudentSections(email);

        call.enqueue(new Callback<List<PreviousStudentSections>>() {
            @Override
            public void onResponse(Call<List<PreviousStudentSections>> call, Response<List<PreviousStudentSections>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PreviousStudentSections> sections = response.body();
                    for (PreviousStudentSections section : sections) {
                        TableRow(section);
                    }
                } else {
                    Toast.makeText(InstructorPreviousStudents.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PreviousStudentSections>> call, Throwable t) {
                Toast.makeText(InstructorPreviousStudents.this, "Failure for Prev Students", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void TableRow(PreviousStudentSections section) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        row.addView(block(section.getCourse_id()));
        row.addView(block(section.getSection_id()));
        row.addView(block(section.getSemester()));
        row.addView(block(section.getYear()));
        row.addView(block(section.getStudent_name()));
        row.addView(block(section.getGrade()));

        tableLayout.addView(row);
    }

    private TextView block(String text) {
        TextView block = new TextView(this);
        block.setText(text);
        return block;
    }
}