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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SleepCirclesActivity extends AppCompatActivity {

    ListView listViewSleepCircle;

    SleepCircleList sleepCircleList; // The adapter

    DatabaseReference database;

    ArrayList<SleepCircle> list;

    String TAG = "SleepCirclesActivity";

    public static final String CIRCLE_NAME = "circle name";
    public static final String CIRCLE_ID = "circle id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circles);

        TextView addCircle = (TextView) findViewById(R.id.create_circle);

        listViewSleepCircle = (ListView)findViewById(R.id.listViewSleepCircles);

        list = new ArrayList<>();

        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCircleIntent = new Intent(SleepCirclesActivity.this, AddSleepCircleActivity.class);
                startActivity(addCircleIntent);
            }
        });

        /*


        String currentUserUid = CurrentUser.uid;
        Log.d(TAG, "Current user uid is: " + currentUserUid);

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

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(currentUserUid).child("circles");
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

        */

        listViewSleepCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SleepCircle circle = sleepCircleList.getItem(i);
                Intent intent = new Intent(getApplicationContext(), SleepCircleDetailsActivity.class);
                intent.putExtra(CIRCLE_NAME, circle.getCircleName());
                intent.putExtra(CIRCLE_ID, circle.getCircleId());

                startActivity(intent);
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart is triggered");
        //list.clear();

        /*

        setContentView(R.layout.activity_sleep_circles);

         String currentUserUid = CurrentUser.uid;
        Log.d(TAG, "Current user uid is: " + currentUserUid);

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

*/
        String currentUserUid = CurrentUser.uid;

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(currentUserUid).child("circles");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot circleSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    SleepCircle circle = circleSnapshot.getValue(SleepCircle.class);
                    //adding artist to the list
                    Log.d(TAG, String.valueOf(circle));
                    list.add(circle);
                }

                //creating adapter
                sleepCircleList = new SleepCircleList(SleepCirclesActivity.this, list);

                //attaching adapter to the listview
                listViewSleepCircle.setAdapter(sleepCircleList);
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
                intent.putExtra(CIRCLE_NAME, circle.getCircleName());
                intent.putExtra(CIRCLE_ID, circle.getCircleId());

                startActivity(intent);
            }
        });



    }


}
