package com.example.languageexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.MessageAdapterListener {

    private RecyclerView recyclerView;
    private EditText messageET;
    private ImageView sendBtn;
    private ImageView backIm;
    private List<MessageM> messageList;
    private MessageAdapter messagesAdapter;
    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        recyclerView = findViewById(R.id.recycler_view);
        messageET = findViewById(R.id.messageET);
        sendBtn = findViewById(R.id.sendBtn);
        backIm = findViewById(R.id.backIm);

        messagesRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms").child("room_id_1").child("messages");


        messageList = new ArrayList<>();
        messagesAdapter = new MessageAdapter(this, messageList, messagesRef, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageM message = snapshot.getValue(MessageM.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to read messages.", error.toException());
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        backIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageET.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String messageId = messagesRef.push().getKey();
            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timestamp = System.currentTimeMillis();

            MessageM message = new MessageM(messageId, senderId, messageText, timestamp);
            messagesRef.child(messageId).setValue(message)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messageET.setText("");
                            scrollToBottom();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ChatActivity", "Failed to send message.", e);

                        }
                    });
        }
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onDeleteMessage(MessageM message) {

        messageList.remove(message);
        messagesAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
    }
}
