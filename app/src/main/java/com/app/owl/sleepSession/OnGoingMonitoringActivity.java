package com.app.owl.sleepSession;

import android.support.v7.app.AppCompatActivity;

public class OnGoingMonitoringActivity extends AppCompatActivity {
    /*

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
        DatabaseReference onGoingSessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid); // TODO: check spelling inside path
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
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timeStamps").limitToLast(1); // TODO: check spelling inside path

        findSession(query, new OnGetDataListener(){
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                final String startTime = localSleepSession.getStartTime();

                // Listen on the sleep session's changes
                DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(startTime); // TODO: check spelling inside path

                sessionDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // A new alert is created
                        // TODO: check if "s" would be "Alerts" for new alerts. If so, add: if(s == "Alerts"){ rest of the code }

                        // Check if you are the currentResponder
                        SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);

                        // If you are the current responder: find the alert, then call handleAlert(alert)
                        if(localSleepSession.getCurrentResponder() == userUid){
                            // Get the last Alert
                            Query alertsDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(startTime).child("alerts").orderByChild("timestamp").limitToLast(1); // TODO: check spelling inside path

                            alertsDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot alertSnapshot : dataSnapshot.getChildren()){
                                        Alert alert = alertSnapshot.getValue(Alert.class);
                                        if(alert.getEndTime() == null){ // TODO: check unused alertEnded field of alert
                                            handleAlert(alert);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        // Check if you are the currentResponder
                        SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);

                        // If you are the current responder: find the alert, then call handleAlert(alert)
                        if(localSleepSession.getCurrentResponder() == userUid){
                            // Get the last Alert
                            Query alertsDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(startTime).child("alerts").orderByChild("timestamp").limitToLast(1); // TODO: check spelling inside path

                            alertsDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot alertSnapshot : dataSnapshot.getChildren()){
                                        Alert alert = alertSnapshot.getValue(Alert.class);
                                        if(alert.getEndTime() == null){ // TODO: check unused alertEnded field of alert
                                            handleAlert(alert);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

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
                });




            }
            @Override
            public void onStart() {}
            @Override
            public void onFailure() {}
        });



    }

    public void handleAlert(Alert alert){

        snoozeTimer = new Timer();
        snoozeTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Inside snoozeDecide - 30s timer is up. Starting sound detection soon");
                new HideSnoozeDuration().execute();
                new HideSnoozeBtn().execute();
                new HideSnoozeClock().execute();
                clockTimer.cancel();
                soundCapture.start();
                startDetectingSounds();
            }
        };
        // Start Timer
        // Start Alarm
        // resolve at the end of timer
        // Create "alert received" button and on click listener
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

    */
}
