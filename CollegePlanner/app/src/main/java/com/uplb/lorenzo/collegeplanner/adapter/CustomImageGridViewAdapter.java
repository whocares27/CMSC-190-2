package com.uplb.lorenzo.collegeplanner.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uplb.lorenzo.collegeplanner.DatabaseHelper;
import com.uplb.lorenzo.collegeplanner.R;
import com.uplb.lorenzo.collegeplanner.entity.PhotoEntity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by lorenzo on 4/17/2016.
 */
public class CustomImageGridViewAdapter extends ArrayAdapter<PhotoEntity> {

    private final Context context;
    private final ArrayList<PhotoEntity> PhotoArrayList;

    public CustomImageGridViewAdapter(Context context, ArrayList<PhotoEntity> itemsArrayList) {

        super(context, R.layout.custom_photo_grid_single, itemsArrayList);

        this.context = context;
        this.PhotoArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_photo_grid_single, parent, false);

        TextView title = (TextView) customView.findViewById(R.id.title);
        TextView date = (TextView) customView.findViewById(R.id.date);
        TextView course = (TextView) customView.findViewById(R.id.course);
        ImageView image = (ImageView) customView.findViewById(R.id.grid_image);
        String t = null;
        if(PhotoArrayList.get(position).title.length() > 0) {
            t = "Title: " + PhotoArrayList.get(position).title;
        } else t = "(Untitled)";
        String d = PhotoArrayList.get(position).date;
        String c = PhotoArrayList.get(position).course_name;
        String path = PhotoArrayList.get(position).path;

        title.setText(t);
        date.setText(d);
        course.setText(c);
        if(!(new File(path)).exists()){
            image.setImageResource(R.drawable.no_image);

        }else {
            ImageLoaderTask imageTask = new ImageLoaderTask(image);
            imageTask.execute(path);
        }
        return customView;
    }




}

class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public ImageLoaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(params[0]), 256, 192);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
