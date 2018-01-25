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

import com.app.owl.R;
import com.app.owl.UserMainActivity;
import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.sleepSession.SleepSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    String circleName, selection, userUid, activityCurrentResponder;
    public static TextView snoozeClock, timeLeft;
    public static Spinner snoozeDuration;
    SleepCircle circle;
    SleepSession sleepSession;
    int seconds, minutes;
    FirebaseUser user;
    Boolean onGoingAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detector);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid();






        Intent intentFromNewSession = getIntent();
        circle = (SleepCircle) intentFromNewSession.getSerializableExtra("Sleep Circle");
        sleepSession = (SleepSession) intentFromNewSession.getSerializableExtra("Sleep Session");
        Log.d(TAG, "The sleep session received as extra as timestamp = " + sleepSession.getTimestamp());
        activityCurrentResponder =  sleepSession.getFirstResponder();

        snoozeDuration = findViewById(R.id.snooze_duration);
        addItemsOnSpinner(snoozeDuration);
        snoozeDuration.setVisibility(View.INVISIBLE);

        snoozeClock = findViewById(R.id.snooze_clock);
        snoozeClock.setVisibility(View.INVISIBLE);

        timeLeft = findViewById(R.id.time_left);
        timeLeft.setVisibility(View.INVISIBLE);

        snoozeBtn = findViewById(R.id.snooze_btn);
        snoozeBtn.setVisibility(View.INVISIBLE);

        snoozeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeBtn.setVisibility(View.INVISIBLE); // Hide snooze button
                snoozeClock.setVisibility(View.INVISIBLE); // Hide countdown to click snooze
                snoozeDuration.setVisibility(View.INVISIBLE); // Hide duration spinner
                snoozeTimer.cancel();
                snoozeTimer.purge();
                clockTimer.cancel();
                clockTimer.purge();
                selection = String.valueOf(snoozeDuration.getSelectedItem().toString());
                snooze(Integer.parseInt(selection));
            }
        });



        endAlert = findViewById(R.id.end_alert_btn);
        endAlert.setVisibility(View.INVISIBLE);
        endAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endSession.setVisibility(View.VISIBLE);
                endAlert.setVisibility(View.INVISIBLE);
                alertAnsweredTimer.cancel();
                alertAnsweredTimer.purge();
                checkAlertAnsweredTask.cancel();
                resolveAlert();
                snoozeDecide();

            }
        });

        endSession = findViewById(R.id.end_session_btn);
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Someone clicked end session button");
                if(timer != null){
                    timer.cancel();
                    timer.purge();
                }
                soundCapture.stop();
                updateOnGoingAlertDb(false,  circle.getUser1());
                updateOnGoingAlertDb(false,  circle.getUser2());
                udpdateUserOngoingSession(false, circle.getUser1());
                udpdateUserOngoingSession(false, circle.getUser2());
                String timeNow = String.valueOf(Calendar.getInstance().getTime());
                udpdateSessionEndTime(timeNow, circle.getUser1());
                udpdateSessionEndTime(timeNow, circle.getUser2());

                Log.d(TAG, "Redirection to welcome page");
                Intent intentReturnToWelcome = new Intent(SoundDetectorActivity.this, UserMainActivity.class);
                startActivity(intentReturnToWelcome);
            }
        });

        //Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions");
        Query query = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").orderByChild("timestamp").limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "Change happened to the sleep session");
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                for(DataSnapshot sessionSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "sessionSnapshot: " + sessionSnapshot);

                    SleepSession localSleepSession = sessionSnapshot.getValue(SleepSession.class);

                    if(localSleepSession.getNotificationIgnored()){
                        Log.d(TAG, "Someone ended the session from a phone. ");

                        Log.d(TAG, "localSleepSession.getStartTime(): " + localSleepSession.getStartTime());
                        Log.d(TAG, "sleepSession.getStartTime(): " + sleepSession.getStartTime());
                        Log.d(TAG, "localSleepSession.getNotificationIgnored(): " + localSleepSession.getNotificationIgnored());
                        Log.d(TAG, "localSleepSession.getTimestamp(): " + localSleepSession.getTimestamp());
                        Log.d(TAG, "sleepSession.getTimestamp(): " + sleepSession.getTimestamp());

                        database = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> childUpdates = new HashMap<>();
                        String path1 = "/MainUsers/" + sleepSession.getFirstResponder() + "/insideSession";
                        String path2 = "/MainUsers/" + sleepSession.getFirstResponder() + "/insideSession";
                        childUpdates.put(path1, false);
                        childUpdates.put(path2, false);
                        database.updateChildren(childUpdates);

                        if(timer != null){
                            timer.cancel();
                            timer.purge();
                        }
                        soundCapture.stop();
                        updateOnGoingAlertDb(false,  circle.getUser1());
                        updateOnGoingAlertDb(false,  circle.getUser2());
                        udpdateUserOngoingSession(false, circle.getUser1());
                        udpdateUserOngoingSession(false, circle.getUser2());


                        Log.d(TAG, "Redirection to welcome page");
                        Intent leaveIntent = new Intent(SoundDetectorActivity.this, UserMainActivity.class);
                        startActivity(leaveIntent);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // TODO: solve onGoingSession flag problem(not nonified when need to quit a session, only keep this method or the previous one
        Query query2 = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child("onGoingSession");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "inside query2");
                Log.d(TAG, "dataSnapshot " + dataSnapshot);
                for(DataSnapshot innerSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "innerSnapshot" + innerSnapshot);
                }
                /*
                Boolean isOngoing = dataSnapshot.getValue(Boolean.class);
                Log.d(TAG, "isOngoing: " + isOngoing);


                if(!isOngoing){
                    Log.d(TAG, "inside if loop of query2");
                    database = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();
                    String path1 = "/MainUsers/" + sleepSession.getFirstResponder() + "/insideSession";
                    String path2 = "/MainUsers/" + sleepSession.getFirstResponder() + "/insideSession";
                    childUpdates.put(path1, false);
                    childUpdates.put(path2, false);
                    database.updateChildren(childUpdates);

                    if(timer != null){
                        timer.cancel();
                        timer.purge();
                    }
                    soundCapture.stop();
                    updateOnGoingAlertDb(false,  circle.getUser1());
                    updateOnGoingAlertDb(false,  circle.getUser2());
                    udpdateUserOngoingSession(false, circle.getUser1());
                    udpdateUserOngoingSession(false, circle.getUser2());


                    Log.d(TAG, "Redirection to welcome page");
                    Intent leaveIntent = new Intent(SoundDetectorActivity.this, UserMainActivity.class);
                    startActivity(leaveIntent);

                }
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        if(timer != null){timer.cancel();}
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
                    onGoingAlert = true;
                    updateOnGoingAlertDb(true,  circle.getUser1());
                    updateOnGoingAlertDb(true,  circle.getUser2());
                    // TODO: Add “isnotified” flag to each MainUser. Set flag to false when alert is created in Detector, at the same time you set onGoing alert to true.
                    updateIsNotified(true,  circle.getUser1());
                    updateIsNotified(true,  circle.getUser2());
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
        Log.d(TAG, "Inside declareAlert - HideEndSession() Should get called ");
        new HideEndSession().execute();
        soundCapture.stop();
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



        alert = new Alert(String.valueOf(Calendar.getInstance().getTime()));

        Log.d(TAG, "Inside declareAlert, activityCurrentResponder:" + activityCurrentResponder);
        Log.d(TAG, "Inside declareAlert, sleepSession.getFirstResponder():" + sleepSession.getFirstResponder());
        Log.d(TAG, "Inside declareAlert, sleepSession.getSecondResponder():" + sleepSession.getSecondResponder());

        if(activityCurrentResponder.equals(sleepSession.getFirstResponder())){
            alert.setFirstResponderId(sleepSession.getFirstResponder());
            alert.setSecondResponderId(sleepSession.getSecondResponder());
        }else{
            alert.setFirstResponderId(sleepSession.getSecondResponder());
            alert.setSecondResponderId(sleepSession.getFirstResponder());
        }

        Log.d(TAG, "Inside declareAlert, alert:" + alert);
        // Add alert to database for each user
        alertHandler.registerAlertInUserDb(alert, sleepSession.getFirstResponder(), circleName, sleepSession.getStartTime());
        alertHandler.registerAlertInUserDb(alert, sleepSession.getSecondResponder(), circleName, sleepSession.getStartTime());

        countDownForResponse();

    }

    // WHen time runs out to click the "end alert" button
    public void countDownForResponse(){
        Log.d(TAG, "entered countDownForResponse - starting timer");

        alertAnsweredTimer = new Timer();
        checkAlertAnsweredTask = new TimerTask() {
            @Override
            public void run() {

                // TODO: Check that update works

                Log.d(TAG, "30second passed: closing the alert and starting sound detection again");

                updateOnGoingAlertDb(false,  circle.getUser1());
                updateOnGoingAlertDb(false,  circle.getUser2());


                String timeNow = String.valueOf(Calendar.getInstance().getTime());

                updateAlertEndDb(timeNow, circle.getUser1(), false);
                updateAlertEndDb(timeNow, circle.getUser2(), false);

/*

                // no one answered the alert after 30s
                database = FirebaseDatabase.getInstance().getReference();



                String path = "/MainUsers/" + circle.getUser1() + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertEnded/";
                // set alert.getAlertEnded() = true;
                AlertHandler.updateAlertBool(database, path, true);
                */

                /*
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path, true);
                database.updateChildren(childUpdates);
                */
/*


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
                */

                Log.d(TAG, "call startDetectingSounds();");
                new ShowEndSession().execute();
                soundCapture.start();
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
                        Log.d(TAG, "Inside snoozeDecide - Starting 30s timer");
                        new ShowSnoozeDuration().execute(); // Show snooze duration spiner
                        new ShowSnoozeBtn().execute(); // Show snooze button
                        new ShowTimeLeft().execute(); // Show snooze time left countdown

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

                        clockTimer = new Timer();
                        clockTask = new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Inside snoozeDecide - Should log the countdown every seconds:" + seconds);
                                //TODO: make countdown show
                                new SetTextSnoozeClock().execute(); // Update time left to click snooze button
                                seconds -= 1;
                            }
                        };



                        snoozeTimer.schedule(snoozeTask, 32000);
                        seconds = 30;
                        clockTimer.schedule(clockTask, 0, 1000);




                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Inside snoozeDecide - You said no to snoozing. sound detection will start soon");

                        soundCapture.start();
                        startDetectingSounds();
                    }}).show();
    }

    public void snooze(int snoozeDelay){
        Log.d(TAG, "Inside snooze - Snoozing for the chosen duration: " + snoozeDelay + " minutes");

        //when timer end, call startDetectingSounds()
        new ShowTimeLeft().execute(); // Show snooze time left countdown
        new SetTextTimeLeft().execute(String.valueOf(seconds)); // Test this
        snoozeTimer = new Timer();
        snoozeTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Inside snooze - Snoozer is done, time to go back to monitoring. sound detection will resume soon");
                new HideTimeLeft().execute();
                snoozeTimeLeft.cancel();
                soundCapture.start();
                startDetectingSounds();
            }
        };

        snoozeTimeLeft = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Inside snooze - This should log the snoozing time left every minute. Time left: " + minutes + " minutes");
                // TODO: make countdown show
                new SetTextTimeLeft().execute(String.valueOf(seconds)); // Update snooze time left countdown
                minutes -= 1;
            }
        };
        minutes = snoozeDelay; // snoozeDelay is in minutes
        int delay = minutes * 60 * 1000 + 200;
        snoozeTimer.schedule(snoozeTask, delay);
        snoozeTimeLeft.schedule(clockTask, 0, 60000);
    }

    public void stopDetectingSounds(){
        Log.d(TAG, "Inside snooze - stopDetectingSounds");
        timer.cancel();
    }

    public void resolveAlert(){
        Log.d(TAG, "Inside resolveAlert, ShowEndSession() should get called");

        new ShowEndSession().execute();
        String timeNow = String.valueOf(Calendar.getInstance().getTime());
        //updateAlertEndDb(timeNow);
        updateAlertEndDb(timeNow, circle.getUser1(), true);
        updateAlertEndDb(timeNow, circle.getUser2(), true);
        updateOnGoingAlertDb(false,  circle.getUser1());
        updateOnGoingAlertDb(false,  circle.getUser2());
        toggleCurrentResponder();


       // TODO: Make sure to call "alert.setAlertResponderId" from responder's device

    }

    // TODO this could be a Java class accessible to the whole program
    public void updateAlertEndDb(String endTime, String localUserUid, Boolean answered){

        // TODO: Check that update works

        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime() + "/Alerts/" + alert.getStartTime() + "/alertEnded/";
        // set alert.getAlertEnded() = true;
        AlertHandler.updateAlertBool(database, path, true);

        String now = String.valueOf(Calendar.getInstance().getTime());
        path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime() + "/Alerts/" + alert.getStartTime() + "/endTime/";
        // set alert.endTime = time
        AlertHandler.updateAlertTime(database, path, endTime);

        path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime() + "/Alerts/" + alert.getStartTime() + "/alertAnswered/";
        // set alertAnswered == false
        AlertHandler.updateAlertBool(database, path, answered);


    }

    public void updateIsNotified(Boolean isOngoing,  String localUserUid){

        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime() + "/isNotified/";

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, isOngoing);
        database.updateChildren(childUpdates);

    }

    public void updateOnGoingAlertDb(Boolean isOngoing,  String localUserUid){

        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime() + "/onGoingAlert/";

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, isOngoing);
        database.updateChildren(childUpdates);

    }

    private void udpdateSessionEndTime(String timeNow, String localUserUid){
        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/SleepSessions/" + sleepSession.getStartTime()  + "/endTime/";

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, timeNow);
        database.updateChildren(childUpdates);

    }

    private void udpdateUserOngoingSession(Boolean isOngoing, String localUserUid){
        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + localUserUid + "/onGoingSession/";

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, isOngoing);
        database.updateChildren(childUpdates);
    }
    public void toggleCurrentResponder(){

        if (activityCurrentResponder.equals(sleepSession.getFirstResponder())){
            activityCurrentResponder = sleepSession.getSecondResponder();
        }else{
            activityCurrentResponder = sleepSession.getFirstResponder();
        }

        database = FirebaseDatabase.getInstance().getReference();

        String path = "/MainUsers/" + circle.getUser1() + "/SleepSessions/" + sleepSession.getStartTime() + "/CurrentResponder/";

        AlertHandler.updateCurrentUser(database, path, activityCurrentResponder);

    }

    // add items into spinner dynamically
    public void addItemsOnSpinner(Spinner spinner) {
        List<String> list = new ArrayList<>();
        list.add("2");
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


    /*
    public void toggleListener(final ToggleListener listener) {
        if(onGoingAlert){
            listener.onTrue();
        }else{
            listener.onFalse();
        }

    }
    */



}
