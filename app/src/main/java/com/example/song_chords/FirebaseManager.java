package com.example.song_chords;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    CallBack callBack;

    public FirebaseManager() {
        this(null);
    }

    public FirebaseManager(CallBack callBack){
        this.database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        this.songsRef = database.getReference("Songs");
        users = new ArrayList<>();
        songs = new ArrayList<>();
        this.callBack = callBack;

        setUsersRef();
        setSongsRef();
    }

    public FirebaseManager refresh(){
       return new FirebaseManager(null);
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

    public void updateUserSong(String id, Song newSong) {
        User user = getUserByKey(id);
        // remove old user
        users.remove(user);

        ArrayList<Song> songArrayList = user.getSongsList();
        if (songArrayList == null) {
            songArrayList = new ArrayList<>();
        }
        songArrayList.add(newSong);
        user.setSongsList(songArrayList);

        // add updated user
        users.add(user);
    }

    public void AddAudio(final String id, Audio newAudio) {
        // Update local
        updateUserAudio(id, newAudio);

        User user = getUserByKey(id);
        final ArrayList<Audio> updated = user.getAudioList();

        for (int i = 0; i < updated.size(); i++) {
            usersRef.getRef().child(id).child("AudioList").child(i + "").setValue(updated.get(i));
        }
    }

    public void AddVideo(final String id, Video newVideo) {
        // Update local
        updateUserVideo(id, newVideo);

        User user = getUserByKey(id);
        final ArrayList<Video> updated = user.getVideoList();

        for (int i = 0; i < updated.size(); i++) {
            usersRef.child(id).child("VideoList").child(i + "").setValue(updated.get(i));
        }
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

    public User getUserByEmail(String email){
        User user=null;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email))
                return users.get(i);
        }
        return user;
    }

    public void updateUserRememberMe(String email,boolean r) {
        if (emailExist(email)) {
            User user = getUserByEmail(email);
            usersRef.child(user.getId()).child("rememberMe").setValue(r);

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getEmail().equals(email))
                    users.get(i).setRememberMe(r);
            }
        }
    }

    public void saveSong(Song song) {
        String id = songsRef.push().getKey();
        songsRef.child(id).setValue(song);
    }

    public void saveSongInUser(String id,Song song){
        // Update locals
        updateUserSong(id,song);

        User user = getUserByKey(id);
        final ArrayList<Song> updated = user.getSongsList();

        for (int i = 0; i < updated.size(); i++) {
            usersRef.getRef().child(id).child("SongsList").child(i + "").setValue(updated.get(i));
        }
    }

    public boolean emailExist(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email))
                return true;
        }
        return false;
    }

    public boolean rememberMe(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email) && users.get(i).getRememberMe()==true) {
                return true;
            }
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

                    // fetch songs
                    number = (int)ds.child("SongsList").getChildrenCount();
                    ArrayList<Song> songArrayList = null;
                    if(number!=0) {
                        songArrayList=new ArrayList<>();
                        for (int j = 0; j < number; j++) {
                            Song song = ds.child("SongsList").child(j+"").getValue(Song.class);
                            songArrayList.add(song);
                        }
                    }
                    user.setSongsList(songArrayList);

                    // add to local array
                    if (!emailExist(user.getEmail())) {
                        users.add(user);
                    }
                }

                if(callBack!=null){
                    callBack.fetch();
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

    public ArrayList<Song> getSongByName(String id,String name) {

        ArrayList<Song> song = null;
        // public song
        for (int i = 0; i < songs.size(); i++) {
            if(songs.get(i).getName().length()>=name.length()){
                String sub = songs.get(i).getName().substring(0,name.length());
                if(sub.equalsIgnoreCase(name)){
                    if (song == null) {
                        song = new ArrayList<>();
                    }
                    if(!songExist(song,songs.get(i))){
                        song.add(songs.get(i));
                    }
                }
            }
        }

        //public singer
        for (int i = 0; i < songs.size(); i++) {
            if(songs.get(i).getSingerName().length()>=name.length()){
                String sub = songs.get(i).getSingerName().substring(0,name.length());
                if(sub.equalsIgnoreCase(name)){
                    if (song == null) {
                        song = new ArrayList<>();
                    }
                    if(!songExist(song,songs.get(i))){
                        song.add(songs.get(i));
                    }
                }
            }
        }


        // private song
        for (int i = 0; i < users.size(); i++) {
            // if user exist
            if (users.get(i).getId().equals(id)) {
                // if song list is not null
                if(users.get(i).getSongsList()!=null){
                    ArrayList<Song> songs1 = new ArrayList<>();
                    songs1=users.get(i).getSongsList();

                    for(int j=0; j<songs1.size(); j++){
                        if(songs1.get(j).getName().length()>=name.length()){
                            String sub = songs1.get(j).getName().substring(0,name.length());
                            if(sub.equalsIgnoreCase(name)){
                                if (song == null) {
                                    song = new ArrayList<>();
                                }
                                if(!songExist(songs1,songs1.get(j))){
                                    song.add(songs1.get(j));
                                }
                            }
                        }
                    }
                }
            }
        }
        return song;
    }

    public boolean songExist(ArrayList<Song> songArrayList,Song song){
        if(songArrayList!=null){
            for(int i=0 ;i<songArrayList.size(); i++){
                if(songArrayList.get(i).equals(song)){
                    return true;
                }
            }
        }
        return false;
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

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
