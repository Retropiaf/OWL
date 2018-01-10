package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserMainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Button users = (Button) findViewById(R.id.users_btn);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

        Button testWireless = (Button) findViewById(R.id.new_sleep_btn);
        testWireless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, SetSleepSessionActivity.class);
                startActivity(intent);
            }
        });

        //Button newCircle = (Button) findViewById(R.id.new_circle_btn);


        /*
        newCircle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {   Intent signUpIntent = new Intent(UserMainActivity.this, setNewCircleActivity.class);
                startActivity(signUpIntent);
            }

        });

        */

        //getUserDetails();
        //setUserDetails();
    }


    private static final String TAG = "UserMainActivity";


    @Override
    protected void onResume(){
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated");
        }
    }


    /*
    private void setUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName("Christiane S").setPhotoUri(Uri.parse("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwixzemttL_YAhVT9GMKHaTyAyEQjRwIBw&url=http%3A%2F%2Fwww.hiphopvideoworld.com%2Fwebbie-this-me%2Fwebbie-this-me%2F&psig=AOvVaw0D7pNDpErwDtSdtu5SIbYq&ust=1515193106889070")).build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: User profile updated.");
                        getUserDetails();
                    }
                }
            });
        }
    }

    private void getUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String properties = "uid: " + uid + "\n" + "name: " + name + "\n" + "email: " + email + "\n" + "photoUrl " + photoUrl;

            Log.d(TAG, "getUserDetails: properties: \n" + properties);
        }
    }
    */
}
