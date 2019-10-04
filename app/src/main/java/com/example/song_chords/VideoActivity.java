package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodecList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    public static final String EXTRA_VIDEO_URI = "com.example.application.Song_Chords.EXTRA_VIDEO_URI";

    public String uri;
    public VideoView videoView;
    public MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        uri=intent.getStringExtra(UserVideo.EXTRA_VIDEO_URI);

        videoView = (VideoView) findViewById(R.id.video);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaController  = new MediaController(VideoActivity.this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
            }
        });
        videoView.start();
    }
}
