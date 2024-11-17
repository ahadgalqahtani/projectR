package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import java.util.UUID;

public class Manger extends AppCompatActivity {

    private DatabaseReference ordersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manger);

        // Initialize Firebase reference for orders
        ordersDatabase = FirebaseDatabase.getInstance().getReference("orders");
    }

    // Method called when Create Order button is clicked
    public void createOrder(View view) {

        Intent intent = new Intent(Manger.this, Order.class);
        startActivity(intent);
    }

}




