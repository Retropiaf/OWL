package com.app.owl.soundDetector;

import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Christiane on 1/16/18.
 */

public class SoundCapture {

    public MediaRecorder mRecorder = null;

    public void start() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (Exception e) {
            }
            mRecorder.start();
        }

    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            double sound = mRecorder.getMaxAmplitude();
            Log.d("In SoundCapture", String.valueOf(sound));
            return sound;

        } else {
            return 0;

        }
    }
}
