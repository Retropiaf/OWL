package com.app.owl.soundDetector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.owl.R;
import com.app.owl.sleepCircle.SleepCirclesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SoundDetectorActivity extends AppCompatActivity {

    String TAG = "SoundDetectorActivity";
    SoundCapture soundCapture;
    Timer timer;
    int seconds;
    BuildSoundArray soundArray;
    isSignificant is_Significant;
    AlertHandler alertHandler;
    Alert alert;
    Button endSession, endAlert;
    DatabaseReference database;
    String uid, circleName, lastFirstResponder, lastSecondResponder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detector);

        Intent intent = getIntent();
        circleName = intent.getStringExtra(SleepCirclesActivity.CIRCLE_NAME);


        endSession = findViewById(R.id.end_session_btn);
        Log.d(TAG, String.valueOf(endSession));

        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        endAlert = findViewById(R.id.end_alert_btn);


        endAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setEndTime(Calendar.getInstance().getTime());
                endSession.setVisibility(View.VISIBLE);
                endAlert.setVisibility(View.INVISIBLE);

            }
        });

        endAlert.setVisibility(View.INVISIBLE);

        timer = new Timer();
        soundArray = new BuildSoundArray();
        seconds = 0;
        alertHandler = new AlertHandler();

        soundCapture = new SoundCapture();

        askPermissions();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        soundCapture.stop();
        //wakeLock.release();

    }

    public void askPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No MODIFY_AUDIO_SETTINGS" );
        else
            Log.d("App", "Yes MODIFY_AUDIO_SETTINGS" );

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No RECORD_AUDIO" );
        else
            Log.d("App", "Yes RECORD_AUDIO" );

        Log.d("App","Requesting permissions" );
        ActivityCompat.requestPermissions( this, new String[]
                {
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        android.Manifest.permission.RECORD_AUDIO
                },1 );
        Log.d("App","Requested perms");

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
            Log.d("App","Setting up visualizer");
            MyTimer();
            //wakeLock.acquire();
            soundCapture.start();
        }



    }

    public void MyTimer() {

        TimerTask task;




        task = new TimerTask() {
            //int i = 0;
            @Override
            public void run() {
                final double sound = soundCapture.getAmplitude();
                double[] arrayAtTime = soundArray.addSound(sound);

                Log.d("arrayAtTime", String.valueOf(arrayAtTime[0]) + " - " + String.valueOf(arrayAtTime[1]) + " - " + String.valueOf(arrayAtTime[2]) + " - " + String.valueOf(arrayAtTime[3]) + " - " + String.valueOf(arrayAtTime[4]));

                is_Significant = new isSignificant(arrayAtTime); // Create new isSignificant class
                Boolean significant = new isSignificant(arrayAtTime).significant(); // Call the significant() method on isSignificant

                // Handle the alert
                if (significant) {
                    Log.d("Alert", "alert parents");

                    endSession = findViewById(R.id.end_session_btn);
                    endAlert.setVisibility(View.INVISIBLE);
                    endAlert = findViewById(R.id.end_alert_btn);
                    endAlert.setVisibility(View.VISIBLE);

                    alert = new Alert();
                    AlertHandler alertHandler = new AlertHandler();


                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();



                    database = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(uid).child("circles").child(circleName);
                    Query lastAlert = database.orderByChild("startTime").limitToFirst(1);

                    lastAlert.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Alert alert = postSnapshot.getValue(Alert.class);
                                lastFirstResponder = alert.getFirstResponderId();
                                lastSecondResponder = alert.getSecondResponderId();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

                    alert.setFirstResponderId(lastSecondResponder);
                    alert.setSecondResponderId(lastFirstResponder);

                    alertHandler.registerAlert(alert, uid, circleName);

                    // TODO: leave timer when alert given and until new alert

                    /*

                    database.orderByChild("startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final List<String> sleepCircles = new ArrayList<String>();

                            for (DataSnapshot sleepCircleSnapshot : dataSnapshot.getChildren()) {
                                String circleName = sleepCircleSnapshot.child("circleName").getValue(String.class);
                                sleepCircles.add(circleName);
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    */

                    //alertHandler.registerAlert(alert);
                    Log.d(TAG, "Sound alert received");
                    //Toast.makeText(SoundDetectorActivity.this, "Sound alert received", Toast.LENGTH_SHORT).show();
                }

                seconds += 1;

            }
        };
        timer.schedule(task, 0, 1000);

    }

}
