package com.app.owl;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class WirelessTestActivity extends AppCompatActivity {
    private static final String TAG = "WirelessTestActivity";

    ProgressDialog mProgressDlg;
    private static final int REQUEST_ENABLE_BT=1;
    //TextView onBtn;
    TextView on_off;
    TextView listBtn;
    TextView findBtn, dk;
    BluetoothAdapter myBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    TextView text;
    TextView discover;
    ListView myListView;
    //DeviceListAdapter mAdapter;
    //ArrayList<BluetoothDevice> mDeviceList;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(myBluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_test);
        text = (TextView) findViewById(R.id.text);
        //onBtn = (TextView) findViewById(R.id.turn_on);
        //offBtn = (TextView) findViewById(R.id.turn_off);
        on_off = (TextView) findViewById(R.id.on_off);


        discover = (TextView) findViewById(R.id.discoverable);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!myBluetoothAdapter.isEnabled()){
            showDisabled();
        }else{
            showEnabled();
        }

        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDiscoverable();

            }
        });

        on_off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enable/disable");
                enableDisableBT();
            }
        });

        /*
        if(myBluetoothAdapter==null){
            showUnsupported();
        }else{
            if(myBluetoothAdapter.isEnabled()) {
                showEnabled();
            }else{
                showDisabled();
            }

            onBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
                }
            });
            offBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myBluetoothAdapter.disable();
                    showDisabled();
                    //Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
                }
            });
        }

        */
    } // End of OnCreate

    public void enableDisableBT(){
        if(myBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: no bluetooth capabilities.");
            showUnsupported();
        }
        if (!myBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            showEnabled();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

        }
        if(myBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            showDisabled();
            myBluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                showEnabled();
            }else{
                showDisabled();
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void showEnabled(){
        text.setText("Bluetooth is On");
        text.setTextColor(Color.BLUE);
        on_off.setText("Turn off");
        //offBtn.setEnabled(true);
        //onBtn.setEnabled(false);
    }

    private void showDisabled(){
        text.setText("Bluetooth is Off");
        text.setTextColor(Color.RED);
        on_off.setText("Turn on");
        //offBtn.setEnabled(false);
        //onBtn.setEnabled(true);
    }
    private void showUnsupported(){
        text.setText("Bluetooth is unsupported");
    }
    private void makeDiscoverable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

}