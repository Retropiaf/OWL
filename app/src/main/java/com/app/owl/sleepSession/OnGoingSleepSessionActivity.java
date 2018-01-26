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
import com.app.owl.R;
import com.app.owl.UserMainActivity;
import com.app.owl.soundDetector.Alert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();

        pageLayout = (LinearLayout) findViewById(R.id.activity_user_main);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        button = inflater.inflate(R.layout.new_session_notification, null);

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

        // Check if the session is ignored or ended. If so, redirect to main page
        DatabaseReference localDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);

        ValueEventListener currentSessionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Changed happened to current user");
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                String currentSession = localUser.getCurrentSession();

                Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").equalTo(currentSession);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                            SleepSession session = sessionSnapshot.getValue(SleepSession.class);
                            if(session.getNotificationIgnored()){
                                if(session.getNotificationIgnoredBy() != null || !session.getNotificationIgnoredBy().equals("")){
                                    Toast.makeText(getApplicationContext(), "Session was ignores by " + session.getNotificationIgnoredBy() , Toast.LENGTH_SHORT).show();
                                    Intent intentReturnToWelcome = new Intent(OnGoingSleepSessionActivity.this, UserMainActivity.class);
                                    startActivity(intentReturnToWelcome);
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Session was ignored by the other user", Toast.LENGTH_SHORT).show();
                                    Intent intentReturnToWelcome = new Intent(OnGoingSleepSessionActivity.this, UserMainActivity.class);
                                    startActivity(intentReturnToWelcome);
                                    finish();
                                }


                            }else if(session.getEndTime() != null || !session.getEndTime().equals("")){
                                Toast.makeText(getApplicationContext(), "Session was terminated", Toast.LENGTH_SHORT).show();
                                Intent intentReturnToWelcome = new Intent(OnGoingSleepSessionActivity.this, UserMainActivity.class);
                                startActivity(intentReturnToWelcome);
                                finish();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        localDatabase.addValueEventListener(currentSessionListener);








        // Check if there is a current alert
        // Check if the session is ignored or ended. If so, redirect to main page
        DatabaseReference alertDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid);
        ValueEventListener alertListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainUser localUser = dataSnapshot.getValue(MainUser.class);
                final String currentAlert = localUser.getCurrentAlert();
                final String currentSession = localUser.getCurrentSession();
                final String currentSecondUser = localUser.getCurrentSecondUser();
                if(currentAlert != null || !currentAlert.equals("") && currentSession != null || !currentSession.equals("")){
                    Log.d(TAG, "There is an ongoing alert");
                    final String alertStart = currentAlert;

                    // find session's current responder. If current user find alert
                    final Query localSessionDatabase2 = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").equalTo(currentSession);
                    Log.d(TAG, "localSessionDatabase: " + localSessionDatabase2);
                    ValueEventListener localSessionListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDatachange 2");
                            Log.d(TAG, "onDatachange 2, dataSnapshot:" + dataSnapshot);
                            final SleepSession localSession = dataSnapshot.getValue(SleepSession.class);
                            if(localSession != null && !localSession.equals("")){
                                Log.d(TAG, "localSession.getCurrentResponder(): " + localSession.getCurrentResponder());
                                Log.d(TAG, "userUid: " + userUid);
                            }

                            if(localSession != null && !localSession.equals("") && localSession.getCurrentResponder() != null && localSession.getCurrentResponder().equals(userUid)){

                                Log.d(TAG, "I'm the current responder");

                                // find alert
                                DatabaseReference innerAlertDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(currentSession).child("alerts").child(alertStart);

                                Log.d(TAG, "innerAlertDatabase; " + innerAlertDatabase);
                                ValueEventListener innerAlertListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d(TAG, "onDatachange 3");
                                        Log.d(TAG, "dataSnapshot:  " + dataSnapshot);

                                        Log.d(TAG, "before calling handle Alert");
                                        Alert currentAlert = dataSnapshot.getValue(Alert.class);
                                        handleAlert(currentAlert, localSession, currentSecondUser);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                        Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                };
                                innerAlertDatabase.addValueEventListener(innerAlertListener);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };
                    localSessionDatabase2.addValueEventListener(localSessionListener);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        alertDatabase.addValueEventListener(alertListener);






    }

    public void onChecking(View button, String alertStartTime, String sessionStartTime, String userUid2) {

        Log.d(TAG, "onChecking");

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
        childUpdates.put(path4, true);
        localDatabase.updateChildren(childUpdates);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates2 = new HashMap<>();
        String path5 = "/MainUsers/"+ userUid + "/currentAlert/";
        String path6 = "/MainUsers/"+ userUid2 + "/currentAlert/"; // TODO add second user
        childUpdates.put(path5, timeNow);
        childUpdates.put(path6, timeNow);
        database.updateChildren(childUpdates2);
        pageLayout.removeView((View) button.getParent());
        Toast.makeText(OnGoingSleepSessionActivity.this, "Don't forget to go check the monitor device.", Toast.LENGTH_SHORT).show();
    }

    public void handleAlert(Alert alert, SleepSession localSleepSession, String secondUser){

        Log.d(TAG, "in  handle Alert");

        final String alertStartTime = alert.getStartTime();
        final String sessionStartTime = localSleepSession.getStartTime();
        final String userUid2 = secondUser;

        // Todo: start alarm
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        button = inflater.inflate(R.layout.new_session_notification, null);
        pageLayout.addView(button, 0);



        onChecking(button, alertStartTime, sessionStartTime, userUid2);

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


                DatabaseReference localDatabase2 = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates2 = new HashMap<>();
                String path5 = "/MainUsers/"+ userUid + "/currentAlert/";
                String path6 = "/MainUsers/"+ userUid2 + "/currentAlert/";
                childUpdates.put(path5, "");
                childUpdates.put(path6, "");
                localDatabase2.updateChildren(childUpdates2);

                pageLayout.removeView((View) button.getParent());

            }
        };

        waitResponseTimer.schedule(task, 60000);

    }


}
