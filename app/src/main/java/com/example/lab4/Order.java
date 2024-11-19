package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Order extends AppCompatActivity {

    // Declare the private fields for the views
    private EditText editTextOrderId, editTextDeliveryDate, editTextCustomerDetails,
            editTextOrderAmount, editTextOrderWeight;
    private Spinner spinnerAssignDriver, spinnerCity;
    private Button buttonSubmitOrder;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> driverAdapter;
    private ArrayList<String> driverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize the views
        editTextOrderId = findViewById(R.id.editTextOrderId);
        editTextDeliveryDate = findViewById(R.id.editTextDeliveryDate);
        editTextCustomerDetails = findViewById(R.id.editTextCustomerDetails);
        editTextOrderAmount = findViewById(R.id.editTextOrderAmount);
        editTextOrderWeight = findViewById(R.id.editTextOrderWeight);
        spinnerAssignDriver = findViewById(R.id.AssignDriverSpinner);
        spinnerCity = findViewById(R.id.spinnerCity);
        buttonSubmitOrder = findViewById(R.id.buttonSubmitOrder);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        driverList = new ArrayList<>();
        driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverList);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignDriver.setAdapter(driverAdapter);

        // Load driver names from Firebase
        databaseReference.orderByChild("role").equalTo("Driver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String driverName = snapshot.child("name").getValue(String.class);
                    if (driverName != null) {
                        driverList.add(driverName);
                    }
                }
                driverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Order.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for the submit button
        buttonSubmitOrder.setOnClickListener(v -> {
            // Fetch user input
            String orderId = editTextOrderId.getText().toString();
            String deliveryDate = editTextDeliveryDate.getText().toString();
            String customerDetails = editTextCustomerDetails.getText().toString();
            String orderAmount = editTextOrderAmount.getText().toString();
            String orderWeight = editTextOrderWeight.getText().toString();
            String assignedDriver = spinnerAssignDriver.getSelectedItem() != null
                    ? spinnerAssignDriver.getSelectedItem().toString() : "";
            String city = spinnerCity.getSelectedItem() != null
                    ? spinnerCity.getSelectedItem().toString() : "";

            // Validate inputs
            if (orderId.isEmpty() || deliveryDate.isEmpty() || customerDetails.isEmpty() ||
                    orderAmount.isEmpty() || orderWeight.isEmpty() || assignedDriver.isEmpty() || city.isEmpty()) {
                Toast.makeText(Order.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = "in progress";
            // Create an OrderData object
            OrderData orderData = new OrderData(orderId, deliveryDate, customerDetails,
                    orderAmount, orderWeight,
                    assignedDriver, city, status);

            // Save the order to the database
            saveOrderToDatabase(orderId, orderData);
        });

        // Spinner listeners
        spinnerAssignDriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                String selectedCity = parent.getItemAtPosition(position).toString();
                Intent intent;
                if (selectedCity.equals("Jeddah")) {
                    intent = new Intent(Order.this, MapsActivityJeddah.class);
                } else if (selectedCity.equals("Makkah")) {
                    intent = new Intent(Order.this, MapsActivityMakkah.class);
                } else {
                    return;
                }
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void saveOrderToDatabase(String orderId, OrderData orderData) {
        // Get reference to the "order" node in Firebase
        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("order");

        // Save the order data to Firebase under the orderId
        orderReference.child(orderId).setValue(orderData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Show a success message
                        Toast.makeText(Order.this, "Order saved successfully!", Toast.LENGTH_SHORT).show();

                        // Create an intent to open ViewOrder activity
                        Intent intent = new Intent(Order.this, ViewOrder.class); // Updated class name
                        intent.putExtra("orderId", orderData.getOrderId());
                        intent.putExtra("deliveryDate", orderData.getDeliveryDate());
                        intent.putExtra("customerDetails", orderData.getCustomerDetails());
                        intent.putExtra("orderAmount", orderData.getOrderAmount());
                        intent.putExtra("orderWeight", orderData.getOrderWeight());
                        intent.putExtra("assignedDriver", orderData.getAssignedDriver());
                        intent.putExtra("city", orderData.getCity());

                        // Start the ViewOrder activity
                        startActivity(intent);

                        // Optionally, close the current activity (Order activity)
                        finish();
                    } else {
                        // Show a failure message
                        Toast.makeText(Order.this, "Failed to save order", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
