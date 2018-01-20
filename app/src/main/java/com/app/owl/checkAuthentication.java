package com.app.owl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Christiane on 1/4/18.
 */

public class checkAuthentication{
    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
}
