package com.example.song_chords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;

public class UploadSongActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";

    public String userId;
    public String songName;
    public String singerName;
    public String sondLyrics;

    public TextView song_name;
    public TextView singer_name;
    public TextView song_lyrics;
    public Button upload;

    private StorageReference storageReference;
    public FirebaseManager manager;

    private ProgressDialog progressDialog;

    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        Intent intent = getIntent();
        userId = intent.getStringExtra(LoginActivity.EXTRA_USER_ID);

        manager = new FirebaseManager();
        // Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(UploadSongActivity.this);

        upload = (Button) findViewById(R.id.button_upload);
        song_name = (TextView) findViewById(R.id.text_song_name);
        singer_name = (TextView) findViewById(R.id.text_singer_name);
        song_lyrics = (TextView) findViewById(R.id.songInput);

        setOnUploadButton();

    }

    public void setOnUploadButton() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songName = song_name.getText() + "";
                singerName= singer_name.getText() + "";
                sondLyrics= song_lyrics.getText() + "";

                if (songName == "") {
                    Toast.makeText(UploadSongActivity.this, "Please enter song name!", Toast.LENGTH_LONG).show();
                } else if (singerName == "") {
                    Toast.makeText(UploadSongActivity.this, "Please enter singer name!", Toast.LENGTH_LONG).show();
                }else {
                    createPDF();
                    uploadSong();
                }
            }
        });
    }

    public void createPDF() {

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        page.getCanvas().drawText(sondLyrics, 10, 25, paint);
        pdfDocument.finishPage(page);

        String myFilePath = getExternalCacheDir().getAbsolutePath() + "/mySongPDF.pdf";
        file = new File(myFilePath);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        pdfDocument.close();

    }

    public void uploadSong() {
        progressDialog.setMessage("Uploading Song..");
        progressDialog.show();

        Uri pdfUri = Uri.fromFile(file);
        final StorageReference filePath = storageReference.child("Songs").child(songName + ".pdf");

        filePath.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;

                        Song newSong = new Song(songName, singerName, "null", uri.toString());
                        //Upload song
                        manager.saveSong(newSong);
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Do you want to save changes?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        // upload song
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();

                Intent intent = new Intent(UploadSongActivity.this, MenuActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();
            }
        }).show();
    }
}
