package com.example.languageexchange;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<MessageM> messageList;
    private DatabaseReference messagesRef;
    private DatabaseReference usersRef;
    private Context context;
    private MessageAdapterListener messageAdapterListener;

    public MessageAdapter(Context context, List<MessageM> messageList, DatabaseReference messagesRef, MessageAdapterListener listener) {
        this.context = context;
        this.messageList = messageList;
        this.messagesRef = messagesRef;
        this.messageAdapterListener = listener;
        this.usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_layout, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        MessageM message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
        holder.timestampText.setText(formatTimestamp(message.getTimestamp()));


        usersRef.child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profile_picture").getValue(String.class);


                    if (holder.usernameText != null) {
                        holder.usernameText.setText(username);
                    }
                    if (holder.profileImage != null && profileImageUrl != null) {

                        Picasso.get().load(profileImageUrl).into(holder.profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageAdapter", "Failed to fetch sender details: " + databaseError.getMessage());
            }
        });


        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (message.getSenderId().equals(currentUserId)) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteConfirmationDialog(message);
                    return true;
                }
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageM message = messageList.get(position);
        if (message != null && message.getSenderId() != null) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (message.getSenderId().equals(currentUserId)) {
                return VIEW_TYPE_SENDER;
            } else {
                return VIEW_TYPE_RECEIVER;
            }
        }
        return VIEW_TYPE_SENDER;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, messageText, timestampText;
        CircleImageView profileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username);
            messageText = itemView.findViewById(R.id.message);
            timestampText = itemView.findViewById(R.id.time);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }

    private String formatTimestamp(long timestamp) {

        return DateFormat.getDateTimeInstance().format(new Date(timestamp));
    }

    private void showDeleteConfirmationDialog(MessageM message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteMessage(message);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteMessage(MessageM message) {
        String messageId = message.getMessageId();
        messagesRef.child(messageId).removeValue()
                .addOnSuccessListener(aVoid -> {

                    messageAdapterListener.onDeleteMessage(message);
                })
                .addOnFailureListener(e -> {

                    Log.e("MessageAdapter", "Failed to delete message: " + e.getMessage());
                });
    }

    public interface MessageAdapterListener {
        void onDeleteMessage(MessageM message);
    }
}
