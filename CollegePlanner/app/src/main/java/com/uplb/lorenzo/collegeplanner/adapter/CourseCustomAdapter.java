package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.CourseEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class CourseCustomAdapter extends ArrayAdapter<CourseEntity> {

    private final Context context;
    private final ArrayList<CourseEntity> CourseArrayList;

    public CourseCustomAdapter(Context context, ArrayList<CourseEntity> itemsArrayList) {

        super(context, R.layout.course_custom_row, itemsArrayList);

        this.context = context;
        this.CourseArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.course_custom_row, parent, false);

        TextView code = (TextView) customView.findViewById(R.id.row_code);
        TextView section = (TextView) customView.findViewById(R.id.row_section);
        TextView type = (TextView) customView.findViewById(R.id.row_type);

        String c = CourseArrayList.get(position).code;
        String s = CourseArrayList.get(position).section;
        String t = "("+CourseArrayList.get(position).type+")";

        code.setText(c);
        section.setText(s);
        type.setText(t);

        return customView;
    }
}
