package com.example.song_chords;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SongActivity extends AppCompatActivity {
    private final int REQUEST_CODE=101;
    private final int RESPONSE_CODE=200;
    private final int REQUEST_PREMISSION_CODE=1000;
    private Uri songUri;
    private Uri videoUri=null;
    private Uri reoordUri=null;
    public FirebaseManager manager;

    public static final String EXTRA_SONG_LINK = "com.example.application.song_chords.EXTRA_SONG_LINK";
    public static final String EXTRA_SONG_NAME = "com.example.application.song_chords.EXTRA_SONG_NAME";
    public static final String EXTRA_VIDEO_URI = "com.example.application.song_chords.EXTRA_VIDEO_URI";
    private final OkHttpClient client = new OkHttpClient();

    public PDFView pdfView;
    //TextView chordsRef;

    Button activateCamera;
    Button activateRecoerd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        activateCamera = (Button)findViewById(R.id.btn_camera);
        activateRecoerd = (Button)findViewById(R.id.btn_record);
        setCameraAction();
        // TO DO
        setReordingAction();

        manager=new FirebaseManager();
        //chordsRef = (TextView) findViewById(R.id.chords);
        pdfView = (PDFView) findViewById(R.id.pdfView);



        Intent intent =  getIntent();
        final String txtFileUrl = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_LINK);
        String name = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_NAME);

        new RetrievePdfStream().execute(txtFileUrl);
    }

    class RetrievePdfStream extends AsyncTask<String,Void, InputStream>{
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream=null;
            try {
                URL url =new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode()==RESPONSE_CODE){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

    public void setCameraAction(){
        activateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureVideo(view);
            }
        });
    }

    public void setReordingAction(){
        activateRecoerd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    public void captureVideo(View view){
        // to open camera app
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(videoIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(videoIntent,REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && requestCode==RESULT_OK){
            videoUri = data.getData();
        }
    }

    public void playVideo(View view){
        Intent intent=new Intent(SongActivity.this,VideoPlayActivity.class);
        intent.putExtra(EXTRA_VIDEO_URI, videoUri);
        startActivity(intent);

    }
}
/*
    <TextView
        android:id="@+id/chords"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/text_margin"
        android:visibility="invisible"
        android:text="@string/large_text" />
 */