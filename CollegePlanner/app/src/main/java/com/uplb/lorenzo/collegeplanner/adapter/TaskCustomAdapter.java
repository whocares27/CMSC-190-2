package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.TaskEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class TaskCustomAdapter extends ArrayAdapter<TaskEntity> {

    private final Context context;
    private final ArrayList<TaskEntity> TaskArrayList;

    public TaskCustomAdapter(Context context, ArrayList<TaskEntity> itemsArrayList) {

        super(context, R.layout.task_custom_row, itemsArrayList);

        this.context = context;
        this.TaskArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.task_custom_row, parent, false);

        TextView title = (TextView) customView.findViewById(R.id.row_title);
        TextView date = (TextView) customView.findViewById(R.id.row_date);
        TextView course = (TextView) customView.findViewById(R.id.row_course);
        TextView weight = (TextView) customView.findViewById(R.id.row_weight);

        String t = TaskArrayList.get(position).title;
        String d = TaskArrayList.get(position).due_date;
        String c = TaskArrayList.get(position).course_name;
        String w = TaskArrayList.get(position).weight;

        title.setText(t);
        date.setText(d);
        course.setText(c);
        weight.setText(w);

        return customView;
    }

}
