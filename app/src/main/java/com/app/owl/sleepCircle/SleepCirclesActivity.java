package com.app.owl.sleepCircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.owl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SleepCirclesActivity extends AppCompatActivity {

    ListView listViewSleepCircle;

    List<SleepCircle> sleepCircleList;

    DatabaseReference database;

    String id;

    String TAG = "SleepCirclesActivity";

    public static final String CIRCLENAME = "circle name";
    public static final String CIRCLEID = "circle id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circles);

        TextView addCircle = (TextView) findViewById(R.id.create_circle);
        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCircleIntent = new Intent(SleepCirclesActivity.this, AddSleepCircleActivity.class);
                startActivity(addCircleIntent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();
            database = FirebaseDatabase.getInstance().getReference();

            Query query = database.child("SleepCircles").orderByChild("circleId").equalTo(uid);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot sleepCircle : dataSnapshot.getChildren()) {
                            SleepCircle circle = sleepCircle.getValue(SleepCircle.class);
                            if(circle != null){id = circle.circleId;}
                            if(id != null) {

                                //database = FirebaseDatabase.getInstance().getReference("SleepCircles").child(circleId);

                                listViewSleepCircle = (ListView) findViewById(R.id.listViewSleepCircles);

                                sleepCircleList = new ArrayList();

                                Button newCircle = (Button) findViewById(R.id.create_circle);

                                newCircle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(SleepCirclesActivity.this, AddSleepCircleActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                listViewSleepCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        SleepCircle circle = sleepCircleList.get(i);
                                        Intent intent = new Intent(getApplicationContext(), SleepCircleDetailsActivity.class);
                                        intent.putExtra(CIRCLENAME, circle.getCircleName());
                                        intent.putExtra(CIRCLEID, circle.getCircleId());

                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }else{
                        Log.d(TAG, "DataSnapshot doesn't exists ");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();
            database = FirebaseDatabase.getInstance().getReference();
            Query query = database.child("SleepCircles").orderByChild("circleId").equalTo(uid);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot sleepCircle : dataSnapshot.getChildren()) {
                            SleepCircle circle  = sleepCircle.getValue(SleepCircle.class);
                            if(circle != null){id = circle.circleId;}
                            if(id != null) {
                                database = FirebaseDatabase.getInstance().getReference("SleepCircles").child(id);

                                database.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        sleepCircleList.clear();

                                        for (DataSnapshot sleepCircleSnapshot: dataSnapshot.getChildren()){
                                            SleepCircle circle = sleepCircleSnapshot.getValue(SleepCircle.class);
                                            sleepCircleList.add(circle);
                                        }

                                        SleepCircleList adapter = new SleepCircleList(SleepCirclesActivity.this, sleepCircleList);

                                        listViewSleepCircle.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }else{
                        Log.d("In onDataChange!!!!!!!", "DataSnapshot doesn't exists :(((((");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




    }
}
