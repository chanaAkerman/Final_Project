package com.example.song_chords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    private static final int PERMISSION_REQUEST_CODE = 200;
    
    public String userId;

    public Button search;
    public Button upload;
    public Button userAudio;
    public Button userVideo;

    private boolean permissionAccepted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        userId=intent.getStringExtra(LoginActivity.EXTRA_USER_ID);

        //Permissions
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
        checkPermission();

        search=(Button)findViewById(R.id.btn_search);
        upload=(Button)findViewById(R.id.btn_upload);
        userAudio=(Button)findViewById(R.id.btn_audio);
        userVideo=(Button)findViewById(R.id.btn_video);

        setOnSearchButton();
        setOnUploadButton();
        setOnUserAudioButton();
        setOnUserVideoButton();
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

    private void setOnUserVideoButton() {
            userVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permissionAccepted) {
                        Intent intent = new Intent(MenuActivity.this, UserVideo.class);
                        intent.putExtra(EXTRA_USER_ID, userId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MenuActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void setOnUserAudioButton() {
        userAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAccepted) {
                    Intent intent = new Intent(MenuActivity.this, UserAudio.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MenuActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setOnUploadButton() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAccepted) {
                    Intent intent = new Intent(MenuActivity.this, UploadSongActivity.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MenuActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setOnSearchButton() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAccepted) {
                    Intent intent = new Intent(MenuActivity.this, SearchSongActivity.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MenuActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Exiting the App")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // The user wants to leave - so dismiss the dialog and exit
                        finish();
                        dialog.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();
            }
        }).show();
    }
}
