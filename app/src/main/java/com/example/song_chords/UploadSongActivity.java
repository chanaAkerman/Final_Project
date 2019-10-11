package com.example.song_chords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.UnicodeSetSpanner;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class UploadSongActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public static final int GET_FROM_GALLERY = 3;

    public String userId;
    public String songName;
    public String singerName;
    public String sondLyrics;

    public TextView song_name;
    public TextView singer_name;
    public TextView song_lyrics;
    public Button upload;
    public Button upload_picture;
    public ImageView imageView;

    public CheckBox public_CheckBox;
    public CheckBox private_CheckBox;

    private StorageReference storageReference;
    public FirebaseManager manager;

    private ProgressDialog progressDialog;

    private boolean pub=true;


    String picUri = "";

    File file = null;

    public boolean permissionAccepted = true;

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
        upload_picture = (Button) findViewById(R.id.btn_add_picture);
        song_name = (TextView) findViewById(R.id.text_song_name);
        singer_name = (TextView) findViewById(R.id.text_singer_name);
        song_lyrics = (TextView) findViewById(R.id.songInput);
        imageView = (ImageView) findViewById(R.id.singer_image);

        public_CheckBox = (CheckBox)findViewById(R.id.bok_public);
        private_CheckBox = (CheckBox)findViewById(R.id.bok_private);

        checkPermission();

        setOnUploadButton();
        setOnUploadPictureButton();
        setPublicCheckBoxAction();
        setPrivateCheckBoxAction();

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

    public void setOnUploadButton() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionAccepted) {
                    songName = song_name.getText() + "";
                    singerName = singer_name.getText() + "";
                    sondLyrics = song_lyrics.getText() + "";

                    if (songName == "") {
                        Toast.makeText(UploadSongActivity.this, "Please enter song name!", Toast.LENGTH_LONG).show();
                    } else if (singerName == "") {
                        Toast.makeText(UploadSongActivity.this, "Please enter singer name!", Toast.LENGTH_LONG).show();
                    } else {
                        // Create pdf file for the new song
                        //createPDF();
                        savePDF();

                        // Upload the song to fireBase
                        // Public - to the public song list
                        // Private - to the private song list
                        if (pub) {
                            uploadSongPublic();
                        } else {
                            uploadSongPrivate();
                        }

                        // Exit Activity after uploading - possible to stay and upload more.
                        exitDialog();

                    }
                }else{
                    Toast.makeText(UploadSongActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setOnUploadPictureButton() {
        upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionAccepted)
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                else{
                    Toast.makeText(UploadSongActivity.this, " Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String path = saveImage(bitmap);
                imageView.setImageBitmap(bitmap);

                Uri pictureUri = Uri.fromFile(new File(path));
                if(singerName==null )
                    singerName="unknown";
                final StorageReference filePath = storageReference.child("Photo").child(singerName + ".png");

                filePath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                picUri = uri.toString();

                            }
                        });
                    }
                });


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void savePDF(){
        Document document = new Document();
        String myFilePath = getExternalCacheDir().getAbsolutePath() + "/mySongPDF.pdf";

        try{
            String lyrics = song_lyrics.getText().toString();

            PdfWriter.getInstance(document,new FileOutputStream(myFilePath));
            document.open();
            document.add(new Paragraph(lyrics));
            document.close();

            file = new File(myFilePath);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    public void uploadSongPublic() {
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

                        Song newSong = new Song(songName, singerName, picUri, uri.toString());
                        //Upload song
                        manager.saveSong(newSong);
                    }
                });
            }
        });

    }

    public void uploadSongPrivate() {
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

                        Song newSong = new Song(songName, singerName, picUri, uri.toString());
                        //Upload song
                        manager.saveSongInUser(userId,newSong);
                    }
                });
            }
        });
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                getExternalCacheDir().getAbsolutePath() + "/"+singerName + ".jpg");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void setPublicCheckBoxAction(){
        public_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // save in public
                    private_CheckBox.setChecked(false);
                    pub=true;
                }
            }
        });
    }

    public void setPrivateCheckBoxAction(){
        private_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // save in private
                    public_CheckBox.setChecked(false);
                    pub=false;
                }
            }
        });
    }

    public void exitDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UploadSongActivity.this);
        builder1.setTitle("Upload song successfully completed");
        builder1.setMessage("Do you want to upload more songs?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        clear();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        Intent intent = new Intent(UploadSongActivity.this, MenuActivity.class);
                        intent.putExtra(EXTRA_USER_ID, userId);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void clear(){
        song_name.setText("");
        singer_name.setText("");
        song_lyrics.setText("");

        public_CheckBox.setChecked(true);
        imageView.setImageBitmap(null);
    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Warning!!!")
                .setMessage("Changes won't be saved!")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        // upload song

                        songName = song_name.getText() + "";
                        singerName= singer_name.getText() + "";
                        sondLyrics= song_lyrics.getText() + "";

                        if (songName == "") {
                            Toast.makeText(UploadSongActivity.this, "Please enter song name!", Toast.LENGTH_LONG).show();
                        } else if (singerName == "") {
                            Toast.makeText(UploadSongActivity.this, "Please enter singer name!", Toast.LENGTH_LONG).show();
                        }else {
                            createPDF();
                            uploadSongPublic();
                        }
                    }
                }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
