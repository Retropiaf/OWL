package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class IntroScreenActivity extends AppCompatActivity {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_intro_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent mainIntent = new Intent(IntroScreenActivity.this,UserMainActivity.class);
                    IntroScreenActivity.this.startActivity(mainIntent);
                    IntroScreenActivity.this.finish();
                }else{
                    Intent mainIntent = new Intent(IntroScreenActivity.this,RegisterActivity.class);
                    IntroScreenActivity.this.startActivity(mainIntent);
                    IntroScreenActivity.this.finish();
                }


            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
