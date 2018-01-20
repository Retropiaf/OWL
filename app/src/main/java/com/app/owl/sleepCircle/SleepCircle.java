package com.app.owl.sleepCircle;

import com.app.owl.CurrentUser;

/**
 * Created by Christiane on 1/17/18.
 */
public class SleepCircle {
    String user1;
    String user2;
    String circleName;
    String circleId;
    String monitorIp;



    public SleepCircle() {}

    public SleepCircle(String circleName, String user2, String monitorIp) {

        this.user1 = CurrentUser.uid;

        this.user2 = user2;

        this.circleName = circleName;

        this.monitorIp = monitorIp;
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

    public String getMonitorIp() {
        return monitorIp;
    }

}
