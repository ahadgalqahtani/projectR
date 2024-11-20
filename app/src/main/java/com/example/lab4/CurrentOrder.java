package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentOrder extends AppCompatActivity {

    private TextView orderDetailsTextView;
    private ToggleButton toggleStatus;
    private DatabaseReference orderReference;
    private String currentOrderId;  // This will hold the order ID from DriverActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        // Get the orderId passed from DriverActivity
        currentOrderId = getIntent().getStringExtra("orderId");

        // Initialize views
        orderDetailsTextView = findViewById(R.id.tv_order_details);
        toggleStatus = findViewById(R.id.toggle_status);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // This sets the Toolbar as the action bar
        getSupportActionBar().setTitle("Current Order");

        // Firebase reference to the order with the dynamic orderId
        orderReference = FirebaseDatabase.getInstance().getReference("orders").child(currentOrderId);

        // Load order details
        loadOrderDetails();

        // Handle toggle button changes
        toggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newStatus = isChecked ? "Completed" : "In Progress";
            updateOrderStatus(newStatus);
        });
    }

    /**
     * Fetches the current order details from Firebase and updates the UI.
     */
    private void loadOrderDetails() {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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
                    }
                } else {
                    Toast.makeText(CurrentOrder.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CurrentOrder.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the order status in Firebase.
     *
     * @param status The new status to be set ("In Progress" or "Completed").
     */
    private void updateOrderStatus(String status) {
        orderReference.child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Order status updated to " + status, Toast.LENGTH_SHORT).show();
                        // Once the status is updated, return to the DriverActivity
                        Intent intent = new Intent(CurrentOrder.this, Driver.class);
                        startActivity(intent);
                        finish(); // Close the current activity (CurrentOrder)
                    } else {
                        Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}