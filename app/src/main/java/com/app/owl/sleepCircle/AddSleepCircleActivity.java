package com.app.owl.sleepCircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.owl.MainUser;
import com.app.owl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddSleepCircleActivity extends AppCompatActivity {

    SleepCircle sleepCircle;
    DatabaseReference database;
    String seconUserEmail;
    String secondUserId;
    String TAG = "AddSleepCircleActivity";
    SleepCircle circle;
    String circleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_circle);

        EditText editCircleName = (EditText) findViewById(R.id.editCircleName);
        circleName = editCircleName.getText().toString().trim();

        EditText editSecondUser = (EditText) findViewById(R.id.second_user_email);
        seconUserEmail = editSecondUser.getText().toString().trim();

        Button buttonAdd = (Button) findViewById(R.id.confirm_create_circle);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(circleName)){
                    Toast.makeText(AddSleepCircleActivity.this,"You need to enter a name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(seconUserEmail)){
                    Toast.makeText(AddSleepCircleActivity.this,"You need to enter an email", Toast.LENGTH_SHORT).show();
                }else{
                    findSecondUser();
                }

            }
        });

    }

    private void findSecondUser(){

        database = FirebaseDatabase.getInstance().getReference();

        database.child("MainUsers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MainUser user = snapshot.getValue(MainUser.class);
                            if(user.getUserEmail() == seconUserEmail){
                                secondUserId = user.getUserId();
                                circle = new SleepCircle(circleName, secondUserId);
                                addCircleToDatabase(circle.getCircleId(), circle);

                            }
                        }
                        Log.d(TAG, "no user found with email '" + seconUserEmail + "'");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

    }

    private void addCircleToDatabase(String id, SleepCircle circle) {

       database = FirebaseDatabase.getInstance().getReference("SleepCircles").child(id);

       String SleepCircleId =  database.push().getKey();

       circle.setCircleId(SleepCircleId);

       database.child(secondUserId).setValue(circle);

       Toast.makeText(AddSleepCircleActivity.this, "Adding sleep circle to database", Toast.LENGTH_SHORT).show();

       Intent intent = new Intent(AddSleepCircleActivity.this, SleepCirclesActivity.class);
       startActivity(intent);



    }
}
