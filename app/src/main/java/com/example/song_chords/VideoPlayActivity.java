package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    public static final String EXTRA_VIDEO_URI = "com.example.application.song_chords.EXTRA_VIDEO_URI";
    public VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        videoView = (VideoView)findViewById(R.id.video_view);

        Intent intent = getIntent();
        Uri videoUri = Uri.parse(intent.getStringExtra(SongActivity.EXTRA_VIDEO_URI));
        videoView.start();

    }
}
