package com.app.owl;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;

import java.util.List;

/**
 * Created by Christiane on 1/8/18.
 */

public class WirelessTestActivity2 extends Activity {

    protected static final String TAG = "WirelessTestActivity2";

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Establish connection to the proxy.
    mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.A2DP);


    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {

            if (profile == BluetoothProfile.A2DP) {

                mBluetoothSpeaker = (BluetoothA2dp) proxy;

                // no devices are connected
                List<BluetoothDevice> connectedDevices = mBluetoothSpeaker.getConnectedDevices();

                //the one paired (and disconnected) speaker is returned here
                int[] statesToCheck = {BluetoothA2dp.STATE_DISCONNECTED};
                List<BluetoothDevice> disconnectedDevices = mBluetoothSpeaker.getDevicesMatchingConnectionStates(statesToCheck);



                BluetoothDevice btSpeaker = disconnectedDevices.get(0);

                //WHAT NOW?

            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                mBluetoothSpeaker = null;
            }
        }
    };

}
