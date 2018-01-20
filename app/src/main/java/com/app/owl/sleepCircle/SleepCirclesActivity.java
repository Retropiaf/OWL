package com.app.owl.sleepCircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.owl.CurrentUser;
import com.app.owl.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SleepCirclesActivity extends AppCompatActivity {

    ListView listViewSleepCircle;

    SleepCircleList sleepCircleList; // The adapter

    DatabaseReference database;

    ArrayList<SleepCircle> list = new ArrayList<>();

    String TAG = "SleepCirclesActivity";

    public static final String CIRCLE_NAME = "circle name";
    public static final String CIRCLE_ID = "circle id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circles);

        CurrentUser.findId();
        String currentUserId = CurrentUser.id;
        Log.d(TAG, "Current user id is: " + currentUserId);

        TextView addCircle = (TextView) findViewById(R.id.create_circle);
        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCircleIntent = new Intent(SleepCirclesActivity.this, AddSleepCircleActivity.class);
                startActivity(addCircleIntent);
            }
        });

        listViewSleepCircle = (ListView)findViewById(R.id.listViewSleepCircles);

        sleepCircleList = new SleepCircleList(this, list);

        listViewSleepCircle.setAdapter(sleepCircleList);

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(currentUserId).child("circles");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SleepCircle sleepCircle = dataSnapshot.getValue(SleepCircle.class);
                list.add(sleepCircle);
                sleepCircleList.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                SleepCircle sleepCircle = dataSnapshot.getValue(SleepCircle.class);
                list.remove(sleepCircle);
                sleepCircleList.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listViewSleepCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SleepCircle circle = sleepCircleList.getItem(i);
                Intent intent = new Intent(getApplicationContext(), SleepCircleDetailsActivity.class);
                intent.putExtra(CIRCLE_NAME, circle.getSleepCircleName());
                intent.putExtra(CIRCLE_ID, circle.getCircleId());

                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
/*
        CurrentUser.findId();
        String currentUserId = CurrentUser.id;

        TextView addCircle = (TextView) findViewById(R.id.create_circle);
        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCircleIntent = new Intent(SleepCirclesActivity.this, AddSleepCircleActivity.class);
                startActivity(addCircleIntent);
            }
        });

        listViewSleepCircle = (ListView)findViewById(R.id.listViewSleepCircles);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(SleepCirclesActivity.this, android.R.layout.simple_dropdown_item_1line, list);
        listViewSleepCircle.setAdapter(adapter);
        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(currentUserId).child("circles");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.add(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
    }


}
