package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Manager extends AppCompatActivity {

    private BaseActivityHelper baseActivityHelper;
    private DatabaseReference ordersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Initialize Firebase reference for orders
        ordersDatabase = FirebaseDatabase.getInstance().getReference("orders");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        baseActivityHelper = new BaseActivityHelper(this, bottomNavigationView);
        baseActivityHelper.setupBottomNavigationView();
    }

    // Method called when Create Order button is clicked
    public void createOrder(View view) {

        Intent intent = new Intent(Manager.this, Order.class);
        startActivity(intent);
    }
}




