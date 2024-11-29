package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentOrder extends AppCompatActivity implements OnMapReadyCallback {
    private TextView orderDetailsTextView;
    private ToggleButton toggleStatus;
    private DatabaseReference orderReference;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        // Get the orderId passed from DriverActivity
        String currentOrderId = getIntent().getStringExtra("orderId");
        if (currentOrderId == null) {
            Toast.makeText(this, "No order ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }

        // Initialize views
        orderDetailsTextView = findViewById(R.id.tv_order_details);
        toggleStatus = findViewById(R.id.toggle_status);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Current Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fix: Update Firebase reference to match Driver activity
        orderReference = FirebaseDatabase.getInstance().getReference("order").child(currentOrderId);

        // Load order details
        loadOrderDetails();

        // Handle toggle button changes
        toggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show confirmation dialog before marking as completed
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Completion")
                        .setMessage("Are you sure you want to mark this order as completed?")
                        .setPositiveButton("Yes", (dialog, which) -> updateOrderStatus("Completed"))
                        .setNegativeButton("No", (dialog, which) -> toggleStatus.setChecked(false))
                        .show();
            } else {
                updateOrderStatus("In Progress");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadOrderDetails() {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    OrderData order = snapshot.getValue(OrderData.class);
                    if (order != null) {
                        // Display order details
                        String details = "Order ID: " + order.getOrderId() +
                                "\nDelivery Date: " + order.getDeliveryDate() +
                                "\nCustomer: " + order.getCustomerDetails() +
                                "\nAmount: " + order.getOrderAmount() +
                                "\nWeight: " + order.getOrderWeight() +
                                "\nCity: " + order.getCity() +
                                "\nStatus: " + order.getStatus();
                        orderDetailsTextView.setText(details);

                        // Set the toggle button state based on the order status
                        toggleStatus.setChecked("Completed".equals(order.getStatus()));

                        // Disable toggle if order is already completed
                        toggleStatus.setEnabled(!"Completed".equals(order.getStatus()));
                    }
                } else {
                    Toast.makeText(CurrentOrder.this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CurrentOrder.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(String status) {
        orderReference.child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if ("Completed".equals(status)) {
                            Toast.makeText(this, "Order Completed", Toast.LENGTH_SHORT).show();

                            // Clear the static currentOrderId in Driver class
                            Driver.currentOrderId = null;

                            // Add a small delay before returning
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(CurrentOrder.this, Driver.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }, 1000);
                        } else {
                            Toast.makeText(this, "Order status updated to " + status, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                        toggleStatus.setChecked(!toggleStatus.isChecked()); // Revert toggle state
                    }
                });
    }

    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Example: Set a marker based on sample coordinates
        LatLng sampleLocation = new LatLng(37.7749, -122.4194); // Replace with actual order location
        googleMap.addMarker(new MarkerOptions().position(sampleLocation).title("Delivery Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sampleLocation, 12));
    }
}
