package com.app.owl.soundDetector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.owl.OnGetDataListener;
import com.app.owl.R;
import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.sleepSession.SleepSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class SoundDetectorActivity extends AppCompatActivity {

    String TAG = "SoundDetectorActivity";
    SoundCapture soundCapture;
    Timer timer, snoozeTimer, clockTimer, snoozeTimeLeft, alertAnsweredTimer;
    TimerTask task, snoozeTask, clockTask, checkAlertAnsweredTask;
    BuildSoundArray soundArray;
    isSignificant is_Significant;
    AlertHandler alertHandler;
    Alert alert;
    public static Button endSession, endAlert, snoozeBtn;
    DatabaseReference database, sleepSessionDatabase;
    String circleName, selection, userUid;
    TextView snoozeClock, timeLeft;
    Spinner snoozeDuration;
    SleepCircle circle;
    SleepSession sleepSession;
    int seconds, minutes;
    FirebaseUser user;
    Thread t1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detector);

        ShowEndSessionBtn showEndSessionBtn = new ShowEndSessionBtn(SoundDetectorActivity.this);
        t1 = new Thread(showEndSessionBtn);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();

        Intent intent = getIntent();
        circle = (SleepCircle) intent.getSerializableExtra("Sleep Circle");
        sleepSession = (SleepSession) intent.getSerializableExtra("Sleep Session");

        snoozeDuration = findViewById(R.id.snooze_duration);
        addItemsOnSpinner(snoozeDuration);
        snoozeDuration.setVisibility(View.INVISIBLE);



        snoozeBtn = findViewById(R.id.snooze_btn);
        snoozeBtn.setVisibility(View.INVISIBLE);
        snoozeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeBtn.setVisibility(View.INVISIBLE);
                snoozeClock.setVisibility(View.INVISIBLE);
                snoozeTimer.cancel();
                selection = String.valueOf(snoozeDuration.getSelectedItem().toString());
                snooze(Integer.parseInt(selection));
            }
        });

        snoozeClock = findViewById(R.id.snooze_clock);
        snoozeClock.setVisibility(View.INVISIBLE);

        timeLeft = findViewById(R.id.time_left);
        timeLeft.setVisibility(View.INVISIBLE);


        endSession = findViewById(R.id.end_session_btn);
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeDecide();
                onDestroy();
                // TODO: Go back intent
            }
        });

        endAlert = findViewById(R.id.end_alert_btn);
        endAlert.setVisibility(View.INVISIBLE);
        endAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endSession.setVisibility(View.VISIBLE);
                endAlert.setVisibility(View.INVISIBLE);
                resolveAlert();
                soundCapture.start();
                startDetectingSounds();
            }
        });

        soundArray = new BuildSoundArray();
        alertHandler = new AlertHandler();
        soundCapture = new SoundCapture();
        alertHandler = new AlertHandler();
        askPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        soundCapture.stop();
        //wakeLock.release();
        // TODO LOCK THE SCREEN ON
    }

    public void askPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No MODIFY_AUDIO_SETTINGS" );
        else
            Log.d("App", "Yes MODIFY_AUDIO_SETTINGS" );

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No RECORD_AUDIO" );
        else

        ActivityCompat.requestPermissions( this, new String[]
                {
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        android.Manifest.permission.RECORD_AUDIO
                },1 );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean success = true;
        Log.d("App","in onRequestPermissionsResult");
        for( int i = 0; i < permissions.length; ++i ) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                Log.d("App",permissions[i]+" granted");
            } else {
                Log.d("App",permissions[i]+" denied");
                Toast.makeText(getApplicationContext(), "Needed " + permissions[i], Toast.LENGTH_SHORT).show();
                success = false;
            }
        }
        if( success ) {
            soundCapture.start();
            startDetectingSounds();
        }
    }

    public void startDetectingSounds() {

        timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {
                final double sound = soundCapture.getAmplitude();
                double[] arrayAtTime = soundArray.addSound(sound);

                Log.d("arrayAtTime", String.valueOf(arrayAtTime[0]) + " - " + String.valueOf(arrayAtTime[1]) + " - " + String.valueOf(arrayAtTime[2]) + " - " + String.valueOf(arrayAtTime[3]) + " - " + String.valueOf(arrayAtTime[4]));

                is_Significant = new isSignificant(arrayAtTime); // Create new isSignificant class
                Boolean significant = new isSignificant(arrayAtTime).significant(); // Call the significant() method on isSignificant

                if (significant) {
                    Log.d(TAG, "This is an alert!");
                    declareAlert();
                }
            }
        };

        timer.schedule(task, 0, 1000);
    }

    /*
    public void changeVisibility() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                endSession.setVisibility(View.INVISIBLE);
                endAlert.setVisibility(View.VISIBLE);
                Toast.makeText(SoundDetectorActivity.this, "New alert received" , Toast.LENGTH_SHORT).show();
            }
        };
        thread.start();
    }
    */


    public void declareAlert(){

        stopDetectingSounds();
        //t1.start();

        // TODO: fix change button visibility
/*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                endSession.setVisibility(View.INVISIBLE);

                endAlert.setVisibility(View.VISIBLE);
            }
        });
        */



        alert = new Alert();

        sleepSessionDatabase = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("Sleep Sessions").child(sleepSession.getStart_time());

        findSession(sleepSessionDatabase, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                SleepSession sleepSession = dataSnapshot.getValue(SleepSession.class);

                if(sleepSession.getCurrentResponder().equals(sleepSession.getFirstResponder())){
                    alert.setFirstResponderId(sleepSession.getFirstResponder());
                    alert.setSecondResponderId(sleepSession.getSecondResponder());
                }else{
                    alert.setFirstResponderId(sleepSession.getSecondResponder());
                    alert.setSecondResponderId(sleepSession.getFirstResponder());
                }

                // Add alert to database for each user
                alertHandler.registerAlertInUserDb(alert, sleepSession.getFirstResponder(), circleName, sleepSession.getStart_time());
                alertHandler.registerAlertInUserDb(alert, sleepSession.getSecondResponder(), circleName, sleepSession.getStart_time());

                countDownForResponse();
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

    // WHen time runs out to click the "end alert" button
    public void countDownForResponse(){
        Log.d(TAG, "entered countDownForResponse - starting timer");

        alertAnsweredTimer = new Timer();
        checkAlertAnsweredTask = new TimerTask() {
            @Override
            public void run() {

                Log.d(TAG, "30second passed: closing the alert and starting sound detection again");

                // no one answered the alert after 30s
                database = FirebaseDatabase.getInstance().getReference();



                String path = "/MainUsers/" + circle.getUser1() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertEnded/";
                // set alert.getAlertEnded() = true;

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path, true);
                database.updateChildren(childUpdates);
                //AlertHandler.updateAlertBool(database, path, true);

                String now = String.valueOf(Calendar.getInstance().getTime());
                path = "/MainUsers/" + circle.getUser1() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/endTime/";
                // set alert.endTime = now
                AlertHandler.updateAlertTime(database, path, now);

                path = "/MainUsers/" + circle.getUser1() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertAnswered/";
                // set alertAnswered == false
                AlertHandler.updateAlertBool(database, path, false);

                path = "/MainUsers/" + circle.getUser2() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertEnded/";
                // set alert.getAlertEnded() = true;
                AlertHandler.updateAlertBool(database, path, true);

                now = String.valueOf(Calendar.getInstance().getTime());
                path = "/MainUsers/" + circle.getUser2() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/endTime/";
                // set alert.endTime = now
                AlertHandler.updateAlertTime(database, path, now);

                path = "/MainUsers/" + circle.getUser2() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertAnswered/";
                // set alertAnswered == false
                AlertHandler.updateAlertBool(database, path, false);

                Log.d(TAG, "call startDetectingSounds();");

                startDetectingSounds();
            }
        };
        alertAnsweredTimer.schedule(checkAlertAnsweredTask, 32000);

    }

    public void snoozeDecide(){
        new AlertDialog.Builder(SoundDetectorActivity.this)
                .setTitle("Snooze?")
                .setMessage("Do you want to set a delay before next alert?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        snoozeTimer = new Timer();
                        snoozeTask = new TimerTask() {
                            @Override
                            public void run() {
                                snoozeBtn.setVisibility(View.INVISIBLE);
                                snoozeClock.setVisibility(View.INVISIBLE);
                                clockTimer.cancel();
                                startDetectingSounds();
                            }
                        };

                        clockTimer = new Timer();
                        clockTask = new TimerTask() {
                            @Override
                            public void run() {
                                snoozeClock.setText(seconds);
                                seconds -= 1;
                            }
                        };
                        snoozeTimer.schedule(snoozeTask, 32000);
                        seconds = 30;
                        snoozeClock.setVisibility(View.VISIBLE);
                        clockTimer.schedule(clockTask, 0, 1000);


                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startDetectingSounds();
                    }}).show();
    }

    public void snooze(int snoozeDelay){
        // take is the value chosen and start a timer with that period of time
        //when timer end, call startDetectingSounds()
        snoozeTimer = new Timer();
        snoozeTask = new TimerTask() {
            @Override
            public void run() {
                timeLeft.setVisibility(View.INVISIBLE); // make invisible
                snoozeTimeLeft.cancel();
                startDetectingSounds();
            }
        };

        snoozeTimeLeft = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {

                timeLeft.setText(minutes);
                minutes -= 1;
            }
        };
        minutes = snoozeDelay; // snoozeDelay is in minutes
        int delay = minutes * 60 * 1000 + 200;
        snoozeTimer.schedule(snoozeTask, delay);
        snoozeClock.setVisibility(View.VISIBLE);
        snoozeTimeLeft.schedule(clockTask, 0, 60000);
    }
    public void stopDetectingSounds(){
        timer.cancel();
    }

    public void resolveAlert(){
        String timeNow = String.valueOf(Calendar.getInstance().getTime());
        updateAlertEndDb(timeNow);
        toggleCurrentResponder();



       // TODO: Make sure to call "alert.setAlertResponderId" from responder's device

    }

    // TODO this could be a Java class accessible to the whole program
    public void updateAlertEndDb(String time){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/endTime/", time);
        childUpdates.put("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertEnded/", true);
        database.updateChildren(childUpdates);

    }
    public void toggleCurrentResponder(){
        String currentResponder = FirebaseDatabase.getInstance().getReference("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/currentResponder/").toString();

        Log.d(TAG, "currentResponder retrieved from the database with full path: " + currentResponder);

        String firstResponderId = FirebaseDatabase.getInstance().getReference("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/firstResponderId/").toString();

        String secondResponderId = FirebaseDatabase.getInstance().getReference("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/secondResponderId/").toString();

        if (currentResponder.equals(firstResponderId)){
            currentResponder = secondResponderId;
        }else{
            currentResponder = firstResponderId;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/currentResponder/", currentResponder);
        database.updateChildren(childUpdates);
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner(Spinner spinner) {
        List<String> list = new ArrayList<>();
        list.add("10");
        list.add("15");
        list.add("20");
        list.add("30");
        list.add("40");
        list.add("50");
        list.add("60");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void findSession(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        Log.d(TAG, "inside findSession, ref = " + ref);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot = " + dataSnapshot);
                SleepSession localSleepSession = dataSnapshot.getValue(SleepSession.class);
                if (localSleepSession.getStart_time().equals(sleepSession.getStart_time())){
                    listener.onSuccess(dataSnapshot);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }


}
