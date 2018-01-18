package com.app.owl.sleepCircle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Christiane on 1/17/18.
 */

public class SleepCircle {

    static FirebaseUser user;
    String user1;
    String user2;
    String monitor;
    String circleName;
    String circleId;


    public SleepCircle() {}

    public SleepCircle(String circleName, String user2) {
        this.user1 = findCurrentUser();

        this.user2 = user2;

        this.circleName = circleName;
    }

    private String findCurrentUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){return user.getUid();}
        return null; // Handle case when no user is logged in

    }

    public String getSleepCircleName(){ return circleName; }


    public String getCircleId() {
        return circleId;
    }
    public void setCircleId (String id) {
        this.circleId = id;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public String getCircleName() {
        return circleName;
    }

    public String getMonitor() {
        return monitor;
    }

    /*
    static void addCircle(String circleId) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("sleepCircle").child(circleId).setValue(this);
    }
    */

}
