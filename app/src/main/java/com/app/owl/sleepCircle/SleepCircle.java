package com.app.owl.sleepCircle;

import android.util.Log;

import com.app.owl.CurrentUser;

/**
 * Created by Christiane on 1/17/18.
 */
public class SleepCircle {

    ///static FirebaseUser user;
    String user1;
    String user2;
    String monitor;
    String circleName;
    String circleId;



    public SleepCircle() {}

    public SleepCircle(String circleName, String user2) {
        Log.d("SleepCircle", "Before CurrentUser.findId()");
        CurrentUser.findId();
        Log.d("SleepCircle", "after CurrentUser.findId()");
        this.user1 = CurrentUser.id;

        this.user2 = user2;

        this.circleName = circleName;
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
