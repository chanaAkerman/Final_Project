package com.example.song_chords;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "com.example.application.Song_Chords.EXTRA_USER_ID";
    public static final String EXTRA_USER_GMAIL = "com.example.application.Song_Chords.EXTRA_USER_GMAIL";
    private static final String TAG = "AndroidClarified";
    public int RC_SIGN_IN=101;

    public TextView register;
    public TextView textEmail;
    public TextView password;
    public TextView forgotPassword;
    public Button logIn;
    public CheckBox rememberMe_CheckBox;
    public boolean rememberUser = false;

    public FirebaseManager manager;

    public SignInButton signInButton;
    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // initialize
        manager = new FirebaseManager();

        register = (TextView)findViewById(R.id.text_view_register);
        textEmail = (TextView)findViewById(R.id.text_email);
        password = (TextView)findViewById(R.id.edit_text_password);
        rememberMe_CheckBox = (CheckBox)findViewById(R.id.rememberMe);
        forgotPassword = (TextView)findViewById(R.id.text_view_forget_password);

        logIn = (Button)findViewById(R.id.logIn);
        signInButton = findViewById(R.id.signInGoogle);

        Spannable WordtoSpan = new SpannableString("Don't have an account.\nRegister Here");
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 32, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.setText(WordtoSpan);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //addSongs();

        setLogInAction();
        setForgotPasswordAction();
        setRegisterAction();

        setCheckBoxAction();
        // Automatic fill for exist user
        setAutomatiFill();

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        // sign in successfully
        String googleEmail = googleSignInAccount.getEmail();

        if(manager.emailExist(googleEmail) && manager.rememberMe(googleEmail)){
            User user = manager.getUserByEmail(googleEmail);
            textEmail.setText(googleEmail);
            password.setText(user.getPassword());

            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra(EXTRA_USER_ID, user.getId());
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
        else if(manager.emailExist(googleEmail)){
            textEmail.setText(googleEmail);
        }else{
            // move to register
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            intent.putExtra(EXTRA_USER_GMAIL, googleEmail);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    /*
    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(account);

            }else{
            Log.d(TAG, "Not logged in");
        }super.onStart();

    }*/


    public void setAutomatiFill() {
        textEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(manager.emailExist(textEmail.getText().toString()) &&
                        manager.rememberMe(textEmail.getText().toString())){
                    String p = manager.getPassword(textEmail.getText().toString());
                    password.setText(p);
                }
            }
        });
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
                        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.blink);
                        logIn.startAnimation(animation);

                        // User exist, start new activity
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra(EXTRA_USER_ID, manager.getUserKey(user));
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    } else if (manager.emailExist(user.getEmail())) {
                        // wrong password
                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                        overridePendingTransition(R.anim.rotate,R.anim.blink);
                    } else {
                        if(!isInternetAvailable()){
                            Toast.makeText(LoginActivity.this,"Internet Connection Is Required",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(LoginActivity.this, "User not exist in database", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
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
                intent.putExtra(EXTRA_USER_GMAIL, "");
                startActivity(intent);
                finish();
            }
        });
    }

    public void setCheckBoxAction(){
        rememberMe_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rememberUser = true;
                    manager.updateUserRememberMe(textEmail.getText().toString(),true);
                }else {
                    rememberUser = false;
                    manager.updateUserRememberMe(textEmail.getText().toString(),false);
                }
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

    public void addSongs(){
        Song s1 = new Song("Tonight", "Jonas Brothers","","");
        Song s3 = new Song("Hello Beautiful", "Jonas Brothers", "", "");
        Song s4 = new Song("Australia", "Jonas Brothers", "", "");
        Song s5 = new Song("Youngblood", "5 Second of Summer", "", "");
        Song s6 = new Song("Amnesia", "5 Second of Summer", "", "");
        Song s7 = new Song("She looks so perfect", "5 Second of Summer", "", "");
        Song s8 = new Song("Perfect", "Ed Sheeran", "", "");
        Song s9 = new Song("Everything has changed", "Taylor Swift", "", "");
        Song s12 = new Song("The story of us", "Taylor Swift", "", "");
        Song s10 = new Song("Firework", "Katy Perry", "", "");
        Song s11 = new Song("One last time", "Ariana Grande", "", "");

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
    }

}
