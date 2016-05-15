package com.uplb.lorenzo.collegeplanner.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.InstructorEntity;

import org.w3c.dom.Text;

public class InstructorCustomAdapter extends ArrayAdapter<InstructorEntity> {

    private final Context context;
    private final ArrayList<InstructorEntity> InstructorArrayList;

    public InstructorCustomAdapter(Context context, ArrayList<InstructorEntity> itemsArrayList) {

        super(context, R.layout.instructor_custom_row, itemsArrayList);

        this.context = context;
        this.InstructorArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.instructor_custom_row, parent, false);

        TextView name = (TextView) customView.findViewById(R.id.row_name);
        TextView college = (TextView) customView.findViewById(R.id.row_college);

        String fullname = InstructorArrayList.get(position).getFullname();
        String col = InstructorArrayList.get(position).college;

        name.setText(fullname);
        college.setText(col);


        return customView;
    }

}
