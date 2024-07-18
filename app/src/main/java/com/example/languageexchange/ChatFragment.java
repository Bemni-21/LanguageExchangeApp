package com.example.languageexchange;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private ListView userListView;
    private UserrAdapter userrAdapter;
    private ArrayList<Userr> userList;
    private ArrayList<Userr> filteredUserList;

    private DatabaseReference usersRef;

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_user, container, false);


        userListView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        CardView joinChatRoomCard = view.findViewById(R.id.joinChatRoomCard);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");


        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userrAdapter = new UserrAdapter(getActivity(), userList);
        userListView.setAdapter(userrAdapter);


        fetchUsers();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


        joinChatRoomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });

        return view;
    }

    private void fetchUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Userr user = snapshot.getValue(Userr.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter(String query) {
        filteredUserList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredUserList.addAll(userList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Userr user : userList) {
                if (user.getName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getUsername().toLowerCase().contains(lowerCaseQuery)) {
                    filteredUserList.add(user);
                }
            }
        }
        userrAdapter = new UserrAdapter(getActivity(), filteredUserList);
        userListView.setAdapter(userrAdapter);
    }
}
