package com.example.languageexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private CircleImageView profileCircleImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView deleteTextView;
    private TextView ruleTextView;
    private TextView questionTextView;
    private TextView logoutTextView;
    private Switch notificationsSwitch;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NOTIFICATION_ENABLED = "notificationEnabled";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        profileCircleImageView = view.findViewById(R.id.profileCircleImageView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.email);
        deleteTextView = view.findViewById(R.id.delete);
        ruleTextView = view.findViewById(R.id.rule);
        questionTextView = view.findViewById(R.id.question);
        logoutTextView = view.findViewById(R.id.logout);
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());


        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, getActivity().MODE_PRIVATE);

        loadUserProfile();


        boolean notificationEnabled = sharedPreferences.getBoolean(NOTIFICATION_ENABLED, true);
        notificationsSwitch.setChecked(notificationEnabled);


        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateNotificationState(isChecked);
        });

        // Set onClickListeners
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        ruleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RuleActivity.class));
            }
        });

        questionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QuestionActivity.class));
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        return view;
    }

    private void loadUserProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String profileImageUrl = snapshot.child("profile_picture").getValue(String.class);

                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(getActivity())
                                .load(profileImageUrl)
                                .into(profileCircleImageView);
                    } else {
                        profileCircleImageView.setImageResource(R.drawable.hack);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNotificationState(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_ENABLED, isChecked);
        editor.apply();


        if (isChecked) {
            Toast.makeText(getActivity(), "Notifications turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Notifications turned off", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUser.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getActivity(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to login or home screen
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Failed to delete user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Redirect to login screen
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
