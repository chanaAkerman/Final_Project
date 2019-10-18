package com.example.song_chords;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SongActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";

    public static final String EXTRA_SONG_LINK = "com.example.application.song_chords.EXTRA_SONG_LINK";
    public static final String EXTRA_SONG_NAME = "com.example.application.song_chords.EXTRA_SONG_NAME";
    public static final String EXTRA_VIDEO_URI = "com.example.application.song_chords.EXTRA_VIDEO_URI";

    public static final String LOG_TAG = "ERROR";

    public String songName;

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

    public ScrollView scrollView;

    private StorageReference storageReference;

    public File fileNameVideo = null;
    public String fileNameAudio = null;

    boolean recording = false;
    private MediaRecorder recorder = null;
    private ProgressDialog progressDialog;

    public boolean isRecording = false;

    Chronometer simpleChronometer;
    public String rec_time;

    public boolean permissionAccepted = true;

    public int x=0;
    public int y=0;

    public int currentPage=0;


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

        // get variables from previous activity
        Intent intent = getIntent();
        final String txtFileUrl = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_LINK);
        songName = intent.getStringExtra(SearchSongActivity.EXTRA_SONG_NAME);
        userId = intent.getStringExtra(SearchSongActivity.EXTRA_USER_ID);

        checkPermission();

        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer

        // Dialog
        progressDialog = new ProgressDialog(SongActivity.this);

        // Firebase manager
        manager = new FirebaseManager();
        // Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        fileNameAudio = getExternalCacheDir().getAbsolutePath();
        try {
            fileNameAudio += "/" + createTransactionID() + ".3gp";
        } catch (Exception e) {
            Log.e(LOG_TAG, "Generate name");
        }

        pdfView = (PDFView) findViewById(R.id.pdfView);
        scrollView = (ScrollView)findViewById(R.id.scrollView) ;
        recordingLabel = (TextView) findViewById(R.id.recording_label);
        activateCamera = (Button) findViewById(R.id.btn_camera);
        activateRecord = (Button) findViewById(R.id.btn_record);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(songName);

        setCameraAction();
        setRecordingAction();

        new RetrievePdfStream().execute(txtFileUrl);

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        pdfView.setScrollX(x);
                        pdfView.setScrollY(y++);

                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10);
    }


    public void checkPermission() {
        if ((!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) ||
                (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) ||
                (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))||
                (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) )
        {
            permissionAccepted= false;
        }else {
            permissionAccepted = true;
        }
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SongActivity.this, SearchSongActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        startActivity(intent);
        finish();
    }



    // Camera Session
    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }

    public void setCameraAction() {
        activateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCamera()) {
                    if(permissionAccepted) { RecordingVideo();
                    } else {
                    Toast.makeText(SongActivity.this, " Permission denied", Toast.LENGTH_LONG).show(); }
                }else {
                    Toast.makeText(SongActivity.this, "Device does not support Camera", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void RecordingVideo() {
        try {
            fileNameVideo = new File(getExternalCacheDir().getAbsolutePath()+ "/" + createTransactionID() + ".mp4");

        } catch (Exception e) {
            Log.e(LOG_TAG, "failed to create file name video");
        }
        if (fileNameVideo != null) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Video name & Save Video");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    // generate name
                    uploadVideo("");
                } else {
                    String name = input.getText().toString();
                    uploadVideo(name);
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

    public void uploadVideo(String name) {
        progressDialog.setMessage("Uploading Video..");
        progressDialog.show();

        if (name == "") {
            try {
                name = createTransactionID();
            } catch (Exception e) {
                Toast.makeText(SongActivity.this, "Problems generate name", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Video name");
            }
        }
        final String vName = name;
        final StorageReference filePath = storageReference.child("Video").child(name + ".mp4");

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(SongActivity.this, videoUri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );

        final String duration = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
        );

        retriever.release();

        filePath.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;

                        Video newVideo = new Video(vName, duration, uri.toString());
                        manager.AddVideo(userId, newVideo);

                        //Do what you want with the url
                    }
                });
            }
        });
    }




    // Recording Session

    public void setRecordingAction() {
        activateRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionAccepted) {
                    if (!isRecording) {
                        isRecording = true;
                        startRecording();
                    } else {
                        isRecording = false;
                        stopRecording();
                    }
                }else {
                    Toast.makeText(SongActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
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

        Animation animation = AnimationUtils.loadAnimation(SongActivity.this, R.anim.blink2);
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

        rec_time = simpleChronometer.getText().toString();
        simpleChronometer.stop();

        showAlertAndSaveAudio();
    }

    private void uploadAudio(String name) {
        progressDialog.setMessage("Uploading Audio..");
        progressDialog.show();

        if (name == "") {
            try {
                name = createTransactionID();
            } catch (Exception e) {
                Toast.makeText(SongActivity.this, "Problems generate name", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Audio name");
            }
        }
        final String rName = name;
        final StorageReference filePath = storageReference.child("Audio").child(name + ".3gp");
        Uri uri = Uri.fromFile(new File(fileNameAudio));


        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;

                        Audio newAudio = new Audio(rName, rec_time, uri.toString());
                        manager.AddAudio(userId, newAudio);

                        //Do what you want with the url
                    }
                });
            }
        });
    }

    private void showAlertAndSaveAudio() {
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
                if (input.getText().toString().equals("")) {
                    // generate name
                    uploadAudio("");
                } else {
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