package com.app.owl.sleepCircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.app.owl.CurrentUser;
import com.app.owl.MainUser;
import com.app.owl.R;
import com.app.owl.monitor.PhoneMonitor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddSleepCircleActivity extends AppCompatActivity {

    String TAG = "AddSleepCircleActivity";

    DatabaseReference database;
    String secondUserEmail;
    String secondUserId;
    SleepCircle circle;
    String circleName;
    EditText editSecondUser;
    EditText editCircleName;
    MainUser user2;
    String sleepCircleId;
    CheckBox checkBox;
    String monitorIp;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_circle);

        Log.d(TAG, "In onCreate");

        editCircleName = (EditText) findViewById(R.id.editCircleName);

        editSecondUser = (EditText) findViewById(R.id.second_user_email);

        checkBox = findViewById(R.id.make_monitor);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        Button buttonAdd = (Button) findViewById(R.id.confirm_create_circle);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    new AlertDialog.Builder(AddSleepCircleActivity.this)
                            .setTitle("Confirm")
                            .setMessage("You can not use a device both as a monitor and pair it to your earbuds. Are you sure you want to use this device as a monitor?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!myWifi.isConnected()){
                                        new AlertDialog.Builder(AddSleepCircleActivity.this)
                                                .setTitle("Confirm")
                                                .setMessage("Your device need to be connected to wifi.")
                                                .setPositiveButton("Turn wifi on", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        wifiManager.setWifiEnabled(true);
                                                        createCircle();
                                                        wifiManager.setWifiEnabled(false);
                                                    }})
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }}).show();


                                    }
                                    createCircle();
                                }})
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }}).show();


                }
            }

        });

    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
    }

    private void makeMonitor(){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            PhoneMonitor phoneMonitor = new PhoneMonitor();
            monitorIp = String.valueOf(phoneMonitor.deviceIP);

        }


    }
    private void removeMonitor(){
        monitorIp = null;

    }

    private void createCircle(){

        database = FirebaseDatabase.getInstance().getReference();
        circleName = editCircleName.getText().toString().trim();
        secondUserEmail = editSecondUser.getText().toString().trim().toLowerCase();

        if(TextUtils.isEmpty(circleName)){
            //Toast.makeText(AddSleepCircleActivity.this,"You need to enter a circle name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(secondUserEmail)){
            //Toast.makeText(AddSleepCircleActivity.this,"You need to enter an email", Toast.LENGTH_SHORT).show();
        }else{
            database.child("MainUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        user2 = snapshot.getValue(MainUser.class);
                        Log.d(TAG, "user2 = " + user2);
                        if(user2.getUserEmail() != null) {
                            String user_email = user2.getUserEmail().toLowerCase();


                            Log.d(TAG, "user is: " + String.valueOf(user2));

                            if(secondUserEmail.equals(user_email)){
                                Log.d(TAG, "Found second user");
                                secondUserId = user2.getUserId();

                                Log.d(TAG, "Create new Circle");

                                if (checkBox.isChecked()) {
                                    makeMonitor();
                                }else{
                                    removeMonitor();
                                }
                                circle = new SleepCircle(circleName, secondUserId, monitorIp);
                                addCircleToDatabase(circle);
                                addCircleToUsers();
                                returnToSleepCirclesPage();


                            }
                        }


                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO: Handle database error
                }

            });
        }

    }

    private void addCircleToDatabase(SleepCircle circle) {
        Log.d(TAG, "In addCircleToDatabase");

       database = FirebaseDatabase.getInstance().getReference("SleepCircles");

       sleepCircleId =  database.push().getKey();

       circle.setCircleId(sleepCircleId);

       database.child(sleepCircleId).setValue(circle);

       Toast.makeText(AddSleepCircleActivity.this, "Adding sleep circle to database", Toast.LENGTH_SHORT).show();

    }

    private void addCircleToUsers(){

        Log.d(TAG, "In addCircleToUsers");

        CurrentUser.findId();
        String currentUserId = CurrentUser.id;

        updateUserCircleList(currentUserId);
        updateUserCircleList(user2.getUserId());

        //TODO: Check if user is logged in (add a method to current user) and handle logged out user


    }

    private void updateUserCircleList(String userKey){
        Log.d(TAG, "In updateUserCircleList");

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers");

        Log.d(TAG, String.valueOf(database));

        database.child(userKey).child("circles").child(sleepCircleId).setValue(circle);

    }

    private void returnToSleepCirclesPage(){
        Log.d(TAG, "In returnToSleepCirclesPage");

        Toast.makeText(AddSleepCircleActivity.this, "Adding sleep circle to database", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddSleepCircleActivity.this, SleepCirclesActivity.class);
        startActivity(intent);
    }
}
