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
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class SleepCircleDetailsActivity extends AppCompatActivity {

    String TAG = "SleepCircleDetailsActivity";
    TextView circleName, editMonitorName;
    TextView user_1_view;
    TextView user_2_view;
    TextView monitor_view;
    ConnectivityManager connManager;
    NetworkInfo myWifi;
    WifiManager wifiManager;
    Boolean wifiOn;
    String monitorIp;
    String user1;
    String user2;
    String circle_id;
    String monitor;
    String monitorName;
    Button make_monitor2, make_monitor1;
    Button start_sleep_session;
    Intent intent;
    String name;
    String userName1;
    String userName2;


    public static final String CIRCLE_NAME = "circle name";
    public static final String CIRCLE_ID = "circle id";
    public static final String USER_1 = "user 1";
    public static final String USER_2 = "user 2";
    public static final String MONITOR = "monitor";
    public static final String USERNAME_1 = "username 1";
    public static final String USERNAME_2 = "username 2";
    public static final String MONITOR_NAME = "monitor name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circle_details);

        circleName = (TextView) findViewById(R.id.circle_details_name);
        //user_1_view = (TextView) findViewById(R.id.circle_details_user1);
        user_2_view = (TextView) findViewById(R.id.circle_details_user2);
        monitor_view = (TextView) findViewById(R.id.circle_details_monitor);
        editMonitorName = (EditText) findViewById(R.id.edit_monitor_name);
        editMonitorName.setVisibility(View.INVISIBLE);
        make_monitor1 = findViewById(R.id.circle_details_make_monitor1);
        make_monitor1.setVisibility(View.INVISIBLE);
        make_monitor2 = findViewById(R.id.circle_details_make_monitor2);
        make_monitor2.setVisibility(View.INVISIBLE);
        start_sleep_session = findViewById(R.id.start_sleep_session);
        make_monitor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMonitor1();
            }

        });
        make_monitor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMonitor2();
            }

        });

        intent = getIntent();
        name = intent.getStringExtra(SleepCirclesActivity.CIRCLE_NAME);
        circle_id = intent.getStringExtra(SleepCirclesActivity.CIRCLE_ID);
        user1 = intent.getStringExtra(SleepCirclesActivity.USER_1);
        user2 = intent.getStringExtra(SleepCirclesActivity.USER_2);
        //userName1 = intent.getStringExtra(SleepCirclesActivity.USERNAME_1);
        Log.d(TAG, "USERNAME " + userName1);
        userName2 = intent.getStringExtra(SleepCirclesActivity.USERNAME_2);
        monitorIp = intent.getStringExtra(SleepCirclesActivity.MONITOR);
        monitorName = intent.getStringExtra(SleepCirclesActivity.MONITOR_NAME);



        /*

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("MainUsers");
        database.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    MainUser mainUser = snapshot.getValue(MainUser.class);
                    if(mainUser.getUid() == uid){
                        userName1 = mainUser.getUserName();
                    }else{
                        //TODO: handle no user
                    }

                } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: Handle database error
            }
        });


*/




        if(monitorIp == null){
            monitor_view.setText("No monitor registered yet.");
            make_monitor1.setVisibility(View.VISIBLE);
            make_monitor2.setVisibility(View.INVISIBLE);
            start_sleep_session.setVisibility(View.INVISIBLE);
        }else{
            make_monitor1.setVisibility(View.INVISIBLE);
            make_monitor2.setVisibility(View.INVISIBLE);
            monitor_view.setText(monitorName);
            start_sleep_session.setVisibility(View.VISIBLE);
        }

        //user_1_view.setText(userName1);
        circleName.setText(name);
        user_2_view.setText(userName2);



    }


    private void makeMonitor1(){
        editMonitorName.setVisibility(View.VISIBLE);
        make_monitor1.setVisibility(View.INVISIBLE);
        make_monitor2.setVisibility(View.VISIBLE);
    }

    private void makeMonitor2(){
        Log.d(TAG, "In cmakeMonitor");
        monitorName = editMonitorName.getText().toString().trim();
        if(!TextUtils.isEmpty(monitorName)){

            wifiOn = true;
            wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("MainUsers");
            database.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        MainUser mainUser = snapshot.getValue(MainUser.class);
                        if(mainUser.getUid() == CurrentUser.uid){
                            userName1 = mainUser.getUserName();
                        }

                    } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO: Handle database error
                }
            });

            if(!myWifi.isConnected()){
                wifiOn = false;
                new AlertDialog.Builder(SleepCircleDetailsActivity.this)
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

                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/MainUsers/" + user1 + "/circles/" + circle_id + "/monitorIp/", monitorIp);
                                    childUpdates.put("/MainUsers/" + user2 + "/circles/" + circle_id + "/monitorIp/", monitorIp);
                                    childUpdates.put("/SleepCircles/" + circle_id + "/monitorIp/", monitorIp);

                                    childUpdates.put("/MainUsers/" + user1 + "/circles/" + circle_id + "/monitorName/", monitorName);
                                    childUpdates.put("/MainUsers/" + user2 + "/circles/" + circle_id + "/monitorName/", monitorName);
                                    childUpdates.put("/SleepCircles/" + circle_id + "/monitorName/", monitorName);

                                    database.updateChildren(childUpdates);
                                    finish();

                                    overridePendingTransition(0, 0);
                                    Intent intent = new Intent(getApplicationContext(), SleepCircleDetailsActivity.class);
                                    intent.putExtra(CIRCLE_NAME, name);
                                    intent.putExtra(CIRCLE_ID, circle_id);
                                    intent.putExtra(USER_1, user1);
                                    intent.putExtra(USER_2, user2);
                                    intent.putExtra(MONITOR, monitorIp);
                                    intent.putExtra(USERNAME_1, userName1);
                                    intent.putExtra(USERNAME_2, userName2);
                                    intent.putExtra(MONITOR_NAME, monitorName);

                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    Log.d(TAG, "Reloaded");

                                }
                            }})
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SleepCircleDetailsActivity.this,"You cancelled \"Use this device as a monitor\"", Toast.LENGTH_SHORT).show();
                            }}).show();
            }



        }else{
            Toast.makeText(SleepCircleDetailsActivity.this,"Please enter a name for your device", Toast.LENGTH_SHORT).show();
        }


    }
}
