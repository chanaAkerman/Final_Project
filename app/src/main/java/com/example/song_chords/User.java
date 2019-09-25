package com.example.song_chords;

public class User {
    public String email;
    public String password;

    public User(){ }

    public User(String email,String password){
        this.email=email;
        this.password =password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "[ "+email+", "+password+" ]";
    }

    @Override
    public boolean equals(Object obj) {
        return email.equals( ((User)obj).getEmail())&&
        password.equals(((User)obj).getPassword());
    }
}
