package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public static final String EXTRA_USER_GMAIL = "com.example.application.Song_Chords.EXTRA_USER_GMAIL";
    public TextView login;
    public TextView textEmail;
    public TextView password;
    public TextView invalidEmail;
    public Button register;

    public FirebaseManager manager;

    public String gmailAccount;

    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\%\\-\\+]{1,256}"+
                    "\\@"+"[a-zA-Z0-9\\-]{0,64}"+"("+"\\."+"[a-zA-Z0-9][a-zA-Z0-9]\\-]{0,25}"+")");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        manager = new FirebaseManager();

        login = (TextView)findViewById(R.id.text_view_login);
        textEmail = (TextView)findViewById(R.id.text_email);
        password = (TextView)findViewById(R.id.edit_text_password);
        invalidEmail = (TextView)findViewById(R.id.error);
        register = (Button)findViewById(R.id.button_register);

        Spannable WordtoSpan = new SpannableString("Already have an account.\nLogin Here");
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 31, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(WordtoSpan);

        Intent intent = getIntent();
        gmailAccount=intent.getStringExtra(LoginActivity.EXTRA_USER_GMAIL);
        if(gmailAccount!="")
            textEmail.setText(gmailAccount);

        setRegisterAction();
        setLoginAction();
    }

    private void setLoginAction() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setRegisterAction(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText()+"";
                String pass = password.getText()+"";

                if(email=="") {
                    Toast.makeText(RegisterActivity.this, "Please enter email address!", Toast.LENGTH_LONG).show();
                }else if(pass=="") {
                    Toast.makeText(RegisterActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();
                }else {
                    if(validateEmail())
                    {
                        User user = new User(email, pass);
                        int res = manager.saveUser(user);

                        if (res == 0) {
                            Toast.makeText(RegisterActivity.this, "User exist", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "User added successfully", Toast.LENGTH_LONG).show();
                        }
                        Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.blink);
                        register.startAnimation(animation);

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    public boolean validateEmail(){
        String emailInput = textEmail.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            invalidEmail.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }


}
