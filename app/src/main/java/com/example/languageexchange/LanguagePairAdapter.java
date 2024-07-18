package com.example.languageexchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class LanguagePairAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> languagePairs;
    private String currentUserId;

    public LanguagePairAdapter(Context context, List<Map<String, String>> languagePairs) {
        this.context = context;
        this.languagePairs = languagePairs;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getCount() {
        return languagePairs.size();
    }

    @Override
    public Object getItem(int position) {
        return languagePairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false);
        }

        TextView sourceLanguageTv = convertView.findViewById(R.id.sourceLanguageTv);
        TextView wordTv = convertView.findViewById(R.id.wordTv);
        TextView targetLanguageTv = convertView.findViewById(R.id.targetLanguageTv);
        TextView translationTv = convertView.findViewById(R.id.translationTv);
        TextView userTv = convertView.findViewById(R.id.userTv);

        Map<String, String> languagePair = languagePairs.get(position);

        sourceLanguageTv.setText(languagePair.get("source_language") + ":");
        wordTv.setText(languagePair.get("word"));
        targetLanguageTv.setText(languagePair.get("target_language") + ":");
        translationTv.setText(languagePair.get("translated_word"));

        String userId = languagePair.get("user_id");
        if (userId.equals(currentUserId)) {
            userTv.setText("By you");
        } else {
            fetchUserName(userId, userTv);
        }

        return convertView;
    }

    private void fetchUserName(String userId, TextView userTv) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("username").getValue(String.class);
                        if (username != null) {
                            userTv.setText("By " + username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userTv.setText("By Unknown");
                    }
                });
    }
}
