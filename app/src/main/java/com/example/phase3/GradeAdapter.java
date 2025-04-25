package com.example.phase3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
public class GradeAdapter extends BaseAdapter {
    private Context context;
    private List<MidtermGrade> grades;
    private LayoutInflater inflater;

    public GradeAdapter(Context context, List<MidtermGrade> grades) {
        this.context = context;
        this.grades = grades;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return grades.size();
    }

    @Override
    public Object getItem(int position) {
        return grades.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_grade, parent, false);
            holder = new ViewHolder();
            holder.courseTextView = convertView.findViewById(R.id.courseTextView);
            holder.sectionTextView = convertView.findViewById(R.id.sectionTextView);
            holder.semesterTextView = convertView.findViewById(R.id.semesterTextView);
            holder.gradeTextView = convertView.findViewById(R.id.gradeTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MidtermGrade grade = grades.get(position);

        // the placeholders are replaced with actual data
        holder.courseTextView.setText(grade.getCourseId() + " - " + grade.getCourseName());
        holder.sectionTextView.setText("Section: " + grade.getSectionId());
        holder.semesterTextView.setText(grade.getSemester() + " " + grade.getYear());
        holder.gradeTextView.setText(grade.getMidtermGrade());

        // low grades
        String gradeValue = grade.getMidtermGrade();
        if (gradeValue.equals("C-") || gradeValue.equals("D+") || gradeValue.equals("D") ||
                gradeValue.equals("D-") || gradeValue.equals("F")) {
            holder.gradeTextView.setTextColor(Color.RED);
        } else {
            holder.gradeTextView.setTextColor(Color.BLACK);
        }
        return convertView;

    }

    private static class ViewHolder {
        TextView courseTextView;
        TextView sectionTextView;
        TextView semesterTextView;
        TextView gradeTextView;
    }
}
