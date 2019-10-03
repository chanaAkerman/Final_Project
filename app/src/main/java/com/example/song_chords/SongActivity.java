package com.example.song_chords;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SongActivity extends AppCompatActivity{
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";

    public static final String EXTRA_SONG_LINK = "com.example.application.song_chords.EXTRA_SONG_LINK";
    public static final String EXTRA_SONG_NAME = "com.example.application.song_chords.EXTRA_SONG_NAME";
    public static final String EXTRA_VIDEO_URI = "com.example.application.song_chords.EXTRA_VIDEO_URI";

    public static final String LOG_TAG = "ERROR";

    private final int REQUEST_CODE = 101;
    private final int VIDEO_CAPTURE = 101;
    private final int RESPONSE_CODE = 200;
    static final int REQUEST_VIDEO_CAPTURE = 1;


    public PDFView pdfView;
    public TextView recordingLabel;
    public String userId;

    public Button activateCamera;
    public Button activateRecord;

    public Uri videoUri;
    public Uri audioUri;
    public FirebaseManager manager;

    private StorageReference storageReference;

    public String fileName;
    public String fileNameVideo = null;
    public String video = null;
    public String fileNameAudio = null;
    public String audio = null;

    public File mediaFile = null;

    public MediaRecorder mediaRecorder;
    public SurfaceHolder holder;
    boolean recording = false;
    private MediaRecorder recorder = null;
    private ProgressDialog progressDialog;

    public boolean isRecording = false;

    Chronometer simpleChronometer;
    public String rec_time;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer

        // Dialog
        progressDialog = new ProgressDialog(SongActivity.this);
        // Firebase manager
        manager = new FirebaseManager();

        // get variables from previous activity
        Intent intent = getIntent();
        final String txtFileUrl = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_LINK);
        String name = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_NAME);
        userId = intent.getStringExtra(SearchSongActivity.EXTRA_USER_ID);

        // Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        fileNameAudio = getExternalCacheDir().getAbsolutePath();
        fileNameVideo = getExternalCacheDir().getAbsolutePath();
        try {
            fileNameAudio += "/" + createTransactionID() + ".3gp";
            fileNameVideo += "/" + createTransactionID() + ".mp4";
        } catch (Exception e) {
            Log.e(LOG_TAG, "Generate name");
        }

        pdfView = (PDFView) findViewById(R.id.pdfView);
        recordingLabel = (TextView) findViewById(R.id.recording_label);
        activateCamera = (Button) findViewById(R.id.btn_camera);
        activateRecord = (Button) findViewById(R.id.btn_record);

        setCameraAction();
        setRecordingAction();

        new RetrievePdfStream().execute(txtFileUrl);
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == RESPONSE_CODE) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

    public String createTransactionID() throws Exception {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    // Camera Session
    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }

    public void setCameraAction() {
        activateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasCamera())
                    RecordingVideo();
                else {
                    Toast.makeText(SongActivity.this, "Device does not support Camera", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void RecordingVideo() {
        try {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + createTransactionID() + ".mp4");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Media name");
        }
        if(mediaFile!=null){
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            Uri videoUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();
            showAlertAndSaveVideo();
        }
    }

    public void showAlertAndSaveVideo() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        uploadVideoTo();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save video?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void uploadVideoTo() {
        progressDialog.setMessage("Uploading Video...");
        progressDialog.show();

        StorageReference filePath = storageReference.child("Video").child(video);
        videoUri = Uri.fromFile(mediaFile);

        filePath.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                // save record in user database
                //manager.saveVideo(userId, videoUri.toString());
            }
        });
    }





    // Recording Session
    public void setRecordingAction(){
        activateRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording){
                    isRecording=true;
                    startRecording();
                }else{
                    isRecording=false;
                    stopRecording();
                }
            }
        });
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileNameAudio);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        simpleChronometer.setVisibility(View.VISIBLE);
        recordingLabel.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(SongActivity.this,R.anim.blink);
        activateRecord.startAnimation(animation);

        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();
        recorder.start();
    }

    private void stopRecording() {
        simpleChronometer.setVisibility(View.INVISIBLE);
        recordingLabel.setVisibility(View.INVISIBLE);
        recorder.stop();
        recorder.release();
        recorder = null;

        activateRecord.clearAnimation();

        rec_time=simpleChronometer.getText().toString();
        simpleChronometer.stop();

        showAlertAndSaveAudio();
    }

    private void uploadAudio(String name){
        progressDialog.setMessage("Uploading Audio..");
        progressDialog.show();

        if(name==""){
            try {
                name = createTransactionID();
            }catch (Exception e){
                Toast.makeText(SongActivity.this, "Problems generate name", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Media name");
            }
        }
        StorageReference filePath = storageReference.child("Audio").child(name+".3gp");
        final Uri uri = Uri.fromFile(new File(fileNameAudio));
        final Audio newAudio = new Audio(name,rec_time,uri.toString());

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                manager.AddAudio(userId,newAudio);
            }
        });
    }

    private void showAlertAndSaveAudio(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter recording name & Save Audio");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals("")){
                    // generate name
                    uploadAudio("");
                }else {
                    String name = input.getText().toString();
                    uploadAudio(name);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}