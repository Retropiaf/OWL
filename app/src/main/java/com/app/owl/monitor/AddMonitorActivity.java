package com.app.owl.monitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.owl.R;
import com.google.firebase.database.DatabaseReference;

public class AddMonitorActivity extends AppCompatActivity {

    DatabaseReference databaseMonitor;
    PhoneMonitor phoneMonitor;
    String TAG = "AddMonitorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_monitor);

        phoneMonitor = new PhoneMonitor();


/*
        if(phoneMonitor.getIpAddress() != null){
            String id = databaseMonitor.push().getKey();


            // Add the sleep circle as a node
            databaseMonitor.child(id).setValue(phoneMonitor);

            Log.d(TAG, "registerAlert: phoneMonitor added! ");

        }else{
            Log.d(TAG, "registerAlert: failed to add phoneMonitor to the database ");
        }
        */
    }
}
