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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public TextView register;
    public TextView textEmail;
    public TextView password;
    public TextView forgotPassword;
    public Button logIn;

    public FirebaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize
        manager = new FirebaseManager();

        register = (TextView)findViewById(R.id.text_view_register);
        textEmail = (TextView)findViewById(R.id.text_email);
        password = (TextView)findViewById(R.id.edit_text_password);
        forgotPassword = (TextView)findViewById(R.id.text_view_forget_password);

        logIn = (Button)findViewById(R.id.button_sign_in);
        //addSongs();

        setLogInAction();

        setForgotPasswordAction();

        setRegisterAction();

    }
    public void setLogInAction(){
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText() + "";
                String pass = password.getText() + "";

                User user = new User(email, pass);

                if (email == "") {
                    Toast.makeText(LoginActivity.this, "Please enter an email address!", Toast.LENGTH_LONG).show();
                } else if (pass == "") {
                    Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();

                } else {
                    if (manager.userExist(user)) {
                        // User exist, start new activity
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra(EXTRA_USER_ID, manager.getUserKey(user));
                        startActivity(intent);
                        finish();
                    } else if (manager.emailExist(user.getEmail())) {
                        // wrong password
                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                        overridePendingTransition(R.anim.rotate,R.anim.blink);
                    } else {
                        Toast.makeText(LoginActivity.this, "User not exist in database", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void setForgotPasswordAction(){
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText()+"";
                if(email!="" && manager.emailExist(email)){
                    String pass = manager.getPassword(email);
                    sendMessage(email,pass);
                    //sendMessage2(email,pass);
                    Toast.makeText(LoginActivity.this, "Password sent to your Email address", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this, "User not exist in database", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setRegisterAction(){
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
        Song s1 = new Song("אם יש גן עדן", "אייל גולן","","");
        Song s3 = new Song("אהבה קטנה", "שירי מימון", "", "");
        Song s4 = new Song("את לי הכל", "הראל סקעת", "", "");
        Song s5 = new Song("אושר לדקה", "אורי בן ארי", "", "");
        Song s6 = new Song("אמא אם הייתי", "חנן בן ארי", "", "");
        Song s7 = new Song("באתי לחלום", "נתן גושן", "", "");
        Song s8 = new Song("בסוף כל יום", "אייל גולן", "", "");
        Song s9 = new Song("כל מה שיש לי", "נתן גושן", "", "");
        Song s10 = new Song("משהו ממני", "הראל סקעת", "", "");
        Song s11 = new Song("מתנות קטנות", "רמי קלינשטיין", "", "");
        Song s12 = new Song("עד שתחזור", "יובל דיין", "", "");
        Song s13 = new Song("רצים באוויר", "גיא ויהל", "", "");
        Song s14 = new Song("תוכו רצוף אהבה", "ישי ריבו", "", "");

        manager.saveSong(s1);
        manager.saveSong(s3);
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
        manager.saveSong(s14);
    }

}
