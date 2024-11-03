package com.example.lab4;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private EditText editTextOrderId, editTextDeliveryDate, editTextCustomerDetails, editTextOrderAmount, editTextOrderWeight;
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
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Adjust the path as necessary
        driverList = new ArrayList<>();
        driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverList);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignDriver.setAdapter(driverAdapter);

        databaseReference.orderByChild("role").equalTo("Driver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverList.clear(); // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String driverName = snapshot.child("name").getValue(String.class); // Adjust based on your data structure
                    if (driverName != null) {
                        driverList.add(driverName); // Add driver name to the list
                    }
                }
                driverAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Order.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for the submit button
        buttonSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch user input
                String orderId = editTextOrderId.getText().toString();
                String deliveryDate = editTextDeliveryDate.getText().toString();
                String customerDetails = editTextCustomerDetails.getText().toString();
                String orderAmount = editTextOrderAmount.getText().toString();
                String orderWeight = editTextOrderWeight.getText().toString();
                String assignedDriver = spinnerAssignDriver.getSelectedItem().toString();

                // Perform validation (basic example)
                if (orderId.isEmpty() || deliveryDate.isEmpty() || customerDetails.isEmpty() ||
                        orderAmount.isEmpty() || orderWeight.isEmpty() || assignedDriver.isEmpty()) {
                    Toast.makeText(Order.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Process the order creation (you may want to send it to a database or another screen)
                Toast.makeText(Order.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();

                // Go back to the Supervisor dashboard


                finish();
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true; // Flag to track initial selection

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Skip the first (default) selection
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                // Retrieve the selected city
                String selectedCity = parent.getItemAtPosition(position).toString();

                // Start the appropriate activity based on selection
                if (selectedCity.equals("Jeddah")) {
                    Intent intent = new Intent(Order.this, MapsActivityJeddah.class);
                    startActivity(intent);
                } else if (selectedCity.equals("Makkah")) {
                    Intent intent = new Intent(Order.this, MapsActivityMakkah.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }
}
