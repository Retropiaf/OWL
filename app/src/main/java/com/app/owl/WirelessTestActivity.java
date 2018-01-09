package com.app.owl;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class WirelessTestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "WirelessTestActivity";

    ProgressDialog mProgressDlg;
    private static final int REQUEST_ENABLE_BT=1;
    TextView on_off;
    BluetoothA2dp mBluetoothEarbuds;
    BluetoothAdapter myBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    TextView text;
    TextView discoverable;
    TextView discover;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    private BluetoothProfile mA2DPSinkProxy;

    ListView lvNewDevices;


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

    /**
     * Broadcast Receiver for listing devices that are not yet paired.
     * Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private BroadcastReceiver  mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastRceiver: BOND_BONDED.");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastRceiver: BOND_BONDING.");

                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastRceiver: BOND_NONE.");

                }
            }
        }
    };




    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //myBluetoothAdapter.closeProfileProxy(mBluetoothEarbuds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_test);
        Log.d(TAG, "in onCreate!");

        text = (TextView) findViewById(R.id.text);
        on_off = (TextView) findViewById(R.id.on_off);
        discoverable = (TextView) findViewById(R.id.discoverable);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        discover = (TextView) findViewById(R.id.discover);
        mBTDevices = new ArrayList<>();

        //Broadcasts when bond state changes (pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(WirelessTestActivity.this);

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


        discoverable.setOnClickListener(new View.OnClickListener() {
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

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: btnDiscover");
                btnDiscover();
            }
        });


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

    public void btnDiscover(){
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Cancelling discovery.");

            checkBTPermission();

            myBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);


        }
        if (!myBluetoothAdapter.isDiscovering()) {
            checkBTPermission();

            myBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

            // Establish connection to the proxy.
            myBluetoothAdapter.getProfileProxy(WirelessTestActivity.this, mProfileListener, BluetoothProfile.A2DP);
        }
    }



    /**
     * *This method is required for all devices running API23+
     */
    private void checkBTPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.Access_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.Access_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }else{
                Log.d(TAG, "No need to check permission: SDK version < LOLLIPOP");
            }


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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        myBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You clicked on a device.");

        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();
        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        // Establish connection to the proxy.
        myBluetoothAdapter.getProfileProxy(WirelessTestActivity.this, mProfileListener, BluetoothProfile.A2DP);


        //Create the bond
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

        }
    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d(TAG, "profile == BluetoothProfile.A2DP - Connecting");
                mBluetoothEarbuds = (BluetoothA2dp) proxy;
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d(TAG, "profile == BluetoothProfile.A2DP - Disconnecting");
                mBluetoothEarbuds = null;
            }
        }
    };


}