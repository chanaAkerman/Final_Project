package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    
    public String userId;

    public Button search;
    public Button upload;
    public Button userAudio;
    public Button userVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        userId=intent.getStringExtra(LoginActivity.EXTRA_USER_ID);

        search=(Button)findViewById(R.id.btn_search);
        upload=(Button)findViewById(R.id.btn_upload);
        userAudio=(Button)findViewById(R.id.btn_audio);
        userVideo=(Button)findViewById(R.id.btn_video);

        setOnSearchButton();
        setOnUploadButton();
        setOnUserAudioButton();
        setOnUserVideoButton();
    }

    private void setOnUserVideoButton() {
        userVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, UserVideo.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setOnUserAudioButton() {
        userAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, UserAudio.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setOnUploadButton() {
    }

    private void setOnSearchButton() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, SearchSongActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();
            }
        });
    }
}
