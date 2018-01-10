package com.app.owl;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    BluetoothAdapter mBluetoothAdapter;

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

    /*
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }

        }
    };
    */

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                Log.d(TAG, "mBroadcastReceiver3 - onReceive: ACTION FOUND.");

                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                device.fetchUuidsWithSdp();

                ParcelUuid[] uuids = device.getUuids();

                if(uuids != null && uuids.length != 0 && inArray(uuids) == true){
                    if(isInList(mBTDevices, device) == false){
                        mBTDevices.add(device);
                        mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                        lvNewDevices.setAdapter(mDeviceListAdapter);
                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());


                    }else{
                        Log.d(TAG, "Device" + device.getName() + " " + device.getAddress() + " is alredy in the list.");
                    }



                }else{
                    Log.d(TAG, "This device does not support A2DP " + device.getName());
                }



            }

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    device.fetchUuidsWithSdp();

                    ParcelUuid[] uuids = device.getUuids();

                    if(uuids != null && uuids.length != 0 && inArray(uuids) == true){
                        if(isInList(mBTDevices, device) == false){
                            mBTDevices.add(device);
                            mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                            lvNewDevices.setAdapter(mDeviceListAdapter);

                            Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());

                        }else{
                            Log.d(TAG, "Device" + device.getName() + " " + device.getAddress() + " is alredy in the list.");
                        }



                    }else{
                        Log.d(TAG, "This device does not support A2DP " + device.getName());
                    }
                }
                //case2: creating a bone
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond

            }
            /*
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d(TAG, "mBroadcastReceiver3 - onReceive: ACTION_ACL_CONNECTED.");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                device.fetchUuidsWithSdp();

                ParcelUuid[] uuids = device.getUuids();

                if(uuids != null && uuids.length != 0 && inArray(uuids) == true){
                    if(isInList(mBTDevices, device) == false){
                        mBTDevices.add(device);
                        mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
                        lvNewDevices.setAdapter(mDeviceListAdapter);
                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());

                    }else{
                        Log.d(TAG, "Device" + device.getName() + " " + device.getAddress() + " is alredy in the list.");
                    }



                }else{
                    Log.d(TAG, "This device does not support A2DP");
                }
            }
            */
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mBroadcastReceiver4");

            if (action.equals(BluetoothDevice.BOND_BONDED)){
                Log.d(TAG, "mBroadcastReceiver4 - onReceive: BOND_BONDED.");

                Log.d(TAG, "Establishing connection to the proxy.");

                // Establish connection to the proxy.
                mBluetoothAdapter.getProfileProxy(SetSleepSessionActivity.this, mProfileListener, BluetoothProfile.A2DP);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mBroadcastReceiver&");

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

        Log.d(TAG, "in onCreate!");

        chooseDevice = (TextView) findViewById(R.id.chooseDevice);

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);

        discoverBtn = (TextView) findViewById(R.id.discover);

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices();
            }
        });

        mBTDevices = new ArrayList<>();

        //Broadcasts when bond state changes (ie:pairing)
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //registerReceiver(mBroadcastReceiver2, filter);

        lvNewDevices.setOnItemClickListener(SetSleepSessionActivity.this);

        // Get the default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        /*
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                if(){
                    Log.d(TAG, "found connected bonded device");
                    mBTDevices.add(device);
                }else{
                    Log.d(TAG, "found unconnected bonded device");
                }



            }
        }
        */
/*
        if (mBluetoothAdapter.getBondedDevices().size() > 0) {
            for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    Log.e("Removing has failed.", e.getMessage());
                }
            }
        }

*/
        if(mBluetoothAdapter == null){
            Log.d(TAG, "No bluetooth functionality");
            Toast toast = Toast.makeText(SetSleepSessionActivity.this, "This device doesn't support bluetooth connection", Toast.LENGTH_LONG);
            toast.show();
        }
        if(mBluetoothAdapter != null){
            discoverDevices();
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

    }


    public void discoverDevices(){
        //mBTDevices.clear();
        //mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
        //lvNewDevices.setAdapter(mDeviceListAdapter);

        if (!mBluetoothAdapter.isEnabled()){mBluetoothAdapter.enable();}
        //mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothEarbuds);


        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Cancelling discovery.");

            checkBTPermission();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }
        if (!mBluetoothAdapter.isDiscovering()) {
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }

        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        discoverDevicesIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        IntentFilter endDiscoveryIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver1, endDiscoveryIntent);






        //mDeviceListAdapter = new DeviceListAdapter(SetSleepSessionActivity.this, R.layout.device_adapter_view, mBTDevices);
        //lvNewDevices.setAdapter(mDeviceListAdapter);

    }

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothEarbuds);
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();
        Log.d(TAG, "onItemClick: deviceName = " + deviceName + " // deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);

            mBTDevices.get(i).createBond();

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver4, discoverDevicesIntent);
        }

        /*
        Log.d(TAG, "Establishing connection to the proxy.");
        // Establish connection to the proxy.
        mBluetoothAdapter.getProfileProxy(SetSleepSessionActivity.this, mProfileListener, BluetoothProfile.A2DP);

        */


    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d(TAG, "profile == BluetoothProfile.A2DP - Connecting");

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




}