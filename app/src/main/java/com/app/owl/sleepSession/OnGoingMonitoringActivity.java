package com.app.owl.sleepSession;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.owl.MainUser;
import com.app.owl.OnGetDataListener;
import com.app.owl.R;
import com.app.owl.UserMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OnGoingMonitoringActivity extends AppCompatActivity {

    FirebaseUser user;
    String userUid;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_monitoring);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();

        // Check is onGoingSession changed to false. If so, redirect to welcome page.
        DatabaseReference onGoingSessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
        onGoingSessionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                if(!localUser.getOnGoingSession()){
                    Intent returnIntent = new Intent(OnGoingMonitoringActivity.this, UserMainActivity.class);
                    startActivity(returnIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Find the right Session
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timeStamps").limitToLast(1);

        findSession(query, new OnGetDataListener(){
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                String startTime = localSleepSession.getStartTime();

                // Listen on the sleep session's changes
                DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(startTime);

                sessionDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // A new alert is created
                        // TODO: check if s would be "Alerts" for new alerts. If so, add: if(s == "Alerts"){ rest of the code }

                        // Check currentResponder
                        SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                        // If you are the current responder, call handleAlert()
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                })




            }
            @Override
            public void onStart() {}
            @Override
            public void onFailure() {}
        });



    }

    public void findSession(Query query, final OnGetDataListener listener) {
        listener.onStart();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    listener.onSuccess(sessionSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
