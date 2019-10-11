package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchSongActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public static final String EXTRA_SONG_LINK = "com.example.application.song_chords.EXTRA_SONG_LINK";
    public static final String EXTRA_SONG_NAME = "com.example.application.song_chords.EXTRA_SONG_NAME";

    public TextView searchField;
    public ImageButton search;
    public ListView searchSongList;
    public ArrayList<Song> songs;

    public FirebaseManager manager;
    public String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);

        Intent intent = getIntent();
        userId=intent.getStringExtra(MenuActivity.EXTRA_USER_ID);

        manager = new FirebaseManager();
        songs = new ArrayList<>();

        searchField = (TextView)findViewById(R.id.search_field);
        search = (ImageButton) findViewById(R.id.search_btn);
        searchSongList = (ListView)findViewById(R.id.searchSongList);

        setSearchAction();

        // on click listener handler
        setSearchSongListAction();

        setAutomatiFill();

    }
    public void setAutomatiFill() {
        searchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String songToSearch = searchField.getText()+"";
                songs = manager.getSongByName(userId,songToSearch);
                if(songs!=null){
                    //creating adapter
                    SongsList songAdapter;
                    songAdapter = new SongsList(SearchSongActivity.this,songs);
                    searchSongList.setAdapter(songAdapter);
                }

            }
        });
    }

    public void setSearchAction(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(SearchSongActivity.this, R.anim.blink);
                search.startAnimation(animation);

                String songToSearch = searchField.getText()+"";
                songs = manager.getSongByName(userId,songToSearch);

                if(songs==null) {
                    Toast.makeText(SearchSongActivity.this, "Song Not Found", Toast.LENGTH_LONG).show();
                    searchSongList.setAdapter(null);
                }else{
                    /*
                    //creating adapter
                    SongsList songAdapter;
                    songAdapter = new SongsList(SearchSongActivity.this,songs);
                    searchSongList.setAdapter(songAdapter);
                    */
                }
            }
        });
    }

    public void setSearchSongListAction(){
        searchSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Song song = songs.get(i);

                //creating an intent
                Intent intent = new Intent(SearchSongActivity.this,SongActivity.class);

                //putting artist name and id to intent
                intent.putExtra(EXTRA_SONG_LINK, song.getChordsRef());
                intent.putExtra(EXTRA_SONG_NAME, song.getName());
                intent.putExtra(EXTRA_USER_ID, userId);

                //starting the activity with intent
                startActivity(intent);

                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchSongActivity.this, MenuActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        startActivity(intent);
        finish();
    }
}
