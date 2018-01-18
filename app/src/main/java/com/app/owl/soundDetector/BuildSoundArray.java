package com.app.owl.soundDetector;

/**
 * Created by Christiane on 1/17/18.
 */

public class BuildSoundArray {
    double[] secondsArray = new double[5];
    int i = 0;

    public BuildSoundArray(){
    }

    public double[] addSound(double sound){
        if(i == 5){i = 0;}
        secondsArray[i] = sound;
        i++;
        return secondsArray;
    }
}
