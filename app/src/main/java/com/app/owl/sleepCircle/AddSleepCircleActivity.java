package com.app.owl.sleepCircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.app.owl.OnGetDataListener;
import com.app.owl.OnGetIpListener;
import com.app.owl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class AddSleepCircleActivity extends AppCompatActivity {

    String TAG = "AddSleepCircleActivity";

    DatabaseReference database;
    String secondUserEmail, secondUserName, monitorName, secondUserUid, sleepCircleId;
    SleepCircle circle;
    EditText editSecondUser, editSecondUserName, editMonitorName, editCircleName;
    MainUser user2;
    CheckBox checkBox;
    String monitorIp, ip;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;
    ArrayList<SleepCircle> list;
    Boolean wifiOn;
    String circleName;
    Boolean userFound, userRegistered;
    Button buttonAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_circle);

        Log.d(TAG, "In onCreate");

        editCircleName = (EditText) findViewById(R.id.editCircleName);

        editSecondUser = (EditText) findViewById(R.id.second_user_email);

        editSecondUserName = (EditText) findViewById(R.id.second_user_username);

        editMonitorName = (EditText) findViewById(R.id.edit_monitor_name);
        editMonitorName.setVisibility(View.INVISIBLE);

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
                                    editMonitorName.setVisibility(View.VISIBLE);

                                }})
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    checkBox.setChecked(false);
                                    Toast.makeText(AddSleepCircleActivity.this,"You unselected \"Use this device as a monitor\"", Toast.LENGTH_SHORT).show();
                                }}).show();


                }else{
                    editMonitorName.setVisibility(View.INVISIBLE);
                    removeMonitor();
                }
            }
        });

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        list = new ArrayList<>();


        buttonAdd = (Button) findViewById(R.id.confirm_create_circle);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAdd.setClickable(false);
                buttonAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                createCircle();
            }

        });

    }

    private void removeMonitor(){
        monitorIp = null;

    }

    private void createCircle(){
        Log.d(TAG, "In createCircle");
        database = FirebaseDatabase.getInstance().getReference();

        circleName = editCircleName.getText().toString().trim();
        secondUserEmail = editSecondUser.getText().toString().trim().toLowerCase();
        secondUserName = editSecondUserName.getText().toString().trim().toLowerCase();
        monitorName = editMonitorName.getText().toString().trim().toLowerCase();


        if(TextUtils.isEmpty(circleName) && TextUtils.isEmpty(secondUserEmail) && TextUtils.isEmpty(secondUserName)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to fill all the fields", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(circleName)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter a circle name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(secondUserEmail)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter an email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(secondUserName)){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter a username", Toast.LENGTH_SHORT).show();
        }else if(editMonitorName.getVisibility() == View.VISIBLE && TextUtils.isEmpty(monitorName) && checkBox.isChecked()){
            Toast.makeText(AddSleepCircleActivity.this,"You need to enter a monitor name", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "got here 0");

            userFound = false;


            database.child("MainUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "got here 0a");

                        user2 = snapshot.getValue(MainUser.class);

                        if(user2.getUserEmail() != null && user2.getUserName() != null) {
                            userFound = true;

                            String user_email = user2.getUserEmail().toLowerCase();
                            String user_name = user2.getUserName().toLowerCase();
                            Log.d(TAG, "got here 0b");

                            Log.d(TAG, secondUserEmail);
                            Log.d(TAG, user_email);
                            Log.d(TAG, user_name);

                            if(secondUserEmail.toLowerCase().equals(user_email) && secondUserName.toLowerCase().equals(user_name)){
                                Log.d(TAG, "found a user");

                                secondUserUid = user2.getUserUid();

                                Log.d(TAG, "secondUserUid: " + secondUserUid);
                                if (user2.getIsRegistered()) {

                                    Log.d(TAG, "monitorIp inside createCircle = " + monitorIp);

                                    circle = new SleepCircle(circleName, secondUserUid, secondUserName, monitorIp, monitorName);

                                    // Todo: greyout button and show loading wheel


                                    if(checkBox.isChecked()){
                                        Log.d(TAG, "Inside createCircle: CheckBox is checked");
                                        Log.d(TAG, "monitorIp inside createCircle = " + monitorIp);

                                        Log.d(TAG, "Inside createCircle: CheckBox is not checked");
                                        makeMonitor(new OnGetIpListener() {
                                            @Override
                                            public void onSuccess() {
                                                circle = new SleepCircle(circleName, secondUserUid, secondUserName, monitorIp, monitorName);
                                                addCircleToDatabase(circle);
                                                addCircleToUsers();
                                                returnToSleepCirclesPage();
                                            }

                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onFailure() {
                                                checkBox.setChecked(false);
                                                editMonitorName.setVisibility(View.INVISIBLE);
                                                buttonAdd.setClickable(true);
                                                buttonAdd.getBackground().clearColorFilter();
                                            }
                                        });

                                    }else{
                                        circle = new SleepCircle(circleName, secondUserUid, secondUserName, monitorIp, monitorName);
                                        addCircleToDatabase(circle);
                                        addCircleToUsers();
                                        returnToSleepCirclesPage();
                                    }


                                }else{
                                    Toast.makeText(AddSleepCircleActivity.this,"User \"" + secondUserName + "\" has not comfirmed their email yet. \"", Toast.LENGTH_SHORT).show();
                                    buttonAdd.setClickable(true);
                                    buttonAdd.getBackground().clearColorFilter();

                                }
                            }
                        } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
                    }

                    if(!userFound) {
                        Log.d(TAG, "NO USER FOUND");
                        //TODO: Fix no user found essage that appears even when user is found
                        Toast.makeText(AddSleepCircleActivity.this, "No user with the email \"" + secondUserEmail + "\" and the username \"" + secondUserName + "\"", Toast.LENGTH_SHORT).show();
                        buttonAdd.setClickable(true);
                        buttonAdd.getBackground().clearColorFilter();
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
        /*
        Log.d(TAG, "addCircleToDatabase");

        database = FirebaseDatabase.getInstance().getReference("SleepCircles");

        sleepCircleId =  database.push().getKey();

        circle.setCircleId(sleepCircleId);


        database.child(circleName).setValue(circle);

        Toast.makeText(AddSleepCircleActivity.this, "Adding sleep circle to database", Toast.LENGTH_SHORT).show();
        addCircleToUsers();
        */


    }

    private void addCircleToUsers(){
        Log.d(TAG, "addCircleToUsers");

        updateUserCircleList(CurrentUser.uid);
        updateUserCircleList(user2.getUserUid());
        returnToSleepCirclesPage();

        //TODO: Check if user is logged in (add a method to current user) and handle logged out user

    }

    private void updateUserCircleList(String userKey){
        Log.d(TAG, "In updateUserCircleList");

        database = FirebaseDatabase.getInstance().getReference().child("MainUsers");

        Log.d(TAG, String.valueOf(database));

        database.child(userKey).child("circles").child(circleName).setValue(circle);

    }

    private void returnToSleepCirclesPage(){
        Log.d(TAG, "In returnToSleepCirclesPage");

        Toast.makeText(AddSleepCircleActivity.this, circleName + " was added to your Sleep Circles", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddSleepCircleActivity.this, SleepCirclesActivity.class);
        startActivity(intent);
    }



    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }
    public void makeMonitor(final OnGetIpListener listener) {
        listener.onStart();

        if(!myWifi.isConnected()){
            Log.d(TAG, "In makeMonitor: wifi is not connected");
            //wifiOn = false;
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

                                try {
                                    InetAddress inetAddress = InetAddress.getLocalHost();
                                    monitorIp = String.valueOf(inetAddress);
                                    listener.onSuccess();

                                } catch (UnknownHostException e) {
                                    Toast.makeText(AddSleepCircleActivity.this,"I'm sorry. I don't know my own ip address.", Toast.LENGTH_SHORT).show();
                                    listener.onFailure();
                                }

                                wifiManager.setWifiEnabled(false);


                            }else{
                                Toast.makeText(AddSleepCircleActivity.this,"This device is too old to be used as a monitor.", Toast.LENGTH_SHORT).show();
                                listener.onFailure();
                            }


                        }})
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkBox.setChecked(false);
                            editMonitorName.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddSleepCircleActivity.this,"You unselected \"Use this device as a monitor\"", Toast.LENGTH_SHORT).show();
                        }}).show();
        }else{

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    monitorIp = String.valueOf(inetAddress);
                    listener.onSuccess();

                } catch (UnknownHostException e) {
                    Toast.makeText(AddSleepCircleActivity.this,"I'm sorry. I don't know my own ip address.", Toast.LENGTH_SHORT).show();
                    listener.onFailure();
                }

            }else{
                Toast.makeText(AddSleepCircleActivity.this,"This device is too old to be used as a monitor.", Toast.LENGTH_SHORT).show();
                listener.onFailure();
            }



        }





    }
}
