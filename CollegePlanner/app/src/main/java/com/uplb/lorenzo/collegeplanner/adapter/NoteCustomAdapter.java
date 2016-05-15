package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.NoteEntity;

import java.util.ArrayList;

/**
 * Created by lorenzo on 4/3/2016.
 */
public class NoteCustomAdapter extends ArrayAdapter<NoteEntity> {

    private final Context context;
    private final ArrayList<NoteEntity> NoteArrayList;

    public NoteCustomAdapter(Context context, ArrayList<NoteEntity> itemsArrayList) {

        super(context, R.layout.note_custom_row, itemsArrayList);

        this.context = context;
        this.NoteArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.note_custom_row, parent, false);

        TextView title = (TextView) customView.findViewById(R.id.row_title);
        TextView date = (TextView) customView.findViewById(R.id.row_date);
        TextView course = (TextView) customView.findViewById(R.id.row_course);
        TextView body = (TextView) customView.findViewById(R.id.row_body);

        String t = NoteArrayList.get(position).title;
        String d = NoteArrayList.get(position).date;
        String c = NoteArrayList.get(position).course_name;
        String b = NoteArrayList.get(position).body.split("\n")[0];

        title.setText(t);
        date.setText(d);
        course.setText(c);
        body.setText(b);

        return customView;
    }

}
