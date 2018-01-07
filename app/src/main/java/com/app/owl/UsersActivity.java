package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    public static final String USERNAME = "username";
    public static final String USER_ID = "userid";
    String id;

    DatabaseReference databaseSecondaryUsers;
    DatabaseReference databaseMainUser;
    ListView listViewSecondaryUsers;

    List<SecondaryUser> secondaryUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();
            databaseMainUser = FirebaseDatabase.getInstance().getReference();
            Query query = databaseMainUser.child("MainUsers").orderByChild("userUid").equalTo(uid);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot mainUser : dataSnapshot.getChildren()) {
                            MainUser user = mainUser.getValue(MainUser.class);
                            if(user != null){id = user.userId;}
                            if(id != null) {

                                databaseSecondaryUsers = FirebaseDatabase.getInstance().getReference("SecondaryUsers").child(id);

                                listViewSecondaryUsers = (ListView) findViewById(R.id.listViewSecondaryUsers);

                                secondaryUserList = new ArrayList();

                                Button newUser = (Button) findViewById(R.id.new_user_btn);

                                newUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(UsersActivity.this, AddUserActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                listViewSecondaryUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        SecondaryUser user = secondaryUserList.get(i);
                                        Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                                        intent.putExtra(USERNAME, user.getSecondaryUserName());
                                        intent.putExtra(USER_ID, user.getUserId());

                                        startActivity(intent);
                                    }
                                });
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



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();
            databaseMainUser = FirebaseDatabase.getInstance().getReference();
            Query query = databaseMainUser.child("MainUsers").orderByChild("userUid").equalTo(uid);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot mainUser : dataSnapshot.getChildren()) {
                            MainUser user = mainUser.getValue(MainUser.class);
                            if(user != null){id = user.userId;}
                            if(id != null) {
                                databaseSecondaryUsers = FirebaseDatabase.getInstance().getReference("SecondaryUsers").child(id);

                                databaseSecondaryUsers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        secondaryUserList.clear();

                                        Log.d("usersactivity", "this is happening");

                                        for (DataSnapshot secondaryUserSnapshot: dataSnapshot.getChildren()){
                                            SecondaryUser user = secondaryUserSnapshot.getValue(SecondaryUser.class);
                                            Log.d("usersactivity", "this is happening too");
                                            secondaryUserList.add(user);
                                        }

                                        AllUsersList adapter = new AllUsersList(UsersActivity.this, secondaryUserList);

                                        listViewSecondaryUsers.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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




    }
}
