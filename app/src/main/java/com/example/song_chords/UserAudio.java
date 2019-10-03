package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserAudio extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public FirebaseManager manager;

    public String userId;
    public ListView audioList;

    public TextView noRecordingLabel;
    public ArrayList<Audio> recordings;

    public MediaPlayer mediaPlayer = null;

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

        showRecordings();
        setAudioListAction();
    }
    public void showRecordings() {

        ArrayList<Audio> recordings = manager.getAudioListOfUser(userId);

        if (recordings == null) {
            noRecordingLabel.setVisibility(View.VISIBLE);
            audioList.setAdapter(null);
        } else {
            //creating adapter
            AudioList audioAdapter;
            audioAdapter = new AudioList(UserAudio.this, recordings);
            audioList.setAdapter(audioAdapter);
        }
    }
    public void setAudioListAction(){
        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Audio audio = recordings.get(i);

                //play record
                play(view, audio.getRef());
            }
        });
    }
    public void play(View view,String uri) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            // stop running recordings
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
