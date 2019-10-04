package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserVideo extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public static final String EXTRA_VIDEO_URI = "com.example.application.Song_Chords.EXTRA_VIDEO_URI";

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
            VideoList videoAdapter;
            videoAdapter = new VideoList(UserVideo.this,videos);
            videoList.setAdapter(videoAdapter);
        }
    }
    public void setSearchSongListAction(){
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Video video = videos.get(i);

                //creating an intent
                Intent intent = new Intent(UserVideo.this,VideoActivity.class);
                intent.putExtra(EXTRA_VIDEO_URI, video.getRef());
                startActivity(intent);

            }
        });
    }
}
