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
import android.widget.CompoundButton;
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

import java.util.ArrayList;

public class AddSleepCircleActivity extends AppCompatActivity {

    String TAG = "AddSleepCircleActivity";

    DatabaseReference database;
    String secondUserEmail;
    String secondUserUid;
    SleepCircle circle;
    //String circleName;
    EditText editSecondUser;
    EditText editCircleName;
    MainUser user2;
    String sleepCircleId;
    CheckBox checkBox;
    String monitorIp;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;
    ArrayList<SleepCircle> list;
    Boolean wifiOn;
    String circleName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_circle);

        Log.d(TAG, "In onCreate");

        editCircleName = (EditText) findViewById(R.id.editCircleName);

        editSecondUser = (EditText) findViewById(R.id.second_user_email);

        checkBox = findViewById(R.id.make_monitor);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()){
                    new AlertDialog.Builder(AddSleepCircleActivity.this)
                            .setTitle("Confirm")
                            .setMessage("You can not use this device as a monitor and pair it to your earbuds. Are you sure you want to use this device as a monitor?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }})
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    checkBox.setChecked(false);
                                    Toast.makeText(AddSleepCircleActivity.this,"You unselected \"Use this device as a monitor\"", Toast.LENGTH_SHORT).show();
                                }}).show();


                }
            }
        });

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        list = new ArrayList<>();


        Button buttonAdd = (Button) findViewById(R.id.confirm_create_circle);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCircle();
            }

        });

    }


    private void makeMonitor(){
        Log.d(TAG, "In cmakeMonitor");
        wifiOn = true;

        if(!myWifi.isConnected()){
            wifiOn = false;
            new AlertDialog.Builder(AddSleepCircleActivity.this)
                    .setTitle("Confirm")
                    .setMessage("Your device need to be connected to wifi.")
                    .setPositiveButton("Turn wifi on", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            wifiManager.setWifiEnabled(true);
                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                            if (SDK_INT > 8)
                            {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                PhoneMonitor phoneMonitor = new PhoneMonitor();
                                monitorIp = String.valueOf(phoneMonitor.deviceIP);
                                if(!wifiOn){ wifiManager.setWifiEnabled(false); }

                            }
                        }})
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkBox.setChecked(false);
                            Toast.makeText(AddSleepCircleActivity.this,"You unselected \"Use this device as a monitor\"", Toast.LENGTH_SHORT).show();
                        }}).show();
        }



    }

    private void removeMonitor(){
        monitorIp = null;

    }

    private void createCircle(){
        Log.d(TAG, "In createCircle");
        database = FirebaseDatabase.getInstance().getReference();
        circleName = editCircleName.getText().toString().trim();

        secondUserEmail = editSecondUser.getText().toString().trim().toLowerCase();

        if(TextUtils.isEmpty(circleName) && TextUtils.isEmpty(secondUserEmail)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter a circle name and a second user", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(circleName)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter a circle name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(secondUserEmail)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter an email", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "got here 0");
            database.child("MainUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "got here 0a");

                        user2 = snapshot.getValue(MainUser.class);

                        if(user2.getUserEmail() != null) {
                            String user_email = user2.getUserEmail().toLowerCase();
                            Log.d(TAG, "got here 0b");

                            Log.d(TAG, secondUserEmail);
                            Log.d(TAG, user_email);

                            if(secondUserEmail.equals(user_email)){
                                Log.d(TAG, "got here 1");
                                secondUserUid = user2.getUserUid();
                                Log.d(TAG, "got here 2");

                                Log.d(TAG, "secondUserUid: " + secondUserUid);

                                circle = new SleepCircle(circleName, secondUserUid, monitorIp);

                                if(checkBox.isChecked()){ makeMonitor(); }

                                addCircleToDatabase(circle);
                                addCircleToUsers();
                                returnToSleepCirclesPage();

                            }else{
                                Toast.makeText(AddSleepCircleActivity.this,"No user with the email \"" + secondUserEmail + "\"", Toast.LENGTH_SHORT).show();
                                editSecondUser.setText("");
                                // add username verification

                            }
                        } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2


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
        Log.d(TAG, "addCircleToDatabase");

        database = FirebaseDatabase.getInstance().getReference("SleepCircles");

        sleepCircleId =  database.push().getKey();

        circle.setCircleId(sleepCircleId);

        database.child(sleepCircleId).setValue(circle);

        Toast.makeText(AddSleepCircleActivity.this, "Adding sleep circle to database", Toast.LENGTH_SHORT).show();

    }

    private void addCircleToUsers(){
        Log.d(TAG, "addCircleToUsers");

        updateUserCircleList(CurrentUser.uid);
        updateUserCircleList(user2.getUserUid());

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

        Toast.makeText(AddSleepCircleActivity.this, circleName + " was added to your Sleep Circles", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddSleepCircleActivity.this, SleepCirclesActivity.class);
        startActivity(intent);
    }
}
