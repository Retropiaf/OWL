package com.app.owl.Sleepcircle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.owl.R;

public class AddSleepCircleActivity extends AppCompatActivity {

    SleepCircle sleepCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_circle);

        sleepCircle = new SleepCircle();
    }
}
