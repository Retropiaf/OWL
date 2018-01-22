package com.app.owl.sleepSession;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.owl.R;

public class OnGoingMonitoringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_monitoring);

        // Create alert and set a responder (if both users are connected it's the next responder otherwise it's the person connected)
        // Alert checked button appears: no more monitoring until clicked
        // when the alert checked button appears, the monitoring resumes

    }
}
