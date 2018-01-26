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
    public Boolean isNotified;
    public HashMap<String, SleepSession> SleepSessions;
    public Boolean insideSession;
    public HashMap<String, SleepCircle> circles;
    public String currentSession;
    public String currentAlert;
    public String currentSecondUser;
    public String currentCircle;


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
        this.isNotified = false;
        this.insideSession = false;
        this.circles = new HashMap<>();
        this.currentSession = "";
        this.currentAlert = "";
        this.currentSecondUser = "";
        this.currentCircle = "";
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

    public Boolean getIsNotified(){ return isNotified; }

    public Boolean getInsideSession(){ return insideSession; }

    public String getCurrentSession(){return currentSession; }
    public String getCurrentAlert(){return currentAlert;}
    public String getCurrentSecondUser(){return currentSecondUser; }
    public String getCurrentCircle(){return currentCircle; }


    public void setOnGoingSession(Boolean bool){ this.onGoingSession = bool; }

    public void setIsNotified(Boolean bool){ this.isNotified = bool; }

    public void setInsideSession(Boolean bool){ this.insideSession = bool; }

    public void setIsRegistered(){
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));
        this.isRegistered = true;
        Log.d("Inside setIsRegistered", String.valueOf(this.isRegistered));

    }
    public void setCurrentSession(String session){ currentSession = session; }
    public void setCurrentAlert(String alert){ currentAlert = alert;}
    public void setCurrentSecondUser(String secondUser){ currentSecondUser = secondUser; }
    public void setCurrentCircle(String circle){currentCircle = circle; }



}
