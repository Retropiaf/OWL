package com.app.owl.soundDetector;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/**
 * Created by Christiane on 1/24/18.
 */

class HideTimeLeft extends AsyncTask<Void, Void, Void>  {

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
    protected void onPostExecute(Void result) {
        SoundDetectorActivity.timeLeft.setVisibility(View.INVISIBLE);
        Log.d("Inside HideTimeLeft", "called");
    }
}
