package com.example.song_chords;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AudioList extends ArrayAdapter<Audio> {
    private Activity context;
    List<Audio> audio;

    public AudioList(Activity context, List<Audio> audio) {
        super(context, R.layout.audio_list, audio);
        this.context = context;
        this.audio = audio;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.audio_list, null, true);

        final ImageButton play = (ImageButton) listViewItem.findViewById(R.id.btn_play);
        TextView textViewRecordingName = (TextView) listViewItem.findViewById(R.id.text_record_name);
        final TextView textViewDuration = (TextView) listViewItem.findViewById(R.id.text_duration);
        TextView textViewRef = (TextView) listViewItem.findViewById(R.id.text_ref_audio);

        Audio myAudio = audio.get(position);

        textViewRecordingName.setText(myAudio.getName());
        textViewDuration.setText(myAudio.getDuration());
        textViewRef.setText(myAudio.getRef());

        return listViewItem;
    }
}
