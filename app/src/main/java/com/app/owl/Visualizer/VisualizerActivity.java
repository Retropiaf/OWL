package com.app.owl.Visualizer;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class VisualizerActivity extends AppCompatActivity {

    private Visualizer audioOutput = null;
    public float intensity = 0; //intensity is a value between 0 and 1. The intensity in this case is the system output volume
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_visualizer);
        createVisualizer();

    }


    private void createVisualizer(){
        int rate = Visualizer.getMaxCaptureRate();
        audioOutput = new Visualizer(0); // get output audio stream
        audioOutput.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                intensity = ((float) waveform[0] + 128f) / 256;
                Log.d("vis", String.valueOf(intensity));
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        },rate , true, false); // waveform not freq data
        Log.d("rate", String.valueOf(Visualizer.getMaxCaptureRate()));
        audioOutput.setEnabled(true);
    }


}
