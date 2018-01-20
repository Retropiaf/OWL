package com.app.owl;

import com.app.owl.sleepCircle.SleepCircle;

import java.util.HashMap;

/**
 * Created by Christiane on 1/5/18.
 */

public class MainUser {


    String userId;
    String userUid;
    String userName;
    String userEmail;
    HashMap<String, SleepCircle> circles;


    public MainUser(){

    }


    public MainUser(String userUid, String userName, String userEmail) {
        //this.userId = userId;
        this.userUid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
        circles = new HashMap<>();
    }

    /*
    public String getUserId() {
        return userId;
    }
    */

    public String getUid() {
        return userUid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public HashMap<String, SleepCircle> getCircleList() {
        return circles;
    }



}
