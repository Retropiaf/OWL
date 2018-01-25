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
import com.app.owl.sleepSession.OnGoingSleepSessionActivity;
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

    private static final String TAG = "UserMainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        notification = null;

        pageLayout = (LinearLayout) findViewById(R.id.activity_user_main);

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

        //find userName





        /*

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                String otherUSerName = localUser.getUserName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        // TODO add a timer countdown to join sleep session
        // When timer runs out, call ignore alert


        // Is the user awaited in a session?
        // Listen for notificationIgnored event

        /*
        database = FirebaseDatabase.getInstance().getReference().child("MainUsers");

        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "Change registered inside current user");
                Log.d(TAG, "dataSnapshot: " + dataSnapshot + "String: " + s);
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                Log.d(TAG, "localUser: " + localUser);
                Boolean localOnGoingSession = localUser.getOnGoingSession();
                final SleepSession sleepSession = dataSnapshot.child("SleepSession").getValue(SleepSession.class);
                Log.d(TAG, "localOnGoingSession: " + localOnGoingSession);
                Log.d(TAG, "!localUser.getInsideSession: " + !localUser.getInsideSession());
                if(localOnGoingSession && !localUser.getInsideSession()){
                    Log.d(TAG, "Session is ongoing and user haven't joined yet");
                    // show notification
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    notification = inflater.inflate(R.layout.new_session_notification, null);
                    // Add the new row before the add field button.
                    pageLayout.addView(notification, 0);
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

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
        Log.d(TAG, "Checking if the user is awaited in a session");
        Log.d(TAG, "database: " + database);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                Log.d(TAG, "localUser: " + localUser);
                Boolean localOnGoingSession = localUser.getOnGoingSession();
                Log.d(TAG, "localUser.getIsSignedIn(): " + localUser.getIsSignedIn());
                Log.d(TAG, "localUser.getUserUid(): " + localUser.getUserUid());
                Log.d(TAG, "localUser.getUserName(): " + localUser.getUserName());
                Log.d(TAG, "localUser.getUid(): " + localUser.getUid());
                Log.d(TAG, "localUser.getUserEmail(): " + localUser.getUserEmail());
                Log.d(TAG, "localUser.getOnGoingSession(): " + String.valueOf(localUser.getOnGoingSession()));
                final SleepSession sleepSession = dataSnapshot.child("SleepSession").getValue(SleepSession.class);

                Log.d(TAG, "!localUser.getInsideSession: " + !localUser.getInsideSession());

                if(localOnGoingSession && !localUser.getInsideSession()){
                    Log.d(TAG, "Session is ongoing and user haven't joined yet");
                    // show notification
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    notification = inflater.inflate(R.layout.new_session_notification, null);
                    // Add the new row before the add field button.
                    pageLayout.addView(notification, 0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Listen for notificationIgnored event
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").limitToLast(1);;

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
                            if(!localSleepSession.getNotificationIgnoredBy().equals(localUserName)){

                                Log.d(TAG, "New SleepSession was ignored by the other user");

                                // TODO inflate "notification ignored by" alert
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

            // if onGoingSleepSession: add notification
            // else do nothing





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


                /*

                database = FirebaseDatabase
                        .getInstance().getReference().child("MainUsers");

                database.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            MainUser mainUser = snapshot.getValue(MainUser.class);
                            Log.d(TAG, "mainUser.getUid()" + mainUser.getUid());
                            Log.d(TAG, "CurrentUser.uid" + CurrentUser.uid);

                            if(mainUser.getUid().equals(CurrentUser.uid)){
                                Log.d(TAG, "Found main user");

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(CurrentUser.uid + "/isSignedIn/", false);
                                database.updateChildren(childUpdates);

                                FirebaseAuth.getInstance().signOut();

                                Toast.makeText(UserMainActivity.this, "You successfully logged-out.", Toast.LENGTH_SHORT).show();

                            }



                        } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: Handle database error
                    }
                });


*/

            }
        });

        testWireless = findViewById(R.id.new_sleep_btn);
        testWireless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, SetSleepSessionActivity.class);
                startActivity(intent);
            }
        });

        sleepCircle = findViewById(R.id.sleep_circle_btn);
        sleepCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, SleepCirclesActivity.class);
                startActivity(intent);
            }
        });

        //Button newCircle = (Button) findViewById(R.id.new_circle_btn);


        /*
        newCircle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {   Intent signUpIntent = new Intent(UserMainActivity.this, setNewCircleActivity.class);
                startActivity(signUpIntent);
            }

        });

        */

        //getUserDetails();
        //setUserDetails();
    }


    public void onJoin(View v) {
        pageLayout.removeView((View) v.getParent());

            database = FirebaseDatabase.getInstance().getReference();
            String path = "MainUsers" + userUid + "insideSession";
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(path, true);
            database.updateChildren(childUpdates);
            // set MainUser insideSession = true


        Intent intent = new Intent(UserMainActivity.this, OnGoingSleepSessionActivity.class);
        startActivity(intent);

        // TODO change redirection toward connect to bluetooth device
    }

    public void onDelete(View v) {
        pageLayout.removeView((View) v.getParent());
        notification = null;
        final String timeNow = String.valueOf(Calendar.getInstance().getTime());

        // find the sleep session
        // set notificationIgnored = true
        // set end time on sleep session
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "dataSnapshot" + dataSnapshot);
                for(DataSnapshot sessionsSnapshots: dataSnapshot.getChildren()){
                    Log.d(TAG, "sessionsSnapshots" + sessionsSnapshots);
                }

                Log.d(TAG, "WRONG (should go inside for/if dataSnapshot" + dataSnapshot);
                SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                String user1 = localSleepSession.getFirstResponder();
                String user2 = localSleepSession.getSecondResponder();
                String sessionStartTime = localSleepSession.getStartTime();
                database = FirebaseDatabase.getInstance().getReference();
                String path1 = "MainUsers"+ user1 + "SleepSessions" + sessionStartTime + "notificationIgnored";
                String path2 = "MainUsers"+ user2 + "SleepSessions" + sessionStartTime + "notificationIgnored";
                String path3 = "MainUsers"+ user1 + "SleepSessions" + sessionStartTime + "endTime";
                String path4 = "MainUsers"+ user2 + "SleepSessions" + sessionStartTime + "endTime";
                String path5 = "MainUsers"+ user1 + "onGoingSession" ;
                String path6 = "MainUsers"+ user2 + "onGoingSession";
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path1, true);
                childUpdates.put(path2, true);
                childUpdates.put(path3, timeNow);
                childUpdates.put(path4, timeNow);
                // Set onGoingSleep session = false on both users
                childUpdates.put(path5, false);
                childUpdates.put(path6, false);
                database.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
        findUserName(database, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                String localUserName = localUser.getUserName();
                // TODO set ignored by current user's userName
            }
            @Override
            public void onStart() {}

            @Override
            public void onFailure() {}
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


    /*
    private void setUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName("Christiane S").setPhotoUri(Uri.parse("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwixzemttL_YAhVT9GMKHaTyAyEQjRwIBw&url=http%3A%2F%2Fwww.hiphopvideoworld.com%2Fwebbie-this-me%2Fwebbie-this-me%2F&psig=AOvVaw0D7pNDpErwDtSdtu5SIbYq&ust=1515193106889070")).build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: User profile updated.");
                        getUserDetails();
                    }
                }
            });
        }
    }

    private void getUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String properties = "uid: " + uid + "\n" + "name: " + name + "\n" + "email: " + email + "\n" + "photoUrl " + photoUrl;

            Log.d(TAG, "getUserDetails: properties: \n" + properties);
        }
    }
    */

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
}
