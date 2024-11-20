package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Manager extends AppCompatActivity {

    private TextView profileName;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private BaseActivityHelper baseActivityHelper;
    private DatabaseReference ordersDatabase;
    private ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI components
        profileName = findViewById(R.id.welcomeMessage); // Match this ID with your XML layout
        logoutButton = findViewById(R.id.logoutButton);

        // Load user profile data
        loadUserProfile();

        // Set logout button functionality
        logoutButton.setOnClickListener(v -> logoutMethod());

        // Initialize Firebase reference for orders
        ordersDatabase = FirebaseDatabase.getInstance().getReference("orders");

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        baseActivityHelper = new BaseActivityHelper(this, bottomNavigationView);
        baseActivityHelper.setupBottomNavigationView();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Log.d("FirebaseDebug", "User ID: " + userId); // Debugging

            userDatabase.child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    User userInfo = snapshot.getValue(User.class);
                    if (userInfo != null) {
                        profileName.setText("Welcome, " + userInfo.getName());
                        Log.d("FirebaseDebug", "User Name: " + userInfo.getName()); // Debugging
                    } else {
                        profileName.setText("Name: Data Missing");
                        Log.e("FirebaseDebug", "User data is null");
                    }
                } else {
                    profileName.setText("Name: Unknown");
                    Log.e("FirebaseDebug", "Snapshot does not exist for UID: " + userId);
                }
            }).addOnFailureListener(e -> {
                profileName.setText("Failed to load profile");
                Log.e("FirebaseDebug", "Error fetching data: " + e.getMessage());
            });
        } else {
            profileName.setText("User not logged in");
            Log.e("FirebaseDebug", "No user is logged in");
        }
    }

    public void createOrder(View view) {
        Intent intent = new Intent(Manager.this, Order.class);
        startActivity(intent);
    }

    public void logoutMethod() {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
