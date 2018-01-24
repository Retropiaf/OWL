package com.app.owl.sleepSession;

import com.app.owl.soundDetector.Alert;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Christiane on 1/21/18.
 */

public class SleepSession  implements Serializable {

    String sleepSessionId;
    String startTime;
    String endTime;
    String currentResponder;
    String firstResponder;
    String secondResponder;
    String circleName;
    Boolean onGoingAlert;
    Boolean notificationIgnored;
    String notificationIgnoredBy;
    //Boolean onGoingSession; Should be in MainUser
    HashMap<Integer, Alert> alerts;

    SleepSession(){}

    SleepSession(String circleName, String sleepSessionId, String currentResponder, String firstResponder, String secondResponder){
        this.startTime = String.valueOf(Calendar.getInstance().getTime());
        this.endTime = "";
        this.circleName = circleName;
        this.sleepSessionId = sleepSessionId;
        this.firstResponder = firstResponder;
        this.currentResponder = currentResponder;
        this.secondResponder = secondResponder;
        this.onGoingAlert = false;
        this.alerts = new HashMap();
        this.notificationIgnored = false;
        this.notificationIgnoredBy = "";

    }

    public String getSleepSessionId(){return sleepSessionId;}
    public String getStartTime(){return startTime;}
    public String getEndTime(){return endTime;}
    public String getCurrentResponder(){return currentResponder;}
    public String getFirstResponder(){return firstResponder;}
    public String getSecondResponder(){return secondResponder;}
    public String getCircleName(){return circleName;}
    public String getNotificationIgnoredBy(){return notificationIgnoredBy;}
    public Boolean getNotificationIgnored (){return notificationIgnored;}
    //public Boolean getOnGoingAlert(){return onGoingAlert;}
    //public HashMap<Integer, Alert> alerts(){return alerts;}

    public void setEndTime(String time ){this.endTime = time;}
    public void  setCurrentResponder(){
        if (this.currentResponder.equals(this.firstResponder)){
            this.currentResponder = this.secondResponder;
        }else{
            this.currentResponder = this.firstResponder;
        }
    }
    public void setNotificationIgnored (Boolean bool){this.notificationIgnored = bool;}
    public void setNotificationIgnoredBy(String userName){this.notificationIgnoredBy = userName;}
    //public void setOnGoingAlert(Boolean bool){this.onGoingAlert = bool;}
}
