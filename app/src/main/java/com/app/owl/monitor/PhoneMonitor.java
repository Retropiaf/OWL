package com.app.owl.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Christiane on 1/17/18.
 */

public class PhoneMonitor {
    InetAddress deviceIP;

    public PhoneMonitor() {
        try {
            this.deviceIP = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            System.out.println("I'm sorry. I don't know my own address. Connect to wifi maybe.");
        }
    }

}
