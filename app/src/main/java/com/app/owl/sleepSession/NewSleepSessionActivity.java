package com.app.owl.sleepSession;

import android.content.Context;
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

import com.app.owl.R;
import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.sleepCircle.SleepCircleList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NewSleepSessionActivity extends AppCompatActivity {

    String TAG = "NewSleepSessionActivity";
    String selected = "";
    String uid, ip, circleName, selection, circleMonitorIp;
    InetAddress inetAddress;
    Spinner chooseCirclespinner;
    Button startSleepBtn, startMonitoringBtn;
    SleepCircleList sleepCircleList; // The adapter
    ArrayList<SleepCircle> list;
    DatabaseReference database;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;
    Boolean monitorDevice;
    SleepCircle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sleep_session);

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

        database = FirebaseDatabase.getInstance().getReference().child("SleepCircles");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> sleepCircles = new ArrayList<String>();

                for (DataSnapshot sleepCircleSnapshot : dataSnapshot.getChildren()) {
                    String circleName = sleepCircleSnapshot.child("circleName").getValue(String.class);
                    sleepCircles.add(circleName);
                }


                ArrayAdapter<String> sleepCircleAdapter = new ArrayAdapter<String>(NewSleepSessionActivity.this, android.R.layout.simple_spinner_item, sleepCircles);
                sleepCircleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                chooseCirclespinner.setAdapter(sleepCircleAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        startMonitoringBtn = findViewById(R.id.start_monitoring_btn);
        startMonitoringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = String.valueOf(chooseCirclespinner.getSelectedItem());
                if (!selected.equals("")) {
                    Log.d(TAG, "Selection: " + selected);
                } else {
                    Log.d(TAG, "nothing is selected");
                }
                                        /*
                                        Intent intent = new Intent(NewSleepSessionActivity.this, OnGoingMonitoringActivity.class);
                                        startActivity(intent);
                                        */
            }
        });
        startSleepBtn = findViewById(R.id.start_sleep_btn);
        startSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected = String.valueOf(chooseCirclespinner.getSelectedItem());
                if (!selected.equals("")) {
                    Log.d(TAG, "Selection: " + selected);
                } else {
                    Log.d(TAG, "nothing is selected");
                }
                                        /*
                                        Intent intent = new Intent(NewSleepSessionActivity.this, OnGoingSleepSessionActivity.class);
                                        startActivity(intent);
                                        */
            }
        });

        startMonitoringBtn.setVisibility(View.INVISIBLE);
        startSleepBtn.setVisibility(View.INVISIBLE);



        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chooseCirclespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Log.d(TAG, "new item selected");
                selection = String.valueOf(chooseCirclespinner.getSelectedItem());
                database = FirebaseDatabase.getInstance().getReference("MainUsers/"+uid+"/circles/");
                Log.d(TAG, "database: " + database);

                startMonitoringBtn.setVisibility(View.INVISIBLE);
                // set startSleepBtn visible
                startSleepBtn.setVisibility(View.VISIBLE);

                database.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        circle = dataSnapshot.getValue(SleepCircle.class);

                        circleName = circle.getCircleName();
                        circleMonitorIp = circle.getMonitorIp();

                        if(selection.equals(circleName) && ip.equals(circleMonitorIp)){
                            Log.d(TAG, "Selected circle device and current device are the same");
                            startMonitoringBtn.setVisibility(View.VISIBLE);
                            // set startSleepBtn invisible
                            startSleepBtn.setVisibility(View.INVISIBLE);

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
                startSleepBtn.setVisibility(View.INVISIBLE);

            }

        });




        //CANCEL
        // Add active status:
        // https://stackoverflow.com/questions/5504632/how-can-i-tell-if-android-app-is-running-in-the-foreground
        // https://android.jlelse.eu/how-to-detect-android-application-open-and-close-background-and-foreground-events-1b4713784b57
        // http://engineering.meetme.com/2015/04/android-determine-when-app-is-opened-or-closed/
        // Check if both users and the monitor are logged in and active

        // add Start button. Greyed-out is users not online / normal if everyone is online


        // OnClick: call Make New Session

        // Create new session with both users uid and usernames and with the monitor uid and name
        // add start date and time

        // Make the monitor the alert giver

        // new intent: open On Going sleep session activity // open On Going monitoring activity on the monitor


    }
    @Override
    protected void onStart() {
        super.onStart();
        /*
        monitorDevice = false;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("MainUser").child(uid).child("SleepCircles");
        chooseCirclespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    inetAddress = InetAddress.getLocalHost();
                    ip = String.valueOf(inetAddress);

                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                SleepCircle circle = snapshot.getValue(SleepCircle .class);

                                if(circle.getMonitorIp() == ip){
                                    monitorDevice = true;
                                }

                            }
                            if(monitorDevice){
                                //set startMonitoringBtn visible
                                startMonitoringBtn.setVisibility(View.VISIBLE);
                                // set startSleepBtn invisible
                                startSleepBtn.setVisibility(View.INVISIBLE);
                            }else{
                                // JOIN SLEEP SESSION and or start monitpring on monitor device
                                //set startMonitoringBtn invisible
                                startMonitoringBtn.setVisibility(View.INVISIBLE);
                                // set startSleepBtn visible
                                startSleepBtn.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } catch (UnknownHostException e) {
                    System.out.println("I'm sorry. I don't know my own address. Connect to wifi, maybe?");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        */
    }
}

