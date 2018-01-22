package com.app.owl.sleepSession;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.app.owl.R;

public class SleepSessionsActivity extends AppCompatActivity {

    Button newSleepSessionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_session);

        // new sleep session button: open new sleep session activity
        newSleepSessionBtn = findViewById(R.id.new_sleep_btn);
        newSleepSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SleepSessionsActivity.this, NewSleepSessionActivity.class);
                startActivity(intent);
            }
        });

        // TODO: List of sleep session
    }
}
