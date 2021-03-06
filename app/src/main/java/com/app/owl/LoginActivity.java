package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Firebase auth
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference database;



    // widgets
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        setupFirebaseAuth();


        Button signIn = (Button) findViewById(R.id.email_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if the fields are filled out
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())){
                    Log.d(TAG, "onClick: attempting to authenticate.");

                    showDialog();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideDialog();

                            /*

                            database = FirebaseDatabase.getInstance().getReference().child("MainUsers");
                            database.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        MainUser mainUser = snapshot.getValue(MainUser.class);

                                        if(mainUser.getUid().equals(CurrentUser.uid) && (mainUser.isRegistered == null || !mainUser.isRegistered)){

                                            Log.d(TAG, "Found the user");

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(CurrentUser.uid + "/isRegistered/", true);
                                            childUpdates.put(CurrentUser.uid + "/isSignedIn/", true);
                                            database.updateChildren(childUpdates);

                                        }



                                    } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // TODO: Handle database error
                                }
                            });
*/


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView register = (TextView) findViewById(R.id.link_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView resetPassword = (TextView) findViewById(R.id.forgot_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView resendEmailVerification = (TextView) findViewById(R.id.resend_verification_email);
        resendEmailVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        hideSoftKeyboard();

    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }


    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /* Firebase setup */


    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){

                    final String uid = user.getUid();

                    if(user.isEmailVerified()){
                        Log.d(TAG, "inside setupFirebaseAuth");

                        /*

                        database = FirebaseDatabase.getInstance().getReference().child("MainUsers");
                        database.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    MainUser mainUser = snapshot.getValue(MainUser.class);

                                    if(mainUser.getUid().equals(CurrentUser.uid) && (mainUser.isRegistered == null || !mainUser.isRegistered)){

                                        Log.d(TAG, "User is signed in: " + mainUser.getIsSignedIn());

                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put(CurrentUser.uid + "/isRegistered/", true);
                                        childUpdates.put(CurrentUser.uid + "/isSignedIn/", true);
                                        database.updateChildren(childUpdates);

                                    }



                                    } // TODO HANDLE ELSE CASE: NO EMAIL FOR USER 2
                                }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // TODO: Handle database error
                            }

                        });

*/
                        Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        database = FirebaseDatabase.getInstance().getReference().child("MainUsers");

                        Map<String, Object> childUpdates = new HashMap<>();
                        Log.d(TAG, "Got here");
                        childUpdates.put(CurrentUser.uid + "/isRegistered/", true);
                        childUpdates.put(CurrentUser.uid + "/isSignedIn/", true);
                        database.updateChildren(childUpdates);


                        Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Taking you to the Main user welcome screen, hopefully!");
                        finish();

                    }else{
                        Toast.makeText(LoginActivity.this, "Check Your Email Inbox for a Verification Link", Toast.LENGTH_SHORT).show();

                        FirebaseAuth.getInstance().signOut();
                    }

                }else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }



}
