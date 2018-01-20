package com.app.owl.sleepCircle;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.owl.R;

import java.util.List;

/**
 * Created by Christiane on 1/18/18.
 */



public class SleepCircleList extends ArrayAdapter<SleepCircle> {

    private Activity context;
    private List<SleepCircle> sleepCircleList;

    public SleepCircleList(Activity context, List<SleepCircle> sleepCircleList) {
        super(context, R.layout.activity_sleep_circles, sleepCircleList);
        this.context = context;
        this.sleepCircleList = sleepCircleList;
    }

    public static final String TAG = "sleepCircleList";

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.sleep_circles_list, null, true);

        TextView textViewCircleName = (TextView) listViewItem.findViewById(R.id.text_view_circle_name);

        SleepCircle circle = sleepCircleList.get(position);
        Log.d(TAG, String.valueOf(circle));
        String name = circle.getSleepCircleName();
        Log.d(TAG, name);
        textViewCircleName.setText(name);

        Log.d(TAG, "I'm in the list");
        return listViewItem;

    }
}
