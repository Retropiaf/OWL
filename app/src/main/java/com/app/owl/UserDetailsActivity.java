package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class UserDetailsActivity extends AppCompatActivity {

    TextView userName;
    DatabaseReference databaseReference; // Use if need to do something with the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        TextView userName = (TextView) findViewById(R.id.dynamic_user_name);

        Intent intent = getIntent();
        String name = intent.getStringExtra(UsersActivity.USERNAME);
        String id = intent.getStringExtra(UsersActivity.USER_ID);
        userName.setText(name);
    }
}
