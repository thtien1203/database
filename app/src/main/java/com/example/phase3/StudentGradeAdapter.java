package com.example.phase3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
public class StudentGradeAdapter extends BaseAdapter {
    private Context context;
    private List<Student> students;
    private Map<String, String> studentGrades;
    private LayoutInflater inflater;
    private List<String> gradeOptions = Arrays.asList(
            "Select Grade", "A+", "A", "A-", "B+", "B", "B-",
            "C+", "C", "C-", "D+", "D", "D-", "F"
    );

    public StudentGradeAdapter(Context context, List<Student> students, Map<String, String> studentGrades) {
        this.context = context;
        this.students = students;
        this.studentGrades = studentGrades;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_student_grade, parent, false);

            holder = new ViewHolder();
            holder.studentIdTextView = convertView.findViewById(R.id.studentIdTextView);
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.currentGradeTextView = convertView.findViewById(R.id.currentGradeTextView);
            holder.gradeSpinner = convertView.findViewById(R.id.gradeSpinner);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = students.get(position);
        holder.studentIdTextView.setText(student.getStudentId());
        holder.nameTextView.setText(student.getName());

        // set current grade
        String currentGrade = student.getCurrentGrade() != null ?
                student.getCurrentGrade() : "Not graded";
        holder.currentGradeTextView.setText(currentGrade);

        // set up grade spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, gradeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.gradeSpinner.setAdapter(adapter);

        // set selection based on current value
        String selectedGrade = studentGrades.get(student.getStudentId());
        if (selectedGrade != null && !selectedGrade.isEmpty()) {
            int gradePosition = gradeOptions.indexOf(selectedGrade);
            if (gradePosition > 0) {
                holder.gradeSpinner.setSelection(gradePosition);
            }
        }
        final String studentId = student.getStudentId();
        holder.gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // skip "Select Grade"
                    studentGrades.put(studentId, gradeOptions.get(position));
                } else {
                    studentGrades.remove(studentId);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        return convertView;
    }
    private static class ViewHolder {
        TextView studentIdTextView;
        TextView nameTextView;
        TextView currentGradeTextView;
        Spinner gradeSpinner;
    }
}
