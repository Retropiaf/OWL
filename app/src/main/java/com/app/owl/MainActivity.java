package com.app.owl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TimerTask tt;
    int counter = 0;

    //startTime and offsetTime must be long and not float.
    long startTime = 0;
    long offsetTime = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.out.println("hi");
        //TextView app_name = (TextView) this.findViewById(R.id.text_view_app_name);
        //LayoutParams lp = new LinearLayout.LayoutParams(int 50, int 50);
        //lp.setMargins(0,25,0,0);
        //app_name.setLayoutParams(lp);
        //app_name.setText("Hello");
        //app_name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 50));
        //app_name.setLayoutParams(new
        //LinearLayout ll = new LinearLayout(this);
        //ll.setOrientation(LinearLayout.VERTICAL);
        //ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.));
/*

        tt = new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
                app_name.setText("hello");
            }
        };


        Timer timer = new Timer();

        // scheduling the task at interval
        timer.schedule(tt, 1000);
*/




        final Button button = findViewById(R.id.sign_up_btn);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                /*
                String text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;
                Context context = MainActivity.this;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                */
                Intent signUpIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(signUpIntent);
            }

        });



    }






}
