package com.example.languageexchange;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExchangeHistoryActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private LanguageSelfAdapter adapter;
    private List<LanguageSelf> languageSelfList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_list);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        adapter = new LanguageSelfAdapter(this, R.layout.self_item, languageSelfList);
        listView.setAdapter(adapter);

        loadLanguageSelfs();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLanguageSelfs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterLanguageSelfs(newText);
                return true;
            }
        });
    }

    private void loadLanguageSelfs() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("language_pairs")
                .orderByChild("user_id")
                .equalTo(currentUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                languageSelfList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sourceLanguage = dataSnapshot.child("source_language").getValue(String.class);
                    String targetLanguage = dataSnapshot.child("target_language").getValue(String.class);
                    String word = dataSnapshot.child("word").getValue(String.class);
                    String translatedWord = dataSnapshot.child("translated_word").getValue(String.class);

                    LanguageSelf languageSelf = new LanguageSelf(sourceLanguage, targetLanguage, word, translatedWord);
                    languageSelfList.add(languageSelf);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExchangeHistoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterLanguageSelfs(String query) {
        List<LanguageSelf> filteredList = new ArrayList<>();
        for (LanguageSelf languageSelf : languageSelfList) {
            if (languageSelf.getWord().toLowerCase().contains(query.toLowerCase()) ||
                    languageSelf.getSourceLanguage().toLowerCase().contains(query.toLowerCase()) ||
                    languageSelf.getTargetLanguage().toLowerCase().contains(query.toLowerCase()) ||
                    languageSelf.getTranslatedWord().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(languageSelf);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Language Pair");
        builder.setMessage("Do you want to delete this language pair?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteLanguageSelf(position));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteLanguageSelf(int position) {
        LanguageSelf languageSelf = languageSelfList.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("language_pairs")
                .orderByChild("user_id")
                .equalTo(currentUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String word = dataSnapshot.child("word").getValue(String.class);
                    String sourceLanguage = dataSnapshot.child("source_language").getValue(String.class);
                    String targetLanguage = dataSnapshot.child("target_language").getValue(String.class);
                    String translatedWord = dataSnapshot.child("translated_word").getValue(String.class);

                    if (word != null && word.equals(languageSelf.getWord()) &&
                            sourceLanguage != null && sourceLanguage.equals(languageSelf.getSourceLanguage()) &&
                            targetLanguage != null && targetLanguage.equals(languageSelf.getTargetLanguage()) &&
                            translatedWord != null && translatedWord.equals(languageSelf.getTranslatedWord())) {
                        dataSnapshot.getRef().removeValue();
                        languageSelfList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ExchangeHistoryActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExchangeHistoryActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

