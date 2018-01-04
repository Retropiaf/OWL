package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class UserMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Button newCircle = (Button) findViewById(R.id.set_circle);

        newCircle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {   Intent signUpIntent = new Intent(UserMainActivity.this, setNewCircleActivity.class);
                startActivity(signUpIntent);
            }

        });
    }
}
