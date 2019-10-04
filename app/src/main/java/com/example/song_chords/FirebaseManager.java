package com.example.song_chords;

import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseManager {
    public FirebaseDatabase database;
    public DatabaseReference usersRef;
    public DatabaseReference songsRef;
    public ArrayList<User> users;
    public ArrayList<Song> songs;

    public FirebaseManager() {
        this.database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        this.songsRef = database.getReference("Songs");
        users = new ArrayList<>();
        songs = new ArrayList<>();

        setUsersRef();
        setSongsRef();
    }
    public void updateUserAudio(String id, Audio newAudio) {
        User user = getUserByKey(id);
        // remove old user
        users.remove(user);

        ArrayList<Audio> audioArrayList = user.getAudioList();
        if (audioArrayList == null) {
            audioArrayList = new ArrayList<>();
        }
        audioArrayList.add(newAudio);
        user.setAudioList(audioArrayList);

        // add updated user
        users.add(user);
    }

    public void updateUserVideo(String id, Video newVideo) {
        User user = getUserByKey(id);
        // remove old user
        users.remove(user);

        ArrayList<Video> videoArrayList = user.getVideoList();
        if (videoArrayList == null) {
            videoArrayList = new ArrayList<>();
        }
        videoArrayList.add(newVideo);
        user.setVideoList(videoArrayList);

        // add updated user
        users.add(user);
    }

    public void AddAudio(final String id, Audio newAudio) {
        updateUserAudio(id, newAudio);

        User user = getUserByKey(id);
        final ArrayList<Audio> updated = user.getAudioList();


        // Update in firebase
        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < updated.size(); i++) {
                    dataSnapshot.getRef().child("AudioList").child(i + "").setValue(updated.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    public void AddVideo(final String id, Video newVideo) {
        updateUserVideo(id, newVideo);

        User user = getUserByKey(id);
        final ArrayList<Video> updated = user.getVideoList();


        // Update in firebase
        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < updated.size(); i++) {
                    dataSnapshot.getRef().child("VideoList").child(i + "").setValue(updated.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    public int saveUser(User user) {
        if (!emailExist(user.email)) {
            String id = usersRef.push().getKey();
            user.setId(id);
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

    public String getUserKey(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(user))
                return users.get(i).getId();
        }
        return null;
    }

    public User getUserByKey(String id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                return users.get(i);
            }
        }
        return null;
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
                // whenever data at this location is updated.videoList = null
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    // fetch audio
                    int number = (int)ds.child("AudioList").getChildrenCount();
                    ArrayList<Audio> audioArrayList = null;
                    if(number!=0) {
                        audioArrayList=new ArrayList<>();
                        for (int j = 0; j < number; j++) {
                            Audio audio = ds.child("AudioList").child(j+"").getValue(Audio.class);
                            audioArrayList.add(audio);
                        }
                    }
                    user.setAudioList(audioArrayList);

                    // fetch video
                    number = (int)ds.child("VideoList").getChildrenCount();
                    ArrayList<Video> videoArrayList = null;
                    if(number!=0) {
                        videoArrayList=new ArrayList<>();
                        for (int j = 0; j < number; j++) {
                            Video video = ds.child("VideoList").child(j+"").getValue(Video.class);
                            videoArrayList.add(video);
                        }
                    }
                    user.setVideoList(videoArrayList);

                    // add to local array
                    if (!emailExist(user.getEmail())) {
                        users.add(user);
                    }
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

    public ArrayList<Song> getSongByName(String name) {
        ArrayList<Song> song = null;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().equals(name)) {
                if (song == null) {
                    song = new ArrayList<>();
                }
                song.add(songs.get(i));
            }
        }
        return song;
    }

    public ArrayList<Audio> getAudioListOfUser(String id) {
        User currentUser = new User();

        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getId().equals(id)){
                currentUser = users.get(i);
            }
        }
        if (currentUser == null) {
            //user not exist
            return null;
        }
        ArrayList<Audio> current = currentUser.getAudioList();
        return current;
    }

    public ArrayList<Video> getVideoListOfUser(String id) {
        User currentUser = new User();

        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getId().equals(id)){
                currentUser = users.get(i);
            }
        }
        if (currentUser == null) {
            //user not exist
            return null;
        }
        ArrayList<Video> current = currentUser.getVideoList();
        return current;
    }

}
