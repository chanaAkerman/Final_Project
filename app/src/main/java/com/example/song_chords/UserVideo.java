package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserVideo extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public FirebaseManager manager;

    public String userId;
    public ListView videoList;

    public TextView noVideoLabel;
    public ArrayList<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_video);

        manager = new FirebaseManager();

        Intent intent = getIntent();
        userId=intent.getStringExtra(MenuActivity.EXTRA_USER_ID);
        noVideoLabel = (TextView)findViewById(R.id.no_videos);

        videoList = (ListView)findViewById(R.id.video_list);

        videos = manager.getVideoListOfUser(userId);

        if(videos==null) {
            noVideoLabel.setVisibility(View.VISIBLE);
            videoList.setAdapter(null);
        }else{
            //creating adapter
            /*
            VideoList videoAdapter;
            videoAdapter = new VideoList(UserVideo.this,videos);
            videoList.setAdapter(videoAdapter);
            */

        }
    }
}
