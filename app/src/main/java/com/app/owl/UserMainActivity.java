package com.app.owl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.owl.sleepCircle.SleepCirclesActivity;
import com.app.owl.sleepSession.ConnectAudioDeviceActivity;
import com.app.owl.sleepSession.NewSleepSessionActivity;
import com.app.owl.sleepSession.SleepSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserMainActivity extends AppCompatActivity {

    Button testWireless, sleepCircle, signout;
    DatabaseReference database;
    LinearLayout pageLayout;
    FirebaseUser user;
    String userUid;
    View notification;
    LayoutInflater inflater;

    private static final String TAG = "UserMainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        notification = null;

        pageLayout = (LinearLayout) findViewById(R.id.activity_user_main);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        user = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: put on every page
        // TODO: try to put in it's on method
        if(user == null){
            //redirect to login screen

            Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        userUid = user.getUid();


        // TODO add a timer countdown to join sleep session
        // When timer runs out, call ignore alert


        // Is the user awaited in a session?
        // Display alert if user awaited

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
        Log.d(TAG, "database: " + database);

        ChildEventListener childEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Change in MainUsers - onChildAdded");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                Log.d(TAG, "Change in MainUsers - onChildChanged");
                Log.d(TAG, "dataSnapshot " + dataSnapshot);
                Log.d(TAG, "s" + previousChildName);

                Log.d(TAG, "dataSnapshot.getKey(): " + dataSnapshot.getKey());

                if(dataSnapshot.getKey().equals("onGoingSession")){
                    Log.d(TAG, "Change to ongoingSession status");

                    Boolean ongoingSession = dataSnapshot.getValue(Boolean.class);

                    if(ongoingSession){
                        Log.d(TAG, "Session started");


                        // Check if the user has an OnGoingSession and check if the user was notified
                        DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("isNotified");
                        Log.d(TAG, "database: " + database);

                        localDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "Looking at the user object: " + dataSnapshot);

                                if(!dataSnapshot.getValue(Boolean.class)){
                                    Log.d(TAG, "The user has not been notified of the ongoing session yet");

                                    // show notification
                                    notification = inflater.inflate(R.layout.new_session_notification, null);

                                    inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    pageLayout.addView(notification, 0);

                                    // Updates isNotified field
                                    Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timestamp").limitToLast(1);

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.d(TAG, "dataSnapshot" + dataSnapshot);

                                            for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {

                                                Log.d(TAG, "sessionSnapshot" + sessionSnapshot);
                                                final SleepSession localSleepSession = sessionSnapshot.getValue(SleepSession.class);
                                                String sessionStartTime = localSleepSession.getStartTime();

                                                updateIsNotified(sessionStartTime);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }else{Log.d(TAG, "Session ended");}



                }



            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Change in MainUsers - onCancelled");
            }
        };
        database.addChildEventListener(childEventListener2);



        // Listen for notificationIgnored event

        //Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").limitToLast(1);



        DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions");

        ChildEventListener childEventListener3 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Change in MainUsers (listening for notificationIgnored event) - onChildAdded");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                Log.d(TAG, "Change in MainUsers (listening for notificationIgnored event) -  onChildChanged");
                Log.d(TAG, "dataSnapshot" + dataSnapshot);
                Log.d(TAG, "s" + previousChildName);

                Log.d(TAG, "dataSnapshot.getKey(): " + dataSnapshot.getKey());


                    SleepSession localSession = dataSnapshot.getValue(SleepSession.class); // We are assuming that only the most recent sleep session from this user can change. TODO: Find a way to only grab the last sleep session

                    if(localSession.getNotificationIgnored() && !localSession.getNotificationIgnoredBy().equals(userUid)){
                        Log.d(TAG, "The notification was just ignored");
                        Log.d(TAG, "New SleepSession was ignored by the other user");

                        if((notification instanceof View) || notification != null){

                            pageLayout.removeView((View) notification.getParent());
                            notification = null;
                            Log.d(TAG, "Removed alert notification");

                            // Check if a notification was removed because of someone hitting the ignore button. if so show Toast
                            Toast.makeText(UserMainActivity.this, "Sleep Session was cancelled by " + localSession.getNotificationIgnoredBy(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }



            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Change in MainUsers - onCancelled");
            }
        };
        localDatabase.addChildEventListener(childEventListener3);



        /*


        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                if(localSleepSession.getNotificationIgnored()){

                    database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
                    findUserName(database, new OnGetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            MainUser localUser = dataSnapshot.getValue(MainUser.class);
                            String localUserName = localUser.getUserName();
                            if(localSleepSession.getNotificationIgnored() && !localSleepSession.getNotificationIgnoredBy().equals(localUserName)){

                                Log.d(TAG, "New SleepSession was ignored by the other user");

                                if((notification instanceof View) || notification != null){

                                    pageLayout.removeView((View) notification.getParent());
                                    notification = null;
                                    Log.d(TAG, "Removed alert notification");

                                    // Check if a notification was removed because of someone hitting the ignore button. if so show Toast
                                    Toast.makeText(UserMainActivity.this, "Sleep Session was cancelled by " + localSleepSession.getNotificationIgnoredBy(), Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                        @Override
                        public void onStart() {}

                        @Override
                        public void onFailure() {}
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

        */



        signout = findViewById(R.id.sign_out_btn);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Click on Sign out button");

                FirebaseAuth.getInstance().signOut();

                database = FirebaseDatabase.getInstance().getReference().child("MainUsers");

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(CurrentUser.uid + "/isSignedIn/", false);
                database.updateChildren(childUpdates);

                Toast.makeText(UserMainActivity.this, "You successfully logged-out.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
                startActivity(intent);




            }
        });

        testWireless = findViewById(R.id.new_sleep_btn);
        testWireless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, NewSleepSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sleepCircle = findViewById(R.id.sleep_circle_btn);
        sleepCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, SleepCirclesActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    public void onJoin(View v) {
        // Remove notification
        Log.d(TAG, "Someone clicked onJoin");
        if(notification != null){
            pageLayout.removeView((View) notification.getParent());
            notification = null;
            Log.d(TAG, "Removed alert notification");
        }

        // set MainUser insideSession = true
        DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference();
        String path = "/MainUsers/" + userUid + "/insideSession/";
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, true);
        localDatabase.updateChildren(childUpdates);

        // Open Bluetooth connection page
        Intent intentConnectDevice = new Intent(UserMainActivity.this, ConnectAudioDeviceActivity.class);
        startActivity(intentConnectDevice);
        finish();


    }

    public void onDelete(View v) {
        Log.d(TAG, "Someone clicked ignore");

        // Remove the notification
        pageLayout.removeView((View) v.getParent());
        notification = null;
        Log.d(TAG, "Removed alert notification");

        // find the sleep session
        // set notificationIgnored = true & notificationIgnoredBy current user
        // set end time on sleep session
        // Set onGoingSleep session = false on both users
        final String timeNow = String.valueOf(Calendar.getInstance().getTime());
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timestamp").limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot" + dataSnapshot);

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "sessionSnapshot " + sessionSnapshot);
                    final SleepSession localSleepSession = sessionSnapshot.getValue(SleepSession.class);

                    findUserName(database, new OnGetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            MainUser localUser = dataSnapshot.getValue(MainUser.class);
                            String localUserName = localUser.getUserName();

                            Log.d(TAG, "I know my name: " + localUserName);

                            String user1 = localSleepSession.getFirstResponder();
                            String user2 = localSleepSession.getSecondResponder();
                            Log.d(TAG, "user1: " + user1 + " user2: " + user2);
                            String sessionStartTime = localSleepSession.getStartTime();
                            Log.d(TAG, "sessionStartTime: " + sessionStartTime);
                            DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference();
                            Log.d(TAG, "localDatabase: " + localDatabase);
                            Log.d(TAG, "database path to string =  " + String.valueOf(localDatabase));
                            String path1 = "/MainUsers/"+ user1 + "/SleepSessions/" + sessionStartTime + "/notificationIgnored/";
                            String path2 = "/MainUsers/"+ user2 + "/SleepSessions/" + sessionStartTime + "/notificationIgnored/";
                            String path7 = "/MainUsers/"+ user1 + "/SleepSessions/" + sessionStartTime + "/notificationIgnoredBy/";
                            String path8 = "/MainUsers/"+ user2 + "/SleepSessions/" + sessionStartTime + "/notificationIgnoredBy/";
                            String path3 = "/MainUsers/"+ user1 + "/SleepSessions/" + sessionStartTime + "/endTime/";
                            String path4 = "/MainUsers/"+ user2 + "/SleepSessions/" + sessionStartTime + "/endTime/";
                            String path5 = "/MainUsers/"+ user1 + "/onGoingSession/" ;
                            String path6 = "/MainUsers/"+ user2 + "/onGoingSession/";
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(path1, true);
                            childUpdates.put(path2, true);
                            childUpdates.put(path3, timeNow);
                            childUpdates.put(path4, timeNow);
                            childUpdates.put(path5, false);
                            childUpdates.put(path6, false);
                            childUpdates.put(path7, localUserName);
                            childUpdates.put(path8, localUserName);
                            localDatabase.updateChildren(childUpdates);
                            Log.d(TAG, "I'm special, I only happen once");

                        }
                        @Override
                        public void onStart() {}

                        @Override
                        public void onFailure() {}
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated");
        }
    }


    public void findUserName(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public void updateIsNotified(String sessionStartTime){
        database = FirebaseDatabase.getInstance().getReference();
        String path = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/isNotified/";
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, true);
        database.updateChildren(childUpdates);

    }
}
