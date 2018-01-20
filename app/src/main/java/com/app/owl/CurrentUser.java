package com.app.owl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Christiane on 1/6/18.
 */

public class CurrentUser {

    static FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    public static String uid =  user != null ? user.getUid() : null;

}
