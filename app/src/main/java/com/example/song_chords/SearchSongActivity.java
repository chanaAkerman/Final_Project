package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchSongActivity extends AppCompatActivity {
    TextView searchField;
    ImageButton search;
    ListView searchSongList;

    firebaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);

        manager = new firebaseManager();

        searchField = (TextView)findViewById(R.id.search_field);
        search = (ImageButton) findViewById(R.id.search_btn);
        searchSongList = (ListView)findViewById(R.id.searchSongList);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songToSearch = searchField.getText()+"";
                ArrayList<Song> songs = new ArrayList<>();
                songs = manager.getSongByName(songToSearch);
                if(songs==null)
                    Toast.makeText(SearchSongActivity.this, "Song Not Found", Toast.LENGTH_LONG).show();
                else{
                    //creating adapter
                    SongsList songAdapter;
                    songAdapter = new SongsList(SearchSongActivity.this,songs);
                    searchSongList.setAdapter(songAdapter);
                }
            }
        });
    }

    public void showListOfSongs(String songToSearch) {
    }
}
