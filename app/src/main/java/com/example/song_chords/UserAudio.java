package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class UserAudio extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";

    public FirebaseManager manager;

    public String userId;
    public ListView audioList;
    public Button refresh_button;

    public TextView noRecordingLabel;
    public ArrayList<Audio> recordings;
    public boolean isIndPlaying[];
    private MediaPlayer mediaPlayer=null;

    public boolean isPlaying= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_audio);

        manager = new FirebaseManager(new CallBack(){
            public void fetch(){
                refreshListUi();
            }
        });
        recordings = new ArrayList<>();

        Intent intent = getIntent();
        userId=intent.getStringExtra(MenuActivity.EXTRA_USER_ID);

        audioList = (ListView)findViewById(R.id.audio_list);
        noRecordingLabel = (TextView)findViewById(R.id.no_recordings);
        refresh_button = (Button) findViewById(R.id.btn_vrefresh);

        showRefreshButton();
        setAudioListAction();
    }
    public void showRefreshButton() {
        //Use` Callback instead
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshListUi();
            }
        });
    }

    private void refreshListUi() {

        recordings = manager.getAudioListOfUser(userId);

        if (recordings == null) {
            noRecordingLabel.setVisibility(View.VISIBLE);
            audioList.setAdapter(null);
        } else {
            noRecordingLabel.setVisibility(View.INVISIBLE);

            int num = recordings.size();
            isIndPlaying = new boolean[num];
            for (int i = 0; i < num; i++) isIndPlaying[i] = false;

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
                String audioUrl = audio.getRef();

                if(isIndPlaying[i]==false) {
                    streamAudio(audioUrl);
                    isIndPlaying[i] = true;
                }
                else {
                    mediaPlayer.stop();
                    isIndPlaying[i]=false;
                }
            }
        });
    }

    public void streamAudio(String audioUrl) {
        String url = audioUrl; // your URL here

        if(mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
        }
        else if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

    @Override
    public void onBackPressed() {
        // your code.
        Intent intent = new Intent(UserAudio.this, MenuActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        startActivity(intent);
        finish();
    }

}