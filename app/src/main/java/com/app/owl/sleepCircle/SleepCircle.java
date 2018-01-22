package com.app.owl.sleepCircle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Christiane on 1/17/18.
 */
public class SleepCircle {
    String user1;
    String user2;
    String secondUserName;
    private String circleName;
    String circleId;
    String monitorIp;
    String monitorName;
    DatabaseReference database;



    public SleepCircle() {}

    public SleepCircle(String circleName, String user2, String secondUserName, String monitorIp, String monitorName) {

        this.user1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.user2 = user2;
        this.secondUserName = secondUserName;
        this.circleName = circleName;
        this.monitorIp = monitorIp;
        this.monitorName = monitorName;
    }


    public String getCircleName(){ return circleName;}


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

    public String getMonitorIp() {
        return monitorIp;
    }
    public String getMonitorName() {
        return monitorName;
    }


}
