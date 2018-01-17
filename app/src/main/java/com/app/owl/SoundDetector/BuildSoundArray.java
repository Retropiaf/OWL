package com.app.owl.SoundDetector;

import android.util.Log;

/**
 * Created by Christiane on 1/17/18.
 */

public class BuildSoundArray {
    double[] secondsArray = new double[5];
    int i = 0;

    public BuildSoundArray(){
        this.secondsArray = secondsArray;
        this.i = i;
    }

    public double[] addSound(double sound){
        if(i == 5){i = 0;}
        Log.d("i", String.valueOf(i));
        secondsArray[i] = sound;
        i++;
        return secondsArray;
    }
}
