package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    DatabaseReference databaseSecondaryUsers;
    ListView listViewSecondaryUsers;

    List<SecondaryUser> secondaryUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        databaseSecondaryUsers = FirebaseDatabase.getInstance().getReference("secondaryUsers");

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseSecondaryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                secondaryUserList.clear();

                for(DataSnapshot secondaryUserSnapshot : dataSnapshot.getChildren()){
                    SecondaryUser user = secondaryUserSnapshot.getValue(SecondaryUser.class);
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
