package com.app.owl.sleepSession;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.owl.MainUser;
import com.app.owl.OnGetDataListener;
import com.app.owl.R;
import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.soundDetector.SoundDetectorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSleepSessionActivity extends AppCompatActivity {

    String TAG = "NewSleepSessionActivity", CIRCLE = "Sleep Circle", SLEEP_SESSION = "Sleep Session";
    String selected = "";
    String ip, circleName, selection, circleMonitorIp, clickedCircleName, userUid;
    InetAddress inetAddress;
    Spinner chooseCirclespinner;
    Button startMonitoringBtn;
    DatabaseReference database;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;
    SleepCircle circle;
    SleepSession sleepSession;
    FirebaseUser user;
    String firstResponder, secondResponder;
    TextView sleepSessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sleep_session);

        startMonitoringBtn = findViewById(R.id.start_monitoring_btn);
        startMonitoringBtn.setVisibility(View.INVISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();
        Log.d(TAG, "userUid = " + userUid);


        Intent intent = getIntent();
        SleepCircle circleFromIntent = (SleepCircle) intent.getSerializableExtra("Sleep Circle");
        clickedCircleName = circleFromIntent.getCircleName();

        sleepSessionInfo = findViewById(R.id.new_sleep_session_info);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!myWifi.isConnected()) {
            wifiManager.setWifiEnabled(true);
        }

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                Log.d(TAG, "Before getting ip address");

                inetAddress = InetAddress.getLocalHost();
                ip = String.valueOf(inetAddress);

                Log.d(TAG, "after getting ip address. ip = " + ip);
            } catch (UnknownHostException e) {
                System.out.println("I'm sorry. I don't know my own address. Connect to wifi, maybe?");
            }

        }


        chooseCirclespinner = findViewById(R.id.chooseCirclespinner);


        database = FirebaseDatabase.getInstance().getReference("MainUsers/"+userUid+"/circles/");

        final List<String> sleepCircles = new ArrayList<String>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "chooseCirclespinner, dataSnapshot = " + dataSnapshot );

                for (DataSnapshot circleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "chooseCirclespinner, circleSnapshot = " + circleSnapshot);
                    SleepCircle circle = circleSnapshot.getValue(SleepCircle.class);


                    String circleName = circle.getCircleName();
                    Log.d(TAG, "circleName = " + circleName);
                    sleepCircles.add(circleName);

                    ArrayAdapter<String> sleepCircleAdapter = new ArrayAdapter<String>(NewSleepSessionActivity.this, android.R.layout.simple_spinner_item, sleepCircles);
                    sleepCircleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chooseCirclespinner.setAdapter(sleepCircleAdapter);
                    if (clickedCircleName != null){
                        selectSpinnerValue(chooseCirclespinner, clickedCircleName);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        startMonitoringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = String.valueOf(chooseCirclespinner.getSelectedItem());
                Log.d(TAG, "selected = " + selected );

                // TODO: find the sleepCircle using the circle name (clickedCircleName)
                DatabaseReference sleepCircleDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("circles").child(selected);
                database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions");
                firstResponder = userUid;
                secondResponder = "";

                Log.d(TAG, "sleepCircleDatabase: " + sleepCircleDatabase);

                findCircle(selected, sleepCircleDatabase, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        final SleepCircle circle = dataSnapshot.getValue(SleepCircle.class);

                        // TODO: Check if any of the user already in a session if so toast and nothing. Else do the rest

                        database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(circle.getUser1());
                        Log.d(TAG, "database: " + database);
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                                final MainUser localUser1 = dataSnapshot.getValue(MainUser.class);
                                Log.d(TAG, "localUser1: " + localUser1);
                                if(!localUser1.getOnGoingSession()){
                                    database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(circle.getUser2());
                                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final MainUser localUser2 = dataSnapshot.getValue(MainUser.class);
                                            if(!localUser2.getOnGoingSession()) {
                                                Log.d(TAG, "firstResponder: " + firstResponder);
                                                Log.d(TAG, "circle.getUser1(): " + circle.getUser1());
                                                Log.d(TAG, "circle.getUser2(): " + circle.getUser2());

                                                if(firstResponder.equals(circle.getUser1())){
                                                    secondResponder = circle.getUser2();
                                                }else{
                                                    secondResponder = circle.getUser1();
                                                }

                                                //TODO: Check why several SleepSessions nodes get added

                                                Log.d(TAG, "secondResponder: " + secondResponder);
                                                String sleepSessionId =  database.push().getKey();

                                                sleepSession = new SleepSession(selected, sleepSessionId, firstResponder, firstResponder, secondResponder);

                                                addSessionDb(firstResponder, sleepSession.getStartTime(), sleepSession);
                                                addSessionDb(secondResponder, sleepSession.getStartTime(), sleepSession);

                                                udpdateUserOngoingSession(true, firstResponder);
                                                udpdateUserOngoingSession(true, secondResponder);

                                                //TODO update MainUser with ongoingSleepSession = true

                                                Intent intent = new Intent(NewSleepSessionActivity.this, SoundDetectorActivity.class);
                                                intent.putExtra(CIRCLE, circle);
                                                intent.putExtra(SLEEP_SESSION, sleepSession);

                                                startActivity(intent);

                                                Toast.makeText(NewSleepSessionActivity.this, "Starting sleep session", Toast.LENGTH_SHORT).show();

                                            }else{
                                                //  toast: user already has an ongoing session
                                                Toast.makeText(NewSleepSessionActivity.this, "One of the user already has an ongoing session", Toast.LENGTH_SHORT).show();
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




                    }
                    @Override
                    public void onStart() {
                        //when starting
                        Log.d("ONSTART", "Started");
                    }

                    @Override
                    public void onFailure() {
                        Log.d("onFailure", "Failed");
                    }
                });






            }
        });

        // TODO: Can ony start session from the monitoring device
        /*
        startSleepBtn = findViewById(R.id.start_sleep_btn);
        startSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected = String.valueOf(chooseCirclespinner.getSelectedItem());

                Intent intent = new Intent(NewSleepSessionActivity.this, OnGoingSleepSessionActivity.class);
                intent.putExtra(CIRCLE_NAME, circle.getCircleId());
                startActivity(intent);

            }
        });
        startSleepBtn.setVisibility(View.INVISIBLE);
        */

        startMonitoringBtn.setVisibility(View.INVISIBLE);

        chooseCirclespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Log.d(TAG, "new item selected");
                selection = String.valueOf(chooseCirclespinner.getSelectedItem());
                database = FirebaseDatabase.getInstance().getReference("MainUsers/"+userUid+"/circles/");
                Log.d(TAG, "database: " + database);

                startMonitoringBtn.setVisibility(View.INVISIBLE);
                sleepSessionInfo.setVisibility(View.VISIBLE);
                // set startSleepBtn visible
                // startSleepBtn.setVisibility(View.VISIBLE);



                database.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        circle = dataSnapshot.getValue(SleepCircle.class);

                        // TODO make circleName private to this method?
                        circleName = circle.getCircleName();
                        circleMonitorIp = circle.getMonitorIp();

                        if(selection.equals(circleName) && ip.equals(circleMonitorIp)){
                            Log.d(TAG, "selection = " + selection);
                            Log.d(TAG, "circleName = " + circleName);
                            Log.d(TAG, "ip = " + ip);
                            Log.d(TAG, "circleMonitorIp = " + circleMonitorIp);
                            Log.d(TAG, "Selected circle device and current device are the same");
                            startMonitoringBtn.setVisibility(View.VISIBLE);
                            // set startSleepBtn invisible
                            //startSleepBtn.setVisibility(View.INVISIBLE);
                            sleepSessionInfo.setVisibility(View.INVISIBLE);

                        }
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
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

                Log.d(TAG, "Nothing selected");
                startMonitoringBtn.setVisibility(View.INVISIBLE);
                sleepSessionInfo.setVisibility(View.VISIBLE);
                //startSleepBtn.setVisibility(View.INVISIBLE);

            }

        });




        //CANCEL
        // Add active status:
        // https://stackoverflow.com/questions/5504632/how-can-i-tell-if-android-app-is-running-in-the-foreground
        // https://android.jlelse.eu/how-to-detect-android-application-open-and-close-background-and-foreground-events-1b4713784b57
        // http://engineering.meetme.com/2015/04/android-determine-when-app-is-opened-or-closed/
        // Check if both users and the monitor are logged in and active

        // add Start button. Greyed-out is users not online / normal if everyone is online


        // TODO OnClick: call Make New Session

        // TODO Create new session with both users uid and usernames and with the monitor uid and name
        // add start date and time

        // TODO Make the monitor the alert giver

        // TODO new intent: open On Going sleep session activity // open On Going monitoring activity on the monitor




    }

    public void findCircle(final String localCircleName, DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        Log.d(TAG, "inside findCircle, ref = " + ref);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SleepCircle circle1 = dataSnapshot.getValue(SleepCircle.class);
                if (circle1.getCircleName().equals(localCircleName)){
                    listener.onSuccess(dataSnapshot);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        Log.d(TAG, "Inside selectSpinnerValue");
        for(int i = 0; i < spinner.getCount(); i++){
            if(String.valueOf(spinner.getItemAtPosition(i)).equals(myString)){
                spinner.setSelection(i);
                Log.d(TAG, "i:" + i);
                break;
            }
        }
        spinner.setSelection(0);
    }

    private void addSessionDb(String localUserUid, String sessionStartTime, SleepSession localNewSleepSession){

        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sessionStartTime;


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, localNewSleepSession);
        database.updateChildren(childUpdates);
    }

    private void udpdateUserOngoingSession(Boolean isOngoing, String localUserUid){
        database = FirebaseDatabase.getInstance().getReference();

        final String path = "/MainUsers/" + localUserUid + "/onGoingSession/";

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, isOngoing);
        database.updateChildren(childUpdates);
    }
}

