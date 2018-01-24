package com.app.owl.soundDetector;

/**
 * Created by Christiane on 1/17/18.
 */

public class Alert {
    private String startTime;
    private String endTime;
    private String firstResponderId; // Who was supposed to handle alert
    private String secondResponderId; // Back up responder
    private String alertResponderId; // Who actually handled alert
    private Boolean alertRedirected; // Did a redirect happen
    private Boolean alertAnswered; // did a responder click the button on their phones
    private Boolean alertEnded;
    private Boolean speaker;

    public Alert(){}

    public Alert(String startTime){

        this.startTime = startTime;
        this.alertRedirected = false;
        this.alertAnswered = false;
        this.alertEnded = false;
        this.speaker = false;
        this.endTime = "";
    }

    public String getFirstResponderId(){ return firstResponderId; }
    public String getSecondResponderId(){ return secondResponderId; }
    public String getAlertResponderId(){ return alertResponderId; }
    public String getStartTime(){ return startTime; }
    public String getEndTime(){ return endTime; }
    public Boolean getAlertRedirected(){ return alertRedirected; }
    public Boolean getAlertAnswered(){ return alertAnswered; }
    public Boolean getAlertEnded(){ return alertEnded; }
    public Boolean getSpeaker(){ return speaker; }

    public void setFirstResponderId(String responder){this.firstResponderId = responder;}
    public void setSecondResponderId(String responder){this.secondResponderId = responder;}
    public void setEndTime(String date){endTime = date; }
    public void setAlertResponderId(String responderId){
        alertResponderId = responderId;
    }
    public void setAlertRedirected(Boolean bool){ this.alertRedirected = bool; }
    public void setAlertAnswered(Boolean bool){ this.alertAnswered = bool; }
    public void setAlertEnded(Boolean bool){ this.alertEnded = bool; }
    public void setSpeaker(Boolean bool){ this.speaker = bool; }
}
