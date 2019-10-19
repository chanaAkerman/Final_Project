package com.example.song_chords;

import android.graphics.Picture;

import androidx.annotation.Nullable;

public class Song {
    public String name;
    public String singerName;
    public String pictureRef;
    public String chordsRef;

    public Song(){ }

    public Song(String name,String singerName,String pictureRef,String chordsRef){
        this.name=name;
        this.singerName=singerName;
        this.pictureRef = pictureRef;
        this.chordsRef=chordsRef;
    }

    public String getName() {
        return name;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getPictureRef() {
        return pictureRef;
    }

    public String getChordsRef() {
        return chordsRef;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public void setPictureRef(String pictureRef) {
        this.pictureRef = pictureRef;
    }

    public void setChordsRef(String chordsRef) {
        this.chordsRef = chordsRef;
    }

    @Override
    public String toString() {
        return "[ "+name+", "+singerName+", "+pictureRef+", "+chordsRef+" ]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this.name.equals( ((Song)obj).getName()) &&
                this.singerName.equals( ((Song)obj).getSingerName()) &&
                this.pictureRef.equals( ((Song)obj).getPictureRef()) &&
                this.chordsRef.equals( ((Song)obj).getChordsRef())){
            return true;
        }
        return false;
    }
}
