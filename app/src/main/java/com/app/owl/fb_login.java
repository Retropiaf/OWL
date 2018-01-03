package com.app.owl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class fb_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(getApplication());
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_fb_login);


        LoginButton loginButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fb_login", "Success!");
                /*
                if (result.getPostId() != null) {
                    String title = getString(R.string.success);
                    String id = result.getPostId();
                    String alertMessage = getString(R.string.successfully_posted_post, id);
                    showResult(title, alertMessage);
                }

                handleSignInResult(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LoginManager.getInstance().logOut();
                        return null;
                    }
                });
                 */
                String text = "logged in!";
                int duration = Toast.LENGTH_SHORT;
                Context context = fb_login.this;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                System.out.println("logged in");
                Intent signUpIntent = new Intent(fb_login.this, LoginActivity.class);
                startActivity(signUpIntent);
            }

            @Override
            public void onCancel() {
                Log.d("fb_login", "Canceled");
                //handleSignInResult(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(fb_login.class.getCanonicalName(), error.getMessage());
                //handleSignInResult(null);
                /*
                String title = getString(R.string.error);
                String alertMessage = error.getMessage();
                showResult(title, alertMessage);
                */
            }

            /*
            private void showResult(String title, String alertMessage) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(title)
                        .setMessage(alertMessage)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }


            private void handleSignInResult(String title, String alertMessage) {
                new AlertDialog.Builder(fb_login.this)
                        .setTitle(title)
                        .setMessage(alertMessage)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }


            @Override

            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);


                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
             */

        });





    }
}
