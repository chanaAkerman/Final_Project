package com.example.song_chords;

public class Video {
    public String name;
    public String duration;
    public String ref;

    public Video(){}

    public Video(String name,String duration,String ref){
        this.name=name;
        this.duration=duration;
        this.ref=ref;}

    public String getName() {
        return name; }

    public void setName(String name) {
        this.name = name; }

    public String getRef() {
        return ref; }

    public void setRef(String ref) {
        this.ref = ref; }

    public String getDuration() {
        return duration; }

    public void setDuration(String duration) {
        this.duration = duration; }

    @Override
    public String toString() {
        return "[ Video Recording "+name+", F.Reference"+ref+", time:"+duration+" ]";
    }
}
