package com.example.song_chords;

import android.app.ProgressDialog;
import android.content.Context;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class firebaseManager {
    // Write a message to the database
    public FirebaseDatabase database;
    public DatabaseReference usersRef;
    public DatabaseReference songsRef;
    public ArrayList<User> users;
    public ArrayList<Song> songs;

    public firebaseManager() {
        this.database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        this.songsRef = database.getReference("Songs");
        users = new ArrayList<>();
        songs = new ArrayList<>();

        setUsersRef();
        setSongsRef();
    }

    public int saveUser(User user) {
        if (!emailExist(user.email)) {
            String id = usersRef.push().getKey();
            usersRef.child(id).setValue(user);
            return 1; // user added
        }
        return 0; // user exist
    }

    public void saveSong(Song song) {
        String id = songsRef.push().getKey();
        songsRef.child(id).setValue(song);
    }

    public boolean emailExist(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email))
                return true;
        }
        return false;
    }

    public boolean userExist(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(user))
                return true;
        }
        return false;
    }

    public String getPassword(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email))
                return users.get(i).getPassword();
        }
        return null;
    }

    public void setUsersRef() {
        // Read from the database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (!emailExist(user.getEmail()))
                        users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setSongsRef() {
        // Read from the database
        songsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    songs.add(song);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Failed to read value.", databaseError.toException());
            }
        });
    }

    public ArrayList<Song> getSongByName(String name){
        ArrayList<Song> song=null;
        for(int i=0; i<songs.size(); i++){
            if(songs.get(i).getName().equals(name)){
                if(song==null) {song = new ArrayList<>();}
                song.add(songs.get(i));
            }
        }
        return song;
    }
}
