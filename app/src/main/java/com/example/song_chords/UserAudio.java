package com.example.song_chords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class UserAudio extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";

    public FirebaseManager manager;

    public String userId;
    public ListView audioList;
    public Button refresh;

    public TextView noRecordingLabel;
    public ArrayList<Audio> recordings;
    public ArrayList<Boolean> isRePlaying;

    public MediaPlayer mediaPlayer = null;
    public boolean isPlaying= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_audio);

        manager = new FirebaseManager();
        recordings = new ArrayList<>();

        Intent intent = getIntent();
        userId=intent.getStringExtra(MenuActivity.EXTRA_USER_ID);

        audioList = (ListView)findViewById(R.id.audio_list);
        noRecordingLabel = (TextView)findViewById(R.id.no_recordings);
        refresh = (Button) findViewById(R.id.btn_vrefresh);

        showRefreshButton();
        setAudioListAction();
    }

    public void showRefreshButton() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordings = manager.getAudioListOfUser(userId);

                if (recordings == null) {
                    noRecordingLabel.setVisibility(View.VISIBLE);
                    audioList.setAdapter(null);
                } else {
                    noRecordingLabel.setVisibility(View.INVISIBLE);

                    //creating adapter
                    AudioList audioAdapter;
                    audioAdapter = new AudioList(UserAudio.this, recordings);
                    audioList.setAdapter(audioAdapter);
                }
            }
        });
    }

    public void setAudioListAction(){
        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Audio audio = recordings.get(i);
                String audioUrl = audio.getRef();

                reset();
                streamAudio(audioUrl);
            }
        });
    }

    public void streamAudio(String audioUrl) {
        if(mediaPlayer!=null)
            mediaPlayer.stop();

        String url = audioUrl; // your URL here
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
            }
        });

    }

    public void reset(){
        isRePlaying = new ArrayList<>();
        for (int j = 0; j < recordings.size(); j++) {
            isRePlaying.add(false);
        }
        if(mediaPlayer!=null)
            mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent intent = new Intent(UserAudio.this, MenuActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        startActivity(intent);
        finish();
    }

}