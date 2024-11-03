package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Manager extends AppCompatActivity {

    private DatabaseReference ordersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Initialize Firebase reference for orders
        ordersDatabase = FirebaseDatabase.getInstance().getReference("orders");
    }

    // Method called when Create Order button is clicked
    public void createOrder(View view) {

        Intent intent = new Intent(Manager.this, Order.class);
        startActivity(intent);
    }
}




