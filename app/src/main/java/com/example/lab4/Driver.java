package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Driver extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<OrderData> orderList;
    private DatabaseReference userReference, orderReference;
    private String driverId;
    public static String currentOrderId;
    private TextView welcomeMessage;
    private ImageButton logoutButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        // Initialize Firebase references
        userReference = FirebaseDatabase.getInstance().getReference("users");
        orderReference = FirebaseDatabase.getInstance().getReference("order");
        welcomeMessage = findViewById(R.id.welcomeMessage);

        // Initialize order list and RecyclerView
        orderList = new ArrayList<>();
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orderList, true);
        recyclerViewOrders.setAdapter(orderAdapter);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutMethod());

        // Set item click listener
        orderAdapter.setOnItemClickListener(order -> {
            if ("Completed".equals(order.getStatus())) {
                Toast.makeText(this, "This order is already completed", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentOrderId != null && !currentOrderId.equals(order.getOrderId())) {
                Toast.makeText(this, "Please complete the current order first", Toast.LENGTH_SHORT).show();
                return;
            }

            currentOrderId = order.getOrderId();
            Intent intent = new Intent(Driver.this, CurrentOrder.class);
            intent.putExtra("orderId", currentOrderId);
            startActivity(intent);
        });

        // Fetch driver's ID and orders
        fetchDriver();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the order list when returning to this screen
        if (driverId != null) {
            fetchAssignedOrders();
        }
    }

    private void fetchAssignedOrders() {
        if (driverId == null || driverId.isEmpty()) {
            Toast.makeText(this, "Driver ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        userReference.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String driverName = snapshot.child("name").getValue(String.class);

                    orderReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            orderList.clear();
                            for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                OrderData order = orderSnapshot.getValue(OrderData.class);
                                if (order != null && driverName != null &&
                                        driverName.equals(order.getAssignedDriver())) {
                                    orderList.add(order);
                                }
                            }

                            // Sort orders: In Progress first, then by delivery date
                            Collections.sort(orderList, (o1, o2) -> {
                                if (o1.getStatus().equals(o2.getStatus())) {
                                    return o1.getDeliveryDate().compareTo(o2.getDeliveryDate());
                                }
                                return "Completed".equals(o1.getStatus()) ? 1 : -1;
                            });

                            orderAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Driver.this, "Failed to fetch orders: " +
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Driver.this, "Failed to fetch driver data: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDriver() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        userReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    String driverName = snapshot.child("name").getValue(String.class);

                    if ("Driver".equals(role) && driverName != null) {
                        driverId = uid; // Use the current user's UID as the driver ID
                        welcomeMessage.setText("Welcome, " + driverName); // Display the driver's name
                        Log.d("Driver", "Driver Name fetched: " + driverName); // Debugging
                        fetchAssignedOrders(); // Fetch orders assigned to the driver
                    } else {
                        Toast.makeText(Driver.this, "You are not authorized to access this page.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(Driver.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Driver.this, "Failed to fetch user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void logoutMethod() {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }


}