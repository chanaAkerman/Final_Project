package com.example.song_chords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    private static final int PERMISSION_REQUEST_CODE = 200;
    
    public String userId;

    public ImageButton search;
    public ImageButton upload;
    public ImageButton userAudio;
    public ImageButton userVideo;

    private boolean permissionAccepted = true;
    private GoogleSignInClient googleSignInClient;

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

        search=(ImageButton)findViewById(R.id.btn_search);
        upload=(ImageButton)findViewById(R.id.btn_upload);
        userAudio=(ImageButton)findViewById(R.id.btn_audio);
        userVideo=(ImageButton)findViewById(R.id.btn_video);

        setOnSearchButton();
        setOnUploadButton();
        setOnUserAudioButton();
        setOnUserVideoButton();
    }

    private void setOnUserVideoButton() {
            userVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.blink);
                    userVideo.startAnimation(animation);

                    Intent intent = new Intent(MenuActivity.this, UserVideo.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    startActivity(intent);
                    finish();

                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                }
            });
    }

    private void setOnUserAudioButton() {
        userAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.blink);
                userAudio.startAnimation(animation);

                Intent intent = new Intent(MenuActivity.this, UserAudio.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });
    }

    private void setOnUploadButton() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.blink);
                upload.startAnimation(animation);

                Intent intent = new Intent(MenuActivity.this, UploadSongActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });
    }

    private void setOnSearchButton() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.blink);
                search.startAnimation(animation);

                Intent intent = new Intent(MenuActivity.this, SearchSongActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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

                        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //On Succesfull signout we navigate the user back to LoginActivity
                                finish();
                            }
                        });
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
