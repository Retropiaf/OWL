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

public class FindCurrentUser {

    static String id;
    static String uid;
    static FirebaseUser user;


    static void findCurrentUser() {


        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){uid = user.getUid();}


        DatabaseReference databaseMainUser = FirebaseDatabase.getInstance().getReference();


        Query query = databaseMainUser.child("MainUsers").orderByChild("userUid").equalTo(uid);

        Log.d("After the Query!!!!!!!", uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("In onDataChange!!!!!!!", "In onDataChange!!!!!!!");
                if (dataSnapshot.exists()) {
                    Log.d("In onDataChange!!!!!!!", "DataSnapshot exists!!!!!!!");
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



        if(id != null){
            return id;
        }else{
            return null;
        }

    }



}
