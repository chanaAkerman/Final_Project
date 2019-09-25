package com.example.song_chords;

import android.nfc.Tag;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class firebaseManager {
    // Write a message to the database
    public FirebaseDatabase database;
    public DatabaseReference usersRef;
    public DatabaseReference songsRef;
    public ArrayList<User> users;

    public firebaseManager(){
        this.database=FirebaseDatabase.getInstance();
        this.usersRef=database.getReference("Users");
        this.songsRef=database.getReference("songs");
        users = new ArrayList<>();

        setUsersRef();
    }

    public int saveUser(User user){
        if(!emailExist(user.email)) {
            String id = usersRef.push().getKey();
            usersRef.child(id).setValue(user);
            return 1; // user added
        }
        return 0; // user exist
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public boolean emailExist(String email){
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getEmail().equals(email))
                return true;
        }
        return false;
    }

    public boolean userExist(User user){
        for(int i=0; i<users.size(); i++){
            if(users.get(i).equals(user))
                return true;
        }
        return false;
    }

    public void sentMassage(String email){

    }



    public void setUsersRef() {
        // Read from the database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if(!emailExist(user.getEmail()))
                        users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Failed to read value.", databaseError.toException());
            }
        });
    }

}
