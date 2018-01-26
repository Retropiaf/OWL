package com.app.owl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class IntroScreenActivity extends Activity {



    String TAG = "IntroScreenActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_intro_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        /** Duration of wait **/
        int SPLASH_DISPLAY_LENGTH = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "running");
                // Create an Intent that will start the Menu-Activity.

                /*
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent mainIntent = new Intent(IntroScreenActivity.this,UserMainActivity.class);
                    IntroScreenActivity.this.startActivity(mainIntent);
                    IntroScreenActivity.this.finish();
                }else{
                    Intent mainIntent = new Intent(IntroScreenActivity.this,RegisterActivity.class);
                    IntroScreenActivity.this.startActivity(mainIntent);
                    IntroScreenActivity.this.finish();
                }
                 */


                startActivity(new Intent(IntroScreenActivity.this, UserMainActivity.class));
                finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
