package com.app.owl.soundDetector;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * Created by Christiane on 1/23/18.
 */

class HideEndSessionBtn implements Runnable  {
    Activity activity;
    public HideEndSessionBtn(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void run()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                SoundDetectorActivity.endSession.setVisibility(View.INVISIBLE);
            }
        });
    }
}
