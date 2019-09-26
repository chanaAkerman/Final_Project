package com.example.song_chords;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class SongActivity extends AppCompatActivity {
    public static final String EXTRA_SONG_LINK = "com.example.application.song_chords.EXTRA_SONG_LINK";
    public static final String EXTRA_SONG_NAME = "com.example.application.song_chords.EXTRA_SONG_NAME";


    TextView textSongChord;
    TextView songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textSongChord = (TextView)findViewById(R.id.chords);
        //songName  =(TextView)findViewById(R.id.song_name);

        Intent intent =  getIntent();
        String link = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_LINK);
        String name = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_NAME);
        //songName.setText(name);
        textSongChord.setText(link);
    }
}
