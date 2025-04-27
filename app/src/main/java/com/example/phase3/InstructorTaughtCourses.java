 package com.example.phase3;

 import android.os.Bundle;
 import android.widget.TableLayout;
 import android.widget.TableRow;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;

 import com.example.phase3.model.InstructorSections;

 import java.util.List;

 import retrofit2.Call;
 import retrofit2.Callback;
 import retrofit2.Response;

 public class InstructorTaughtCourses extends AppCompatActivity {

     private TableLayout tableLayout;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.taught_sections);
         tableLayout = findViewById(R.id.tableLayout);

         ApiService apiService = RetrofitClient.getClient(getString(R.string.url)).create(ApiService.class);


         String email = getIntent().getStringExtra("email");

         Call<List<InstructorSections>> call = apiService.getInstructorSections(getIntent().getStringExtra("email"));

         call.enqueue(new Callback<List<InstructorSections>>() {
             @Override
             public void onResponse(Call<List<InstructorSections>> call, Response<List<InstructorSections>> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     List<InstructorSections> sections = response.body();
                     for (InstructorSections section : sections) {
                         TableRow(section);
                     }
                 } else {
                     Toast.makeText(InstructorTaughtCourses.this, "Server error", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<List<InstructorSections>> call, Throwable t) {
                 Toast.makeText(InstructorTaughtCourses.this, "Failure for InstructorTaughtCourses", Toast.LENGTH_SHORT).show();
             }
         });
     }

     private void TableRow(InstructorSections section) {
         TableRow row = new TableRow(this);
         row.setLayoutParams(new TableRow.LayoutParams(
                 TableRow.LayoutParams.MATCH_PARENT,
                 TableRow.LayoutParams.WRAP_CONTENT
         ));

         row.addView(block(section.getCourse_id()));
         row.addView(block(section.getSection_id()));
         row.addView(block(section.getSemester()));
         row.addView(block(section.getYear()));

         tableLayout.addView(row);
     }

     private TextView block(String text) {
         TextView block = new TextView(this);
         block.setText(text);
         return block;
     }
 }