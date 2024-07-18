package com.example.languageexchange;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView sourceLanguageTextView, targetLanguageTextView;
    private EditText wordEditText, translationEditText;
    private Button createButton;

    private String selectedSourceLanguage, selectedTargetLanguage;

    private static final String CHANNEL_ID = "language_exchange_channel";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NOTIFICATION_ENABLED = "notificationEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_language_pair);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();


        sourceLanguageTextView = findViewById(R.id.sourceLan);
        targetLanguageTextView = findViewById(R.id.targetLan);
        wordEditText = findViewById(R.id.word);
        translationEditText = findViewById(R.id.trans);
        createButton = findViewById(R.id.createBtn);


        createNotificationChannel();

        sourceLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog(true);
            }
        });

        targetLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog(false);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLanguagePair();
            }
        });
    }


    private void showLanguageDialog(final boolean isSourceLanguage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");


        final String[] languages = {"Amharic", "Oromo", "Tigregna", "Gurage","Wolaytta","Sidamo","Somali","Gamo"};

        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedLanguage = languages[which];
                if (isSourceLanguage) {
                    selectedSourceLanguage = selectedLanguage;
                    sourceLanguageTextView.setText(selectedSourceLanguage);
                } else {
                    selectedTargetLanguage = selectedLanguage;
                    targetLanguageTextView.setText(selectedTargetLanguage);
                }
            }
        });

        builder.show();
    }


    private void createLanguagePair() {
        String word = wordEditText.getText().toString().trim();
        String translation = translationEditText.getText().toString().trim();

        if (TextUtils.isEmpty(selectedSourceLanguage) || TextUtils.isEmpty(selectedTargetLanguage)
                || TextUtils.isEmpty(word) || TextUtils.isEmpty(translation)) {
            Toast.makeText(this, "Please fill all fields and select languages", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();


        String pairId = mDatabase.child("language_pairs").push().getKey();


        Map<String, Object> languagePair = new HashMap<>();
        languagePair.put("source_language", selectedSourceLanguage);
        languagePair.put("target_language", selectedTargetLanguage);
        languagePair.put("word", word);
        languagePair.put("translated_word", translation);
        languagePair.put("user_id", userId);


        mDatabase.child("language_pairs").child(pairId).setValue(languagePair)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendNotification();
                            Toast.makeText(CreateActivity.this, "Language pair created successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Finish activity after successful creation
                        } else {
                            Toast.makeText(CreateActivity.this, "Failed to create language pair", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void sendNotification() {

        boolean notificationEnabled = getNotificationStateFromPreferences();


        if (notificationEnabled) {
            Intent intent = new Intent(this, ExchangeHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_4) // Change to your app icon
                    .setContentTitle("Language Pair Created")
                    .setContentText("Your language pair was created successfully.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }


    private boolean getNotificationStateFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_ENABLED, true);
    }
}
