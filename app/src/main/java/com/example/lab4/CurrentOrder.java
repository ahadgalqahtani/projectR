package com.example.lab4;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentOrder extends AppCompatActivity {

    private TextView orderDetailsTextView;
    private ToggleButton toggleStatus;
    private DatabaseReference orderReference;
    private String currentOrderId = "order123"; // Replace with dynamic order ID from app logic على اساس الاوردر اللي نختاره

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        // Initialize views
        orderDetailsTextView = findViewById(R.id.tv_order_details);
        toggleStatus = findViewById(R.id.toggle_status);

        // Firebase reference
        orderReference = FirebaseDatabase.getInstance().getReference("order").child(currentOrderId);

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
                        String details = "Order ID: " + order.getOrderId() +
                                "\nDelivery Date: " + order.getDeliveryDate() +
                                "\nCustomer: " + order.getCustomerDetails() +
                                "\nAmount: " + order.getOrderAmount() +
                                "\nWeight: " + order.getOrderWeight() +
                                "\nCity: " + order.getCity() +
                                "\nStatus: " + order.getStatus();
                        orderDetailsTextView.setText(details);

                        // Set the toggle state based on the status
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
                    } else {
                        Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
