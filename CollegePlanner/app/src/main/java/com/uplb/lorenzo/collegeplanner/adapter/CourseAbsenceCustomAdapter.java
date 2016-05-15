package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class CourseAbsenceCustomAdapter extends ArrayAdapter<CourseEntity> {

    private final Context context;
    private final ArrayList<CourseEntity> CourseArrayList;

    public CourseAbsenceCustomAdapter(Context context, ArrayList<CourseEntity> itemsArrayList) {

        super(context, R.layout.course_custom_row, itemsArrayList);

        this.context = context;
        this.CourseArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.course_absent_custom_row, parent, false);

        TextView course = (TextView) customView.findViewById(R.id.row_course);
        TextView absence = (TextView) customView.findViewById(R.id.row_absence);
        TextView type = (TextView) customView.findViewById(R.id.row_type);

        RelativeLayout rl = (RelativeLayout) customView.findViewById(R.id.bg);

        int current_absences = CourseArrayList.get(position).current_absences;
        int allowed_absences = CourseArrayList.get(position).absences;

        int ave_absences;

        if(allowed_absences%2==0) {
            ave_absences = allowed_absences/2;
        } else {
            ave_absences = (allowed_absences/2) + 1;
        }

        if(current_absences >= allowed_absences) rl.setBackgroundColor(Color.parseColor("#ff4b37"));
        else if(current_absences < ave_absences) rl.setBackgroundColor(Color.parseColor("#2ecc71"));
        else if(current_absences >= ave_absences) rl.setBackgroundColor(Color.parseColor("#ffa837"));


        String c = CourseArrayList.get(position).code;
        String a = current_absences + "/" + allowed_absences;
        String t = "(" +CourseArrayList.get(position).type+ ")";

        course.setText(c);
        absence.setText(a);
        type.setText(t);

        return customView;
    }
}
