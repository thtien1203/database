package com.example.phase3;


import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phase3.model.CurrentStudentsSections;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorCurrentStudents extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inst_curr_students);
        tableLayout = findViewById(R.id.tableLayout);

        ApiService apiService = RetrofitClient.getClient(getString(R.string.url)).create(ApiService.class);


        String email = getIntent().getStringExtra("email");

        Call<List<CurrentStudentsSections>> call = apiService.getInstructorCurrentStudents(email);

        call.enqueue(new Callback<List<CurrentStudentsSections>>() {
            @Override
            public void onResponse(Call<List<CurrentStudentsSections>> call, Response<List<CurrentStudentsSections>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CurrentStudentsSections> sections = response.body();
                    for (CurrentStudentsSections section : sections) {
                        TableRow(section);
                    }
                } else {
                    Toast.makeText(InstructorCurrentStudents.this, "Server gave error", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<CurrentStudentsSections>> call, Throwable t) {
                Toast.makeText(InstructorCurrentStudents.this, "Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void TableRow(CurrentStudentsSections section) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        row.addView(block(section.getCourse_id()));
        row.addView(block(section.getSection_id()));
        row.addView(block(section.getStudent_name()));

        tableLayout.addView(row);
    }

    private TextView block(String text) {
        TextView block = new TextView(this);
        block.setText(text);
        return block;
    }
}