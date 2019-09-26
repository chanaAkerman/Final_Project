package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    TextView login;
    TextView textEmail;
    TextView password;
    Button register;

    FirebaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        manager = new FirebaseManager();


        login = (TextView)findViewById(R.id.text_view_login);
        textEmail = (TextView)findViewById(R.id.text_email);
        password = (TextView)findViewById(R.id.edit_text_password);
        register = (Button)findViewById(R.id.button_register);

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
                    User user = new User(email,pass);
                    int res = manager.saveUser(user);

                    if(res==0){
                        Toast.makeText(RegisterActivity.this, "User exist", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegisterActivity.this, "User added successfully", Toast.LENGTH_LONG).show();
                    }

                    Intent intent  = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
