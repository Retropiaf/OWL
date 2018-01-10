package com.app.owl;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class SetSleepSessionActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {
    private static final String TAG = "SetSleepSessionActivity";

    //ProgressDialog mProgressDlg;

    // private static final int REQUEST_ENABLE_BT=1;
    BluetoothA2dp mBluetoothEarbuds;
    //Set<BluetoothDevice> pairedDevices;
    TextView chooseDevice;
    TextView discoverBtn;
    String clickedDevice = null;

    BluetoothAdapter mBluetoothAdapter;

    AudioManager mAudioManager;
    MediaPlayer mPlayer;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    //private BluetoothProfile mA2DPSinkProxy;

    ListView lvNewDevices;

    public static final ParcelUuid AudioSink =
            ParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AudioSource =
            ParcelUuid.fromString("0000110A-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AdvAudioDist =
            ParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");


    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                Log.d(TAG, "BroadcastReceiver: ACTION_FOUND.");

                addCompatibleDevice(device);

            } // End of ACTION_FOUND

        }
    };




    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

            Log.d(TAG, "mBroadcastReceiver4");

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "mBroadcastReceiver4 - onReceive: BOND_BONDED.");

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mBroadcastReceiver1");

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

                Toast toast = Toast.makeText(SetSleepSessionActivity.this, "Search over. " + String.valueOf(mBTDevices.size()) + " device(s) found.", Toast.LENGTH_LONG);
                toast.show();

                Log.d(TAG, "mBroadcastReceiver1 - ACTION_DISCOVERY_FINISHED");

                mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sleep_session);

        chooseDevice = (TextView) findViewById(R.id.chooseDevice); // Text indication

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices); // Device list

        discoverBtn = (TextView) findViewById(R.id.discover); // Button

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices();
            }
        });

        mBTDevices = new ArrayList<>(); // Create an empty list of bluetooth devices

        lvNewDevices.setOnItemClickListener(SetSleepSessionActivity.this); // Set an onItemClick listener for elements of the bluetooth device list

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Get the default adapter

        // Check if the user's device has bluetooth
        if(mBluetoothAdapter == null){
            Log.d(TAG, "No bluetooth functionality");
            Toast toast = Toast.makeText(SetSleepSessionActivity.this, "This device doesn't support bluetooth connections", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

    } // End of OnCreate

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();

        // Close proxy connection after use.
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothEarbuds);
        mBluetoothAdapter.disable();
        if (mBroadcastReceiver4 != null) {unregisterReceiver(mBroadcastReceiver3);}
        //if (mBroadcastReceiver4 != null) {unregisterReceiver(mBroadcastReceiver4);}
        mBluetoothAdapter.cancelDiscovery();
    } // End of onDestroy

    public void discoverDevices(){
        if (!mBluetoothAdapter.isEnabled()){mBluetoothAdapter.enable();}
            Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (mBluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "been called 2");
            mBluetoothAdapter.cancelDiscovery();
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();

        }
        if (!mBluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "been called 3");
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();

        }

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice mBluetoothDevice : bondedDevices){
            Log.d(TAG, "found a bounded device");
            addCompatibleDevice(mBluetoothDevice);

        }

        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        discoverDevicesIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);


        IntentFilter endDiscoveryIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver1, endDiscoveryIntent);
    } // End of discoverDevices

    private void checkBTPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.Access_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.Access_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothEarbuds);
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        clickedDevice = deviceName;
        String deviceAddress = mBTDevices.get(i).getAddress();
        Log.d(TAG, "onItemClick: deviceName = " + deviceName + " // deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);

            mBTDevices.get(i).createBond();

            // Establish connection to the proxy.
            mBluetoothAdapter.getProfileProxy(SetSleepSessionActivity.this, mProfileListener, BluetoothProfile.A2DP);

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver4, discoverDevicesIntent);
        }

    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {

                mBluetoothEarbuds = (BluetoothA2dp) proxy;

            }else{
                Log.d(TAG, "The device chosen is not bluetooth earbuds or headphones");
                Toast toast = Toast.makeText(SetSleepSessionActivity.this, "Please connect to bluetooth earbuds or headphones", Toast.LENGTH_LONG);
                toast.show();
                // Close proxy connection after use.
                mBluetoothAdapter.closeProfileProxy(profile, proxy);
                discoverDevices();
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d(TAG, "profile == BluetoothProfile.A2DP - Disconnecting");
                mBluetoothEarbuds = null;
            }
        }
    };

    public static boolean isAudioSource(ParcelUuid uuid) {
        return uuid.equals(AudioSource);
    }

    public static boolean isAudioSink(ParcelUuid uuid) {
        return uuid.equals(AudioSink);
    }

    public static boolean isAdvAudioDist(ParcelUuid uuid) {
        return uuid.equals(AdvAudioDist);
    }



    public static boolean inArray(ParcelUuid[] uuids) {
        for (ParcelUuid x : uuids){
            if (isAudioSink(x)||isAdvAudioDist(x)||isAudioSource(x)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInList(ArrayList<BluetoothDevice> mBTDevices, BluetoothDevice device) {
        for (BluetoothDevice x : mBTDevices){
            if (x.getAddress() == device.getAddress() || x == device || x.equals(device)) {
                return true;
            }
        }
        return false;
    }

    private void addCompatibleDevice(BluetoothDevice device) {

        String deviceName = setName(device);
        String deviceAddress = setAddress(device);

        device.fetchUuidsWithSdp();
        ParcelUuid[] uuids = device.getUuids();

        if(uuids != null && uuids.length != 0 && inArray(uuids)){
            // Check if the device has already been found and added to the list
            if(!isInList(mBTDevices, device)){
                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
                Log.d(TAG, "Added a new device to mBTDevices: " + deviceName + " - " + deviceAddress);
            }else{
                Log.d(TAG, "Device" + deviceName + " " + deviceAddress + " has already been found and added to the mBTDevices list.");
            }
        }else{
            Log.d(TAG, "This device does not support A2DP (is not a pair of earbuds, headphones or a headset). Device:" + deviceName);
        }
    }

    private String setName(BluetoothDevice device) {
        // Set anonymous name or address if the device name or address is null
        String deviceName;

        if(device.getName() == null) {
            deviceName = "anonymous device";
        }else{
            deviceName = device.getName();
        }

        return deviceName;
    }

    private String setAddress(BluetoothDevice device) {
        // Set anonymous name or address if the device name or address is null
        String deviceAddress;

        if(device.getAddress() == null) {
            deviceAddress = "anonymous address";
        }else{
            deviceAddress = device.getAddress();
        }

        return deviceAddress;
    }




}