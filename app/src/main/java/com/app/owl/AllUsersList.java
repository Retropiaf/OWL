package com.app.owl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Christiane on 1/6/18.
 */

public class AllUsersList extends ArrayAdapter<SecondaryUser>{

    private Activity context;
    private List<SecondaryUser> secondaryUserList;

    public AllUsersList(Activity context, List<SecondaryUser> secondaryUserList) {
        super(context, R.layout.activity_users, secondaryUserList);
        this.context = context;
        this.secondaryUserList = secondaryUserList;
    }

    public static final String TAG = "AllUsersList";

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        //return super.getView(position, convertView, parent);
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.user_name);


        SecondaryUser user = secondaryUserList.get(position);
        textViewName.setText(user.getSecondaryUserName());

        Log.d(TAG, "I'm in the list");
        return listViewItem;

    }
}
