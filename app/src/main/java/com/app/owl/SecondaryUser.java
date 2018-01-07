package com.app.owl;

import android.util.Log;

/**
 * Created by Christiane on 1/5/18.
 */

public class SecondaryUser {

    String userId;
    String userName;

    public SecondaryUser(){

    }

    public SecondaryUser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        Log.d("SecondaryUser", "I'm creating a new user!!!!!!");
    }


    public String getUserId() {
        return userId;
    }


    public String getSecondaryUserName() {
        return userName;
    }


}
