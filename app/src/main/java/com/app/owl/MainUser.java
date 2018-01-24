package com.app.owl;

import android.util.Log;

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
    public String name;
    public String userEmail;
    public Boolean isRegistered;
    public Boolean isSignedIn;
    public Boolean onGoingSessions;
    public Boolean inOnGoingSessions;
    HashMap<String, SleepCircle> circles;


    public MainUser(){

    }


    public MainUser(String userUid, String userEmail, String userName, String name) {
        this.uid = userUid;
        this.name = name;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isRegistered = false;
        this.isSignedIn = false;
        this.onGoingSessions = false;
        this.inOnGoingSessions = false;
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

    public Boolean getIsRegistered(){ return isRegistered; }

    public Boolean getIsSignedIn(){ return isSignedIn; }

    public Boolean getOnGoingSessions(){ return onGoingSessions; }

    public Boolean getInOnGoingSessions(){ return inOnGoingSessions; }

    public void setOnGoingSessions(Boolean bool){ onGoingSessions = bool; }

    public void setInOnGoingSessions(Boolean bool){ inOnGoingSessions = bool; }

    public void setIsRegistered(){
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));
        this.isRegistered = true;
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));

    }



}
