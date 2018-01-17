package com.app.owl.Sleepcircle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Christiane on 1/17/18.
 */

public class SleepCircle {
    static FirebaseUser user;
    String user1;
    String user2;
    String monitor;

    public SleepCircle() {
        if (user1 == null){
            this.user1 = findCurrentUser();
        } else {
            this.user2 = findCurrentUser();
        }
    }

    public String findCurrentUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){return user.getUid();}
        return null; // Handle case when no user is logged in

    }
}
