package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.AbsenceEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class AbsenceCustomAdapter extends ArrayAdapter<AbsenceEntity> {

    private final Context context;
    private final ArrayList<AbsenceEntity> AbsenceArrayList;

    public AbsenceCustomAdapter(Context context, ArrayList<AbsenceEntity> itemsArrayList) {

        super(context, R.layout.course_custom_row, itemsArrayList);

        this.context = context;
        this.AbsenceArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.absence_custom_row, parent, false);

        TextView date = (TextView) customView.findViewById(R.id.row_date);

        date.setText(AbsenceArrayList.get(position).date);


        return customView;
    }
}
