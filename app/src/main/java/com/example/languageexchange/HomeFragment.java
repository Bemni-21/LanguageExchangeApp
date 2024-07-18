package com.example.languageexchange;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ListView listView;
    private LanguagePairAdapter adapter;
    private List<Map<String, String>> languagePairs;
    private List<Map<String, String>> filteredLanguagePairs;

    private DatabaseReference mDatabase;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        listView = view.findViewById(R.id.listView);
        searchView = view.findViewById(R.id.searchView);
        languagePairs = new ArrayList<>();
        filteredLanguagePairs = new ArrayList<>();
        adapter = new LanguagePairAdapter(getContext(), filteredLanguagePairs);
        listView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchLanguagePairs();

        setupSearchView();

        return view;
    }

    private void fetchLanguagePairs() {
        DatabaseReference languagePairsRef = mDatabase.child("language_pairs");

        languagePairsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                languagePairs.clear();
                for (DataSnapshot pairSnapshot : snapshot.getChildren()) {
                    Map<String, String> languagePair = new HashMap<>();
                    languagePair.put("source_language", pairSnapshot.child("source_language").getValue(String.class));
                    languagePair.put("target_language", pairSnapshot.child("target_language").getValue(String.class));
                    languagePair.put("word", pairSnapshot.child("word").getValue(String.class));
                    languagePair.put("translated_word", pairSnapshot.child("translated_word").getValue(String.class));
                    languagePair.put("user_id", pairSnapshot.child("user_id").getValue(String.class));
                    languagePairs.add(languagePair);
                }
                filterLanguagePairs(searchView.getQuery().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load language pairs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLanguagePairs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterLanguagePairs(newText);
                return true;
            }
        });
    }

    private void filterLanguagePairs(String query) {
        filteredLanguagePairs.clear();
        if (TextUtils.isEmpty(query)) {
            filteredLanguagePairs.addAll(languagePairs);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Map<String, String> languagePair : languagePairs) {
                String word = languagePair.get("word").toLowerCase();
                if (word.contains(lowerCaseQuery)) {
                    filteredLanguagePairs.add(languagePair);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
