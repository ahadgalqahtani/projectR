package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class Main2 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText et_email, et_password;
//raghad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.email_Login);
        et_password = findViewById(R.id.password_Login);
    }

    public void registerPage(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void loginMethod(View view) {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide a valid Email");
            et_email.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "CORRECT!", Toast.LENGTH_LONG).show();
                        // You can redirect to another page here, e.g., startActivity(new Intent(this, MainPage.class));
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().toString()
                                : "Login failed.";
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}