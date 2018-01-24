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
    String start_time;
    String end_time;
    String currentResponder;
    String firstResponder;
    String secondResponder;
    String circleName;
    HashMap<Integer, Alert> alerts;

    SleepSession(){}

    SleepSession(String circleName, String sleepSessionId, String firstResponder, String secondResponder){
        this.start_time = String.valueOf(Calendar.getInstance().getTime());
        this.circleName = circleName;
        this.sleepSessionId = sleepSessionId;
        this.firstResponder = firstResponder;
        this.currentResponder = firstResponder;
        this.secondResponder = secondResponder;
        this.alerts = new HashMap();

    }

    public String getSleepSessionId(){return sleepSessionId;}
    public String getStart_time(){return start_time;}
    public String getEnd_time(){return end_time;}
    public String getCurrentResponder(){return currentResponder;}
    public String getFirstResponder(){return firstResponder;}
    public String getSecondResponder(){return secondResponder;}
    public String getCircleName(){return circleName;}
    //public HashMap<Integer, Alert> alerts(){return alerts;}

    public void setEnd_time(String time ){this.end_time = time;}
    public void  setCurrentResponder(){
        if (this.currentResponder.equals(this.firstResponder)){
            this.currentResponder = this.secondResponder;
        }else{
            this.currentResponder = this.firstResponder;
        }
    }
}
