package com.app.owl.soundDetector;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Christiane on 1/17/18.
 */

public class AlertHandler {
    private String TAG = "AlertHandler";
    DatabaseReference databaseAlerts;

    public AlertHandler(){}

    public void registerAlert(Alert alert){
        if(alert != null){
            String id = databaseAlerts.push().getKey();

            databaseAlerts.child(id).setValue(alert);

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

}
