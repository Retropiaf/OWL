package com.app.owl.p2p;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.owl.R;


public class VideoStreamActivity extends Activity implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {

    //final static String USERNAME = "admin";
    //final static String PASSWORD = "";
    //final static String RTSP_URL = "rtsp://73.42.136.188:88/videoMain";
    //final static String RTSP_URL = "rtsp://retropiaf:Cs252552@73.42.136.188:88/videoSub";
    //final static String RTSP_URL = "rtsp://admin:password@192.168.0.15:88/videoSub";
    //final static String RTSP_URL = "http://192.168.0.15:88";
    //final static String RTSP_URL = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    //final static String RTSP_URL = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    //final static String RTSP_URL = "rtsp://admin:owly363663@192.168.0.108/cam/realmonitor?channel=1&subtype=1";
    //final static String RTSP_URL = "http://192.168.0.108/cgi-bin/audio.cgi?action=getAudio&httptype=singlepart&channel=1";
    //final static String RTSP_URL = "rtsp://admin:Cs252552@73.42.136.188:554/cam/realmonitor?channel=1&subtype=1";
    //final static String RTSP_URL = "rtsp://@73.42.136.188:554/cam/realmonitor?channel=1&subtype=1";

    private MediaPlayer _mediaPlayer;
    private SurfaceHolder _surfaceHolder;

    private Visualizer audioOutput = null;
    public float intensity = 0; //intensity is a value between 0 and 1. The intensity in this case is the system output volume

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askPermissions();
        startStream();

        /*
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No MODIFY_AUDIO_SETTINGS" );
        else
            Log.d("App", "Yes MODIFY_AUDIO_SETTINGS" );

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No RECORD_AUDIO" );
        else
            Log.d("App", "Yes RECORD_AUDIO" );

        Log.d("App","Requesting permissions" );
        ActivityCompat.requestPermissions( this, new String[]
                {
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        android.Manifest.permission.RECORD_AUDIO
                },1 );
        Log.d("App","Requested perms");
        */



        //createVisualizer();

        /*
        // Set up a full-screen black window.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.black);

        setContentView(R.layout.activity_video_stream);

        // Configure the view that renders live video.
        SurfaceView surfaceView =
                (SurfaceView) findViewById(R.id.surfaceView);

        Log.d("SurfaceView", String.valueOf(surfaceView));

        _surfaceHolder = surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        _surfaceHolder.setFixedSize(320, 240);
        */
    }
    public void askPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No MODIFY_AUDIO_SETTINGS" );
        else
            Log.d("App", "Yes MODIFY_AUDIO_SETTINGS" );

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            Log.d("App", "No RECORD_AUDIO" );
        else
            Log.d("App", "Yes RECORD_AUDIO" );

        Log.d("App","Requesting permissions" );
        ActivityCompat.requestPermissions( this, new String[]
                {
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        android.Manifest.permission.RECORD_AUDIO
                },1 );
        Log.d("App","Requested perms");

    }
    public void startStream(){
        // Set up a full-screen black window.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.black);

        setContentView(R.layout.activity_video_stream);

        // Configure the view that renders live video.
        SurfaceView surfaceView =
                (SurfaceView) findViewById(R.id.surfaceView);

        Log.d("SurfaceView", String.valueOf(surfaceView));

        _surfaceHolder = surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        _surfaceHolder.setFixedSize(320, 240);

    }

    /* SurfaceHolder.Callback */
    @Override
    public void surfaceChanged(
            SurfaceHolder sh, int f, int w, int h) {}

    @Override
    public void surfaceCreated(SurfaceHolder sh) {
        _mediaPlayer = new MediaPlayer();
        //_mediaPlayer.stop();
        //_mediaPlayer.release();
        _mediaPlayer.setDisplay(_surfaceHolder);

        Context context = getApplicationContext();
        //Map<String, String> headers = getRtspHeaders();
        //Uri source = Uri.parse(RTSP_URL);

        try {
            // Specify the IP camera's URL and auth headers.
            _mediaPlayer.setDataSource("rtsp://admin:Cs252552@73.42.136.188:554/cam/realmonitor?channel=1&subtype=1");

            // Begin the process of setting up a video stream.
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.prepareAsync();
        } catch (Exception e) {}
            //Log.d("MediaPlayer", String.valueOf("I was called!!!!!!!!!"));
            //_mediaPlayer.start();
            //Log.d("MediaPlayer", String.valueOf( _mediaPlayer.isPlaying()));
            /*
            _mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                //@Override
                String TAG = "Error";
                public boolean onError(MediaPlayer mediaPlayer, int what, int why) {
                    Log.e(TAG, "onError");
                    if (MediaPlayer.MEDIA_ERROR_UNKNOWN == what) {
                        Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                        if (MediaPlayer.MEDIA_ERROR_IO == why) {
                            Log.e(TAG, "MEDIA_ERROR_IO");

                        }
                        if (MediaPlayer.MEDIA_ERROR_MALFORMED == why) {
                            Log.e(TAG, "MEDIA_ERROR_MALFORMED");
                        }
                        if (MediaPlayer.MEDIA_ERROR_UNSUPPORTED == why) {
                            Log.e(TAG, "MEDIA_ERROR_UNSUPPORTED");
                        }
                        if (MediaPlayer.MEDIA_ERROR_TIMED_OUT == why) {
                            Log.e(TAG, "MEDIA_ERROR_TIMED_OUT");
                        }
                    } else if (MediaPlayer.MEDIA_ERROR_SERVER_DIED == what) {

                        Log.e(TAG, "MEDIA_ERROR_SERVER_DIED");
                    }
                    return true;
                }
            });
            */




    }

    @Override
    public void surfaceDestroyed(SurfaceHolder sh) {
        _mediaPlayer.release();
    }


/*
    private Map<String, String> getRtspHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        String basicAuthValue = getBasicAuthValue(USERNAME, PASSWORD);
        headers.put("Authorization", basicAuthValue);
        return headers;
    }

    private String getBasicAuthValue(String usr, String pwd) {
        String credentials = usr + ":" + pwd;
        int flags = Base64.URL_SAFE | Base64.NO_WRAP;
        byte[] bytes = credentials.getBytes();
        return "Basic " + Base64.encodeToString(bytes, flags);
    }

*/
    /* MediaPlayer.OnPreparedListener */
    @Override
    public void onPrepared(MediaPlayer _mediaPlayer) {
        Log.d("MediaPlayer", String.valueOf("I was called!!!!!!!!!"));
        createVisualizer();
        _mediaPlayer.start();
        Log.d("MediaPlayer", String.valueOf( _mediaPlayer.isPlaying()));

    }

    private void createVisualizer() {
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
        }, rate, true, false); // waveform not freq data
        Log.d("rate", String.valueOf(Visualizer.getMaxCaptureRate()));
        audioOutput.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean success = true;
        Log.d("App","in onRequestPermissionsResult");

        for( int i = 0; i < permissions.length; ++i ) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                Log.d("App",permissions[i]+" granted");
            } else {
                Log.d("App",permissions[i]+" denied");
                Toast.makeText(getApplicationContext(), "Needed " + permissions[i], Toast.LENGTH_SHORT).show();
                success = false;
            }
        }
        if( success ) {
            Log.d("App","Setting up visualizer");
            //createVisualizer();
            //startStream();
        }



    }

    /*
    public void onRequestPermissionsResult (int requestCode,
                                            String[] permissions,
                                            int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean success = true;
        for( int i = 0; i < permissions.length; ++i ) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                Log.d("App",permissions[i]+" granted");
            } else {
                Log.d("App",permissions[i]+" denied");
                Toast.makeText(getApplicationContext(), "Needed " + permissions[i], Toast.LENGTH_SHORT).show();
                success = false;
            }
        }
        if( success ) {
            Log.d("App","Setting up visualizer");
        }
    }
    */

}
