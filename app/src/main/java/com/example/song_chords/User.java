package com.example.song_chords;

import android.provider.MediaStore;

import java.util.ArrayList;

public class User {
    public String id;
    public String email;
    public String password;
    public boolean rememberMe;
    public ArrayList<Audio> audioList;
    public ArrayList<Video> videoList;
    public ArrayList<Song> songsList;

    public User(){ }

    public User(String email,String password){
        this.email=email;
        this.password =password;
        rememberMe=false;
        this.audioList =new ArrayList<>();
        this.videoList =new ArrayList<>();
        this.songsList =new ArrayList<>();
    }

    public User(String email,String password,boolean rememberMe){
        this.email=email;
        this.password =password;
        this.rememberMe=rememberMe;
        this.audioList =new ArrayList<>();
        this.videoList =new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getRememberMe() {
        return this.rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public ArrayList<Audio> getAudioList() {
        return audioList;
    }

    public ArrayList<Video> getVideoList() {
        return videoList;
    }

    public ArrayList<Song> getSongsList() {
        return songsList;
    }

    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }

    public void setAudioList(ArrayList<Audio> audioList) {
        this.audioList=audioList;
    }

    public void setSongsList(ArrayList<Song> songsList) {
        this.songsList = songsList;
    }

    public void clearAudioList(){
        this.audioList.clear();
    }

    public void clearVideoList(){
        this.videoList.clear();
    }

    public void clearSongsList(){
        this.songsList.clear();
    }

    @Override
    public String toString() {
        int vSize=0,aSize=0,sSize=0;
        if(videoList!=null){ vSize=videoList.size();}
        if(audioList!=null){ aSize=audioList.size();}
        if(songsList!=null){ sSize=songsList.size();}
        return "[ "+id+", "+email+", "+password+" audioSize: "+aSize +" videoSize: "+vSize +" songSize: "+sSize+"]";
    }

    @Override
    public boolean equals(Object obj) {
        return email.equals( ((User)obj).getEmail())&&
        password.equals(((User)obj).getPassword());
    }
}
