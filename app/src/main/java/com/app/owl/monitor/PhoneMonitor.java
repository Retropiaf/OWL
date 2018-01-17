package com.app.owl.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Christiane on 1/17/18.
 */

public class PhoneMonitor {
    String deviceIP;

    public PhoneMonitor() {
        this.deviceIP = getIpAddress();
    }

    public String getIpAddress(){
        try {
            InetAddress me = InetAddress.getLocalHost();
             return me.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("I'm sorry. I don't know my own address. Connect to wifi maybe.");
        }
    }


}
