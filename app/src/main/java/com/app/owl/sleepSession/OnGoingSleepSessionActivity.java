package com.app.owl.sleepSession;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.owl.R;

public class OnGoingSleepSessionActivity extends AppCompatActivity {

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




    }
}
