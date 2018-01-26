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
            try {
                mRecorder = new MediaRecorder();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder.setOutputFile("/dev/null");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder.prepare();
            } catch (Exception e) {
            }
            try {
                mRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void stop() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            try {
                mRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            try {
                double sound = mRecorder.getMaxAmplitude();
                Log.d("In SoundCapture", String.valueOf(sound));
                return sound;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return 0;

        } else {
            return 0;

        }
    }
}
