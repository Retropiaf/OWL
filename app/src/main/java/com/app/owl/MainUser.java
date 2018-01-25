package com.app.owl;

import android.util.Log;

import com.app.owl.sleepCircle.SleepCircle;
import com.app.owl.sleepSession.SleepSession;

import java.util.HashMap;

/**
 * Created by Christiane on 1/5/18.
 */

public class MainUser {

    private String uid;
    private String userUid;
    private String userName;
    private String name;
    private String userEmail;
    private Boolean isRegistered;
    private Boolean isSignedIn;
    public Boolean onGoingSession;
    public HashMap<String, SleepSession> SleepSessions;
    public Boolean insideSession;
    public HashMap<String, SleepCircle> circles;


    public MainUser(){

    }

    public MainUser(String userUid, String userEmail, String userName, String name) {
        this.uid = userUid;
        this.name = name;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isRegistered = false;
        this.isSignedIn = false;
        this.onGoingSession = false;
        this.insideSession = false;
        this.circles = new HashMap<>();
    }


    public String getUserUid() {
        return userUid;
    }

    public String getUid() {
        return uid;
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

    public Boolean getIsRegistered(){ return isRegistered; }

    public Boolean getIsSignedIn(){ return isSignedIn; }

    public Boolean getOnGoingSession(){ return onGoingSession; }

    public Boolean getInsideSession(){ return insideSession; }


    public void setOnGoingSession(Boolean bool){ this.onGoingSession = bool; }

    public void setInsideSession(Boolean bool){ this.insideSession = bool; }

    public void setIsRegistered(){
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));
        this.isRegistered = true;
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));

    }



}
