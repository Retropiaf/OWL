package com.app.owl.sleepSession;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.owl.MainUser;
import com.app.owl.OnGetDataListener;
import com.app.owl.R;
import com.app.owl.UserMainActivity;
import com.app.owl.soundDetector.Alert;
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
import java.util.Timer;
import java.util.TimerTask;

public class OnGoingSleepSessionActivity extends AppCompatActivity {

    String TAG = "OnGoingSleepSessionActivity";
    FirebaseUser user;
    String userUid;
    Timer waitResponseTimer;
    TimerTask task;
    Ringtone ringtone;
    Button respondAlertBtn;
    View button;
    LayoutInflater inflater;
    LinearLayout pageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_sleep_session);


        // listen for new alert where you are the person in charge
        // when new alert received alarm rings and a buttons appears/ Alarm becomes louder and louder until responded. after 20s the other person is woken up

        // checking on alert button: click on this device and click on the monitor device
        // Change status of alert to oven
        // change name ef the next alert handler

        // TODO: on the responder's side, every second check if you are the current responder.
        // if you are the current responder, check if there is an ongoing alert
        // if there is an ongoing alert, play alarm.
        // if alert redirected == true, do something special
        // if no answer from responder after 30s and alertRedirected == false, change current responder, set alertRedirected = true and stop alarm.
        // if no answer from responder after 30s and alertRedirected == true, set speaker == true
        // if speaker == true, activate speaker alarm on both devices.
        // if no answer after 30s, end alarm, set alertAnswered = false, alert.endTime to now and alertEnded = true;
        // if responder click the "checking alert" button, set answer alertAnswered = true




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // TODO Check if the session is ignored, if so end session



        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();

        // TODO Create "alert received" button and on click listener in onCreate

        // Check is onGoingSession changed to false. If so, redirect to welcome page.
        DatabaseReference onGoingSessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid); // TODO: check spelling inside path
        onGoingSessionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                if(!localUser.getOnGoingSession()){
                    Log.d(TAG, "No open session");
                    Intent returnIntent = new Intent(OnGoingSleepSessionActivity.this, UserMainActivity.class);
                    startActivity(returnIntent);
                }else{
                    Log.d(TAG, "Ongoing session");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Find the right Session
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timeStamps").limitToLast(1); // TODO: check spelling inside path
        Log.d(TAG, "query: " + query);

        findSession(query, new OnGetDataListener(){
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Session was found" );
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);

                SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);

                final String startTime = localSleepSession.getStartTime();
                Log.d(TAG, "if the datashot was really a session, startTime should be a time. startTime: " + startTime);

                // Listen on the sleep session's changes
                DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(startTime); // TODO: check spelling inside path
                Log.d(TAG, "Get current session: " + sessionDatabase);

                sessionDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onAdded. The snapshot should say what change happened.");
                        Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                        // A new alert is created
                        // TODO: check if "s" would be "Alerts" for new alerts. If so, add: if(s == "Alerts"){ rest of the code }
                        Log.d(TAG, "If the datashapshot is an alert, change the code below this log");


                        /*

                        // Check if you are the currentResponder
                        final SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);

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
                                            handleAlert(alert, localSleepSession);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                         */


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "On changed");

                        /*
                        // Check if you are the currentResponder
                        final SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);

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
                                            handleAlert(alert, localSleepSession);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                         */


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
    public void handleAlert(Alert alert, SleepSession localSleepSession){
        final String alertStartTime = alert.getStartTime();
        final String sessionStartTime = localSleepSession.getStartTime();

        // Todo: start alarm
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        pageLayout = (LinearLayout) findViewById(R.id.activity_user_main);
        button = inflater.inflate(R.layout.respond_alert, null);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pageLayout.addView(button, 0);


        respondAlertBtn = findViewById(R.id.respond_alert_btn);
        respondAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeNow = String.valueOf(Calendar.getInstance().getTime());

                // update alert
                String path1 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/endTime/"; // TODO: check spelling inside path
                String path2 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertResponderId/"; // TODO: check spelling inside path
                String path3 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertAnswered/"; // TODO: check spelling inside path
                String path4 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertEnded/"; // TODO: check spelling inside path
                DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path1, timeNow);
                childUpdates.put(path2, userUid);
                childUpdates.put(path3, true);
                childUpdates.put(path4, true); // TODO: check if alert should end here or in the SoundDetector after the button was pushed there
                localDatabase.updateChildren(childUpdates);
                pageLayout.removeView((View) button.getParent());
                Toast.makeText(OnGoingSleepSessionActivity.this, "Don't forget to go check the monitor device.", Toast.LENGTH_SHORT).show();
            }
        });

        ringtone = RingtoneManager.getRingtone(OnGoingSleepSessionActivity.this, alarmUri);
        ringtone.play();

        waitResponseTimer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // User did not respond to alert

                ringtone.stop();

                String timeNow = String.valueOf(Calendar.getInstance().getTime());

                // update alert
                String path1 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/endTime/"; // TODO: check spelling inside path
                String path2 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertResponderId/"; // TODO: check spelling inside path
                String path3 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertAnswered/"; // TODO: check spelling inside path
                String path4 = "/MainUsers/" + userUid + "/SleepSessions/" + sessionStartTime + "/alerts/" + alertStartTime + "/alertEnded/"; // TODO: check spelling inside path
                DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path1, timeNow);
                childUpdates.put(path2, userUid);
                childUpdates.put(path3, false);
                childUpdates.put(path4, true);
                localDatabase.updateChildren(childUpdates);
                pageLayout.removeView((View) button.getParent());

            }
        };

        waitResponseTimer.schedule(task, 60000);

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
