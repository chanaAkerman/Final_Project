package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    TextView register;
    TextView textEmail;
    TextView password;
    TextView forgotPassword;
    Button logIn;

    firebaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new firebaseManager();

        register = (TextView)findViewById(R.id.text_view_register);
        textEmail = (TextView)findViewById(R.id.text_email);
        password = (TextView)findViewById(R.id.edit_text_password);
        forgotPassword = (TextView)findViewById(R.id.text_view_forget_password);

        logIn = (Button)findViewById(R.id.button_sign_in);
        //addSongs();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText() + "";
                String pass = password.getText() + "";

                User user = new User(email, pass);

                if (email == "") {
                    Toast.makeText(LoginActivity.this, "Please enter email address!", Toast.LENGTH_LONG).show();
                } else if (pass == "") {
                    Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();

                } else {
                    if (manager.userExist(user)) {
                        //Toast.makeText(LoginActivity.this, "User exist", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, SearchSongActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (manager.emailExist(user.getEmail())) {
                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "User not exist in database", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText()+"";
                if(email!="" && manager.emailExist(email)){
                    String pass = manager.getPassword(email);
                    sendMessage(email,pass);
                    //sendMessage2(email,pass);
                    Toast.makeText(LoginActivity.this, "your Password sent to your Email address", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this, "User not exist in database", Toast.LENGTH_LONG).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void sendMessage(final String email,final String pass) {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("chana.80a@gmail.com", "chana770");
                    sender.sendMail("SongChords App",
                            "Your Password is: "+pass,
                            "chana.80a@gmail.com",
                            email);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    public void sendMessage2(String email,String pass){
        Log.i("SendMailActivity", "Send Button Clicked.");

        String fromEmail = "chana.80a@gmail.com";
        String fromPassword = "chana770";
        String toEmails = email;

        List<String> toEmailList = Arrays.asList(toEmails
                .split("\\s*,\\s*"));
        Log.i("SendMailActivity", "To List: " + toEmailList);

        String emailSubject = "Password Reminder";

        String emailBody = "Your password is: "+pass;

        new SendMailTask(LoginActivity.this).execute(fromEmail,
                fromPassword, toEmailList, emailSubject, emailBody);
    }

    public void addSongs(){
        Song s1 = new Song("אם יש גן עדן", "אייל גולן","https://firebasestorage.googleapis.com/v0/b/songchords-b38e1.appspot.com/o/%D7%90%D7%99%D7%99%D7%9C%20%D7%92%D7%95%D7%9C%D7%9F.jpg?alt=media&token=a8dc8647-2d55-451b-8a5c-0ab33db3f0ef",
                "https://firebasestorage.googleapis.com/v0/b/songchords-b38e1.appspot.com/o/%D7%90%D7%9D%20%D7%99%D7%A9%20%D7%92%D7%9F%20%D7%A2%D7%93%D7%9F.txt?alt=media&token=e201ecbb-1a87-40b4-9246-01a41d19444f");


        Song s2 = new Song("אבא", "שלומי שבת", null, "https://firebasestorage.googleapis.com/v0/b/songchords-b38e1.appspot.com/o/%D7%90%D7%91%D7%90.txt?alt=media&token=8449b498-079a-4e3c-863e-8063fc654b82");
     /*     Song s3 = new Song("אהבה קטנה", "שירי מימון", "gs://songchords-b38e1.appspot.com/ShiriMimon.jpg", "gs://songchords-b38e1.appspot.com/אהבה קטנה - שירי מימון.txt");
        Song s4 = new Song("את לי הכל", "הראל סקעת", "gs://songchords-b38e1.appspot.com/HarelSkaat.jpg", "gs://songchords-b38e1.appspot.com/את לי הכל  - הראל סקעת.txt");
        Song s5 = new Song("אושר לדקה", "אורי בן ארי", "", "");
        Song s6 = new Song("אמא אם הייתי", "חנן בן ארי", "", "");
        Song s7 = new Song("באתי לחלום", "נתן גושן", "", "");
        Song s8 = new Song("בסוף כל יום", "אייל גולן", "", "");
        Song s9 = new Song("כל מה שיש לי", "נתן גושן", "", "");
        Song s10 = new Song("משהו ממני", "הראל סקעת", "", "");
        Song s11 = new Song("מתנות קטנות", "רמי קלינשטיין", "", "");
        Song s12 = new Song("עד שתחזור", "יובל דיין", "", "");
        Song s13 = new Song("רצים באוויר", "גיא ויהל", "", "");
        Song s14 = new Song("תוכו רצוף אהבה", "ישי ריבו", "", "");*/

        manager.saveSong(s1);
       manager.saveSong(s2);
       /*   manager.saveSong(s3);
        manager.saveSong(s4);

        manager.saveSong(s5);
        manager.saveSong(s6);
        manager.saveSong(s7);
        manager.saveSong(s8);
        manager.saveSong(s9);
        manager.saveSong(s10);
        manager.saveSong(s11);
        manager.saveSong(s12);
        manager.saveSong(s13);
        manager.saveSong(s14);*/
    }

}
