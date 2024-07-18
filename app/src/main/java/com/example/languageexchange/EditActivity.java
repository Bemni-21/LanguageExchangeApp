package com.example.languageexchange;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText;
    private Button saveButton;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        nameEditText = findViewById(R.id.name);
        usernameEditText = findViewById(R.id.username);
        saveButton = findViewById(R.id.saveButton);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    nameEditText.setText(name);
                    usernameEditText.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString().trim();
                String newUsername = usernameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newUsername)) {
                    Toast.makeText(EditActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                } else {
                    updateUser(newName, newUsername);
                }
            }
        });
    }

    private void updateUser(String newName, String newUsername) {
        userRef.child("name").setValue(newName);
        userRef.child("username").setValue(newUsername).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
