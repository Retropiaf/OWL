package com.app.owl.sleepSession;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.app.owl.R;
import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.sleepCircle.SleepCircleList;

import java.util.ArrayList;

public class NewSleepSessionActivity extends AppCompatActivity {

    String TAG = "NewSleepSessionActivity";

    Spinner chooseCirclespinner;
    Button startSleepBtn, startMonitoringBtn;
    SleepCircleList sleepCircleList; // The adapter
    ArrayList<SleepCircle> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sleep_session);


        // Choose a Circle (or get circle from SleepCircle intent extra)
        chooseCirclespinner = findViewById(R.id.chooseCirclespinner);
        // Todo: fill in the spinner and add a method to get selected value
        sleepCircleList = new SleepCircleList(NewSleepSessionActivity.this, list);
        // Specify the layout to use when the list of choices appears
        sleepCircleList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        chooseCirclespinner.setAdapter(sleepCircleList);

        String selected = chooseCirclespinner.getSelectedItem().toString();
        if (selected.equals("what ever the option was")) {
            Log.d(TAG, "Selection: " + selected);
        }

                // TODO: turn wifi on

        startSleepBtn = findViewById(R.id.start_sleep_btn);
        startSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewSleepSessionActivity.this, OnGoingSleepSessionActivity.class);
                startActivity(intent);
            }
        });

        startMonitoringBtn = findViewById(R.id.start_monitoring_btn);
        startMonitoringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewSleepSessionActivity.this, OnGoingMonitoringActivity.class);
                startActivity(intent);
            }
        });

        /*

        if(true){
            //set startMonitoringBtn visible
            startMonitoringBtn.setVisibility(View.VISIBLE);
            // set startSleepBtn invisible
            startSleepBtn.setVisibility(View.INVISIBLE);
        }else{
            //set startMonitoringBtn invisible
            startMonitoringBtn.setVisibility(View.INVISIBLE);
            // set startSleepBtn visible
            startSleepBtn.setVisibility(View.INVISIBLE);
        }
        */

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
}
