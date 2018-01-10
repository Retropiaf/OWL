package com.app.owl;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Christiane on 1/8/18.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice>  {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices){
        super(context, tvResourceId, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);
        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);

            if (deviceName.getText() != null) {
                Log.d("In DeviceListAdapter", String.valueOf(deviceName.getText()));
                deviceName.setText(device.getName());
            }else{
                Log.d("In DeviceListAdapter", "deviceName == null");
                deviceName.setText("Anonymous device");
            }
            if (deviceAddress.getText() != null) {
                deviceAddress.setText(device.getAddress());
            }else{
                deviceName.setText("Anonymous address");
            }
        }
        return convertView;
    }
}
