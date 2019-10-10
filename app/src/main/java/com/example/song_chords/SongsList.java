package com.example.song_chords;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.ArrayAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SongsList extends ArrayAdapter<Song> {
    private Activity context;
    List<Song> songs;

    public SongsList(Activity context, List<Song> songs) {
        super(context, R.layout.songs_list, songs);
        this.context = context;
        this.songs = songs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.songs_list, null, true);

        ImageView imageViewSingerPic = (ImageView)listViewItem.findViewById(R.id.singerImage);
        TextView textViewSongName = (TextView) listViewItem.findViewById(R.id.textViewSongName);
        TextView textViewSingerName = (TextView) listViewItem.findViewById(R.id.textViewSingerName);

        Song song = songs.get(position);

        textViewSongName.setText(song.getName());
        textViewSingerName.setText("   "+song.getSingerName());
        if(song.getPictureRef()!="" && song.getPictureRef()!=null){
            //imageViewSingerPic.setBackground(null);
            //imageViewSingerPic.setImageResource("@");
            Glide.with(context).load(song.getPictureRef()).into(imageViewSingerPic);
        }

        return listViewItem;
    }
}
