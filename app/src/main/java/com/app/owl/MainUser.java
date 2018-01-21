package com.app.owl;

import com.app.owl.sleepCircle.SleepCircle;

import java.util.HashMap;

/**
 * Created by Christiane on 1/5/18.
 */

public class MainUser {


    //private String userId;
    public String uid;
    public String userUid;
    public String userName;
    public String userEmail;
    HashMap<String, SleepCircle> circles;


    public MainUser(){

    }


    public MainUser(String userUid, String userName, String userEmail) {
        this.uid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
        circles = new HashMap<>();
    }

    /*
    public String getUserId() {
        return userId;
    }
    */

    public String getUserUid() {
        return userUid;
    }

    public String getUid() {
        return uid;
    }

    /*
    public String getId() {
        return userId;
    }
    */

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
