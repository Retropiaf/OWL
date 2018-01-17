package com.app.owl.SoundDetector;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SoundDetectorActivity extends AppCompatActivity {

    SoundCapture soundCapture;
    Timer timer;
    int seconds;
    BuildSoundArray soundArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = new Timer();
        soundArray = new BuildSoundArray();
        seconds = 0;


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
                Log.d("Timer", "Seconds = " + seconds);
                soundArray.addSound(soundCapture.getAmplitude());

                seconds += 1;

            }
        };
        timer.schedule(task, 0, 1000);

    }

}
