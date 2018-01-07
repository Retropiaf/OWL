package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddUserActivity extends AppCompatActivity {


    EditText editTextName;
    Button buttonAdd;

    DatabaseReference databaseSecondaryUsers;
    DatabaseReference databaseMainUser;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);



        editTextName = (EditText) findViewById(R.id.enterNameAddUser);
        buttonAdd = (Button) findViewById(R.id.add_user_btn);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSecondaryUser();
            }
        });
    }

    private void addSecondaryUser(){


        //String name = editTextName.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //if(!TextUtils.isEmpty(name)){
            //String id = databaseSecondaryUsers.push().getKey();

        String uid = user.getUid();


            // Query database for user with this uid
        databaseMainUser = FirebaseDatabase.getInstance().getReference();

            // Get the id of the returned user and save it to add secondary user





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
                        if(user != null){id = user.userId;}
                        if(id != null) {
                            Log.d("id!!!!!!!!!!!!!!!!!!!", id);
                            saveSecondaryUser(id);
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




    /*
        FindCurrentUser.findCurrentUser();

        if ( != null){
            saveSecondaryUser(id);
        }else{
            Log.d("Oops", "id was empty");
        }
    */

        /*
        if(id != null) {
            Log.d("Bef saveSecondaryUser", id);
            saveSecondaryUser(id);
        }else{
            Log.d("Oops", "id was empty");
        }
        */




            //Log.d("addSecondaryUser", id);

            //SecondaryUser secondaryUser = new SecondaryUser(id, name);

            //databaseSecondaryUsers.child(id).setValue(secondaryUser);

            //Toast.makeText(this, "Adding secondary user. Name: " + name, Toast.LENGTH_SHORT).show();



       // }else{
            //Toast.makeText(this, "You need to enter a name", Toast.LENGTH_SHORT).show();
        //}


    }


    private void saveSecondaryUser(String id) {

        Log.d("in saveSecondaryUser", id);

        databaseSecondaryUsers = FirebaseDatabase.getInstance().getReference("SecondaryUsers").child(id);

        String name = editTextName.getText().toString().trim();


        if(!TextUtils.isEmpty(name)){
            String secondUserId = databaseSecondaryUsers.push().getKey();

            SecondaryUser secondaryUser = new SecondaryUser(secondUserId, name);

            databaseSecondaryUsers.child(secondUserId).setValue(secondaryUser);

            Toast.makeText(this, "Adding secondary user. Name: " + name, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AddUserActivity.this, UsersActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(this, "You need to enter a name", Toast.LENGTH_SHORT).show();
        }


    }


}
