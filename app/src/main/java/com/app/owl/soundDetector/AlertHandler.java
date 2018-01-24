package com.app.owl.soundDetector;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christiane on 1/17/18.
 */

public class AlertHandler {
    private String TAG = "AlertHandler";
    private Date value;
    DatabaseReference databaseAlerts;

    public AlertHandler(){}

    public void registerAlertInUserDb(Alert alert, String userUid, String circleName, String sessionTime){

        // TODO: ADD alert to database
        if(alert != null){
            //String path = "/MainUsers/" + userUid + "/SleepSessions/" + sleepSession.getStart_time() + "/Alerts/" + alert.getStartTime() + "/alertEnded/";
            databaseAlerts = FirebaseDatabase.getInstance().getReference().child("MainUsers").child(userUid).child("SleepSessions").child(sessionTime).child("Alerts");
            Log.d(TAG, "databaseAlerts = " + databaseAlerts);
            //String id = databaseAlerts.push().getKey();

            databaseAlerts.child(alert.getStartTime()).setValue(alert);

            Log.d(TAG, "registerAlert: alert added! ");

        }else{
            Log.d(TAG, "registerAlert: failed to add alert to the database ");
        }
    }

    public String chooseResponder(){
        String currentResponder = "";
        Query lastAlert = databaseAlerts.limitToFirst(1);
        //String lastResponder = lastAlert.ResponderId;

        //Get the current sleep session
            // Get the parent id that is not the same as lastResponder, save as current responder

        return currentResponder;
    }

    public void toggleResponder(){}

    static void updateAlertTime(DatabaseReference database, String path, String value){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, value);
        database.updateChildren(childUpdates);
    }
    static void updateAlertBool(DatabaseReference database, String path, Boolean boolValue){
        //database = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, boolValue);
        database.updateChildren(childUpdates);
    }

}
