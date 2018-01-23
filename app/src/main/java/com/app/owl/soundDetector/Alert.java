package com.app.owl.soundDetector;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Christiane on 1/17/18.
 */

public class Alert {
    private Date startTime;
    private Date endTime;
    private String firstResponderId;
    private String secondResponderId;

    //public Alert(){}

    public Alert(){

        this.startTime = Calendar.getInstance().getTime();

    }

    public String getFirstResponderId(){return firstResponderId;}
    public String getSecondResponderId(){return secondResponderId;}
    public Date getStartTime(){return startTime; }
    public Date getEndTime(){return endTime; }

    public void setFirstResponderId(String responder){this.firstResponderId = responder;}
    public void setSecondResponderId(String responder){this.secondResponderId = responder;}
    public void setEndTime(Date date){endTime = date; }

}
