package com.example.song_chords;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

public class VideoList extends ArrayAdapter<Video> {
    private Activity context;
    List<Video> videos;

    public VideoList(Activity context, List<Video> videos) {
        super(context, R.layout.video_list, videos);
        this.context = context;
        this.videos = videos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View listViewItem=inflater.inflate(R.layout.video_list,null,true);

        VideoView videoView=(VideoView) listViewItem.findViewById(R.id.video_view);
        TextView textViewVideoName=(TextView)listViewItem.findViewById(R.id.textViewVideoName);
        TextView textViewDuration=(TextView)listViewItem.findViewById(R.id.textViewDuration);

        Video video=videos.get(position);

        textViewVideoName.setText(video.getName());
        textViewDuration.setText(video.getDuration());

        return listViewItem;
    }

}

