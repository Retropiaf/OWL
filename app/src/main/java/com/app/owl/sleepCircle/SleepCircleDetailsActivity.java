package com.app.owl.sleepCircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.app.owl.R;

public class SleepCircleDetailsActivity extends AppCompatActivity {

    TextView circleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circle_details);

       circleName = (TextView) findViewById(R.id.circle_details_name);

        Intent intent = getIntent();
        String name = intent.getStringExtra(SleepCirclesActivity.CIRCLE_NAME);
        String id = intent.getStringExtra(SleepCirclesActivity.CIRCLE_ID);
        circleName.setText(name);

    }
}
