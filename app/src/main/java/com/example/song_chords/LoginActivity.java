package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText()+"";
                String pass = password.getText()+"";
                User user = new User(email,pass);

                if(email!="" && pass!="" && manager.userExist(user)){
                    Toast.makeText(LoginActivity.this, "User exist", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this, "User not exist", Toast.LENGTH_LONG).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textEmail.getText()+"";
                if(email!="" && manager.emailExist(email)){
                    manager.sentMassage(email);
                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_LONG).show();
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

}
