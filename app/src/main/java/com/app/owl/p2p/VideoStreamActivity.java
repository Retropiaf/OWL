package com.app.owl.p2p;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.app.owl.R;

import java.io.IOException;


public class VideoStreamActivity extends AppCompatActivity {

    VideoView myVideoView;
    ProgressDialog dialog;
    AudioManager audio;
    MediaController mediaController;
    //String url = "http://192.168.0.15:88";
    //String url = "rtsp://192.168.0.15:88";
    String url = "rtsp://192.168.0.15:88";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);
        myVideoView = (VideoView) findViewById(R.id.video_screen);
        dialog = ProgressDialog.show(VideoStreamActivity.this, null, "Video loading...", true);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        mediaController = new MediaController(this);
        myVideoView.setMediaController(mediaController);
        myVideoView.setVideoURI(Uri.parse(url));
        myVideoView.requestFocus();
        /*
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        */



        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync();
                    //mediaPlayer.prepare();// might take long! (for buffering, etc)
                    mediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    System.err.println("IllegalArgumentException: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException: " + e.getMessage());
                }
            }
        });


        /*
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        */


    }






}
