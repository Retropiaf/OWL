package com.app.owl.soundDetector;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Christiane on 1/24/18.
 */

class SetTextTimeLeft extends AsyncTask<String, Void, Void>  {

    protected Void doInBackground(String... string) {
        return null;
    }
    protected void onPostExecute(String result) {
        SoundDetectorActivity.snoozeClock.setText(result);
        Log.d("In SetTextSnoozeClock", "called. result = " + result);
    }
}
