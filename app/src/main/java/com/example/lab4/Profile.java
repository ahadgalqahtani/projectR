package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity  {

    private TextView profileName, profileEmail, profileEmployeeID, profilePhoneNumber, profileRole;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileEmployeeID = findViewById(R.id.profileEmployeeID);
        profilePhoneNumber = findViewById(R.id.profilePhoneNumber);
        profileRole = findViewById(R.id.profileRole);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userDatabase.child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    User userInfo = snapshot.getValue(User.class);
                    if (userInfo != null) {
                        profileName.setText("Name: " + userInfo.getName());
                        profileEmail.setText("Email: " + userInfo.getEmail());
                        profileEmployeeID.setText("Employee ID: " + userInfo.getEmployeeID());
                        profilePhoneNumber.setText("Phone: " + userInfo.getPhoneNumber());
                        profileRole.setText("Role: " + userInfo.getRole());
                    }
                } else {
                    // Handle the case where the user data doesn't exist
                    profileName.setText("Name: Unknown");
                }
            }).addOnFailureListener(e -> {
                // Handle errors when reading the database
                profileName.setText("Failed to load profile");
            });
        }
    }

    public void logoutMethod(View view) {
        mAuth.signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
