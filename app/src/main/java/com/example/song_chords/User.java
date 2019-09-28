package com.example.song_chords;

import java.util.ArrayList;

public class User {
    public String id;
    public String email;
    public String password;
    protected ArrayList<String> videoList;
    protected ArrayList<String> recordingList;

    public User(){ }

    public User(String email,String password){
        this.email=email;
        this.password =password;
        this.videoList =null;
        this.recordingList =null;
        this.recordingList =new ArrayList<>();
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

    public ArrayList<String> getVideoList() {
        return videoList;
    }

    public ArrayList<String> getRecordingList() {
        return recordingList;
    }

    public void setVideoList(ArrayList<String> videoList) {
        this.videoList = videoList;
    }

    public void setRecordingList(ArrayList<String> recordingList) {
        this.recordingList = recordingList;
    }

    public void addVideoUrl(String url){
        if(this.videoList==null)
            this.videoList=new ArrayList<>();
        this.videoList.add(url);
    }

    public void addRecordingUrl(String url){
        if(this.recordingList==null)
            this.recordingList=new ArrayList<>();
        this.recordingList.add(url);
    }

    public void clearRecordindData(){
        this.recordingList.clear();
    }

    public void clearVideoData(){
        this.videoList.clear();
    }

    @Override
    public String toString() {
        return "[ "+email+", "+password+" ]";
    }

    @Override
    public boolean equals(Object obj) {
        return email.equals( ((User)obj).getEmail())&&
        password.equals(((User)obj).getPassword());
    }
}
