package com.example.phase3;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class AlertAdapter extends BaseAdapter {
    private Context context;
    private List<StudentAlert> alerts;
    private List<Integer> selectedAlertIds;
    private LayoutInflater inflater;

    public AlertAdapter(Context context, List<StudentAlert>alerts, List<Integer>selectedAlertIds) {
        this.context = context;
        this.alerts = alerts;
        this.selectedAlertIds = selectedAlertIds;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alerts.size();
    }
    @Override
    public Object getItem(int position) {
        return alerts.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_alert, parent, false);

            holder = new ViewHolder();
            holder.alertTypeTextView = convertView.findViewById(R.id.alertTypeTextView);
            holder.courseTextView = convertView.findViewById(R.id.courseTextView);
            holder.alertMessageTextView = convertView.findViewById(R.id.alertMessageTextView);
            holder.alertDateTextView = convertView.findViewById(R.id.alertDateTextView);
            holder.markReadCheckBox = convertView.findViewById(R.id.markReadCheckBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StudentAlert alert = alerts.get(position);

        holder.alertTypeTextView.setText(alert.getAlertType());
        holder.courseTextView.setText(alert.getCourseId() + " - " + alert.getCourseName() +
                " (" + alert.getSectionId() + ")");
        holder.alertMessageTextView.setText(alert.getAlertMessage());
        holder.alertDateTextView.setText(alert.getAlertDate());

        // Set checkbox visibility and state
        if (alert.isRead()) {
            holder.markReadCheckBox.setVisibility(View.GONE);

            // Style for read alerts
            holder.alertTypeTextView.setTypeface(null, Typeface.NORMAL);
            holder.courseTextView.setTypeface(null, Typeface.NORMAL);
            holder.alertMessageTextView.setTypeface(null, Typeface.NORMAL);
            holder.alertDateTextView.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.markReadCheckBox.setVisibility(View.VISIBLE);
            holder.markReadCheckBox.setChecked(selectedAlertIds.contains(alert.getAlertId()));

            // Style for unread alerts
            holder.alertTypeTextView.setTypeface(null, Typeface.BOLD);
            holder.courseTextView.setTypeface(null, Typeface.BOLD);
            holder.alertMessageTextView.setTypeface(null, Typeface.BOLD);
            holder.alertDateTextView.setTypeface(null, Typeface.BOLD);

            // Set checkbox listener
            holder.markReadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!selectedAlertIds.contains(alert.getAlertId())) {
                            selectedAlertIds.add(alert.getAlertId());
                        }
                    } else {
                        selectedAlertIds.remove(Integer.valueOf(alert.getAlertId()));
                    }
                }
            });
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView alertTypeTextView;
        TextView courseTextView;
        TextView alertMessageTextView;
        TextView alertDateTextView;
        CheckBox markReadCheckBox;
    }
}
