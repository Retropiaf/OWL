package com.app.owl.p2p;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.app.owl.R;

import java.util.HashMap;
import java.util.Map;


public class VideoStreamActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {

    final static String USERNAME = "admin";
    final static String PASSWORD = "password1";
    //final static String RTSP_URL = "rtsp://192.168.0.15:88/videoMain";
    final static String RTSP_URL = "rtsp://admin:password1@192.168.0.15:88/videoSub";
    //final static String RTSP_URL = "http://192.168.0.15:88";
    //final static String RTSP_URL = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";

    private MediaPlayer _mediaPlayer;
    private SurfaceHolder _surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public void surfaceChanged(
            SurfaceHolder sh, int f, int w, int h) {}

    @Override
    public void surfaceCreated(SurfaceHolder sh) {
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setDisplay(_surfaceHolder);

        Context context = getApplicationContext();
        Map<String, String> headers = getRtspHeaders();
        Uri source = Uri.parse(RTSP_URL);

        try {
            // Specify the IP camera's URL and auth headers.
            _mediaPlayer.setDataSource(RTSP_URL);

            // Begin the process of setting up a video stream.
            _mediaPlayer.setOnPreparedListener(this);
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


            _mediaPlayer.prepareAsync();
            //Log.d("MediaPlayer", String.valueOf("I was called!!!!!!!!!"));
            //_mediaPlayer.start();
            //Log.d("MediaPlayer", String.valueOf( _mediaPlayer.isPlaying()));
        }
        catch (Exception e) {}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder sh) {
        _mediaPlayer.release();
    }

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

    /*
MediaPlayer.OnPreparedListener
*/
    @Override
    public void onPrepared(MediaPlayer _mediaPlayer) {
        Log.d("MediaPlayer", String.valueOf("I was called!!!!!!!!!"));
        _mediaPlayer.start();
        Log.d("MediaPlayer", String.valueOf( _mediaPlayer.isPlaying()));
    }




}
