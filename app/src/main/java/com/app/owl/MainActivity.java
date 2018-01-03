package com.app.owl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TimerTask tt;
    int counter = 0;

    //startTime and offsetTime must be long and not float.
    long startTime = 0;
    long offsetTime = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button button = findViewById(R.id.sign_up_btn);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               // Intent signUpIntent = new Intent(MainActivity.this, LoginActivity.class);
                // startActivity(signUpIntent);
            }

        });



    }






}
