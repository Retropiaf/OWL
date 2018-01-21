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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.owl.R;
import com.app.owl.monitor.PhoneMonitor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SleepCircleDetailsActivity extends AppCompatActivity {

    String TAG = "SleepCircleDetailsActivity";
    TextView circleName;
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
    Button make_monitor;
    Intent intent;
    String name;

    public static final String CIRCLE_NAME = "circle name";
    public static final String CIRCLE_ID = "circle id";
    public static final String USER_1 = "user 1";
    public static final String USER_2 = "user 2";
    public static final String MONITOR = "monitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_circle_details);

        circleName = (TextView) findViewById(R.id.circle_details_name);
        user_1_view = (TextView) findViewById(R.id.circle_details_user1);
        user_2_view = (TextView) findViewById(R.id.circle_details_user2);
        monitor_view = (TextView) findViewById(R.id.circle_details_monitor);
        make_monitor = findViewById(R.id.circle_details_make_monitor);
        make_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMonitor();
            }

        });

        intent = getIntent();
        name = intent.getStringExtra(SleepCirclesActivity.CIRCLE_NAME);
        circle_id = intent.getStringExtra(SleepCirclesActivity.CIRCLE_ID);
        user1 = intent.getStringExtra(SleepCirclesActivity.USER_1);
        user2 = intent.getStringExtra(SleepCirclesActivity.USER_2);
        monitor = intent.getStringExtra(SleepCirclesActivity.MONITOR);


        circleName.setText(name);
        user_1_view.setText(user1);
        user_2_view.setText(user2);


        if(monitor == null){
            monitor_view.setText("No monitor registered yet.");
        }else{
            make_monitor.setVisibility(View.INVISIBLE);
            monitor_view.setText(monitor);

        }

    }



    private void makeMonitor(){
        Log.d(TAG, "In cmakeMonitor");
        wifiOn = true;
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
                                database.updateChildren(childUpdates);
                                finish();

                                overridePendingTransition(0, 0);
                                Intent intent = new Intent(getApplicationContext(), SleepCircleDetailsActivity.class);
                                intent.putExtra(CIRCLE_NAME, name);
                                intent.putExtra(CIRCLE_ID, circle_id);
                                intent.putExtra(USER_1, user1);
                                intent.putExtra(USER_2, user2);
                                intent.putExtra(MONITOR, monitorIp);

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



    }
}
