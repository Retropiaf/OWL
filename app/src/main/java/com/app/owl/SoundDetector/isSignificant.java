package com.app.owl.SoundDetector;

/**
 * Created by Christiane on 1/17/18.
 */

public class isSignificant {
    double[] array;

    public isSignificant(double[] array) {
        this.array = array;
    }

    public boolean significant(){
        boolean significant = true;
        for(double sound : array){
            if (sound == 0){
                significant = false;
                break;
            }
        }
        return significant;
    }
}
