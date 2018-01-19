package com.app.owl;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Christiane on 1/6/18.
 */

public class CurrentUser {

    static String TAG = "CurrentUser class";
    static String uid;
    static FirebaseUser user;
    public static String id;


    public static void findId() {

        Log.d(TAG, "inside finddId");

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){uid = user.getUid();}


        DatabaseReference databaseMainUser = FirebaseDatabase.getInstance().getReference();


        Query query = databaseMainUser.child("MainUsers").orderByChild("userUid").equalTo(uid);

        //Log.d("After the Query!!!!!!!", uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "MainUser" node with all children with uid = uid
                    for (DataSnapshot mainUser : dataSnapshot.getChildren()) {
                        MainUser user = mainUser.getValue(MainUser.class);
                        if(user != null){
                            id = user.userId;
                            returnValue();

                        }
                    }
                }else{
                    Log.d("In onDataChange!!!!!!!", "DataSnapshot doesn't exists :(((((");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    static String returnValue() {
        return id;
    }

    public static void isLoggedIn(){}


}
