 package com.example.phase3;

 import android.os.Bundle;
 import android.util.Log;
 import android.view.Gravity;
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

 public class InstructorSectionActivity extends AppCompatActivity {

     private TableLayout tableLayout;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.taught_sections);
         tableLayout = findViewById(R.id.tableLayout);

         ApiService apiService = RetrofitClient.getClient("http://192.168.200.17/Phase3/").create(ApiService.class);


         String email = getIntent().getStringExtra("email");
         Toast.makeText(this, "Received email: " + email, Toast.LENGTH_SHORT).show();


         Call<List<InstructorSections>> call = apiService.getInstructorSections(getIntent().getStringExtra("email"));

         call.enqueue(new Callback<List<InstructorSections>>() {
             @Override
             public void onResponse(Call<List<InstructorSections>> call, Response<List<InstructorSections>> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     List<InstructorSections> sections = response.body();
                     for (InstructorSections section : sections) {
                         addTableRow(section);
                     }
                 } else {
                     Log.e("API_ERROR", "Server returned error: " + response.code());
                 }
             }

             @Override
             public void onFailure(Call<List<InstructorSections>> call, Throwable t) {
                 Log.e("API_FAIL", "Request failed: " + t.getMessage());
             }
         });
     }

     private void addTableRow(InstructorSections section) {
         TableRow row = new TableRow(this);
         row.setLayoutParams(new TableRow.LayoutParams(
                 TableRow.LayoutParams.MATCH_PARENT,
                 TableRow.LayoutParams.WRAP_CONTENT
         ));

         row.addView(createCell(section.getCourse_id()));
         row.addView(createCell(section.getSection_id()));
         row.addView(createCell(section.getSemester()));
         row.addView(createCell(section.getYear()));

         tableLayout.addView(row);
     }

     private TextView createCell(String text) {
         TextView cell = new TextView(this);
         cell.setText(text);
         cell.setPadding(8, 8, 8, 8);
         cell.setGravity(Gravity.CENTER);
         return cell;
     }
 }