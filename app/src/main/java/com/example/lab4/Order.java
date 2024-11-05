package com.example.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.graphics.Color;
import android.util.TypedValue;
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

    private EditText editTextOrderId, editTextDeliveryDate, editTextCustomerDetails, editTextOrderAmount, editTextOrderWeight, editTextOrderSummary;
    ;
    private Spinner spinnerAssignDriver, spinnerCity;
    private Button buttonSubmitOrder;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> driverAdapter;
    private ArrayList<String> driverList;


    private static final String KEY_ORDER_ID = "orderId";
    private static final String KEY_DELIVERY_DATE = "deliveryDate";
    private static final String KEY_CUSTOMER_DETAILS = "customerDetails";
    private static final String KEY_ORDER_AMOUNT = "orderAmount";
    private static final String KEY_ORDER_WEIGHT = "orderWeight";
    private static final String KEY_ORDER_SUMMARY = "orderSummary";
    private static final String KEY_SELECTED_DRIVER = "selectedDriver";
    private static final String KEY_SELECTED_CITY = "selectedCity";
    private String selectedStore = "";

    private TextWatcher formWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateOrderSummary();
        }
    };


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
        editTextOrderSummary = findViewById(R.id.editTextOrderSummary);


        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Adjust the path as necessary
        driverList = new ArrayList<>();
        driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverList);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignDriver.setAdapter(driverAdapter);

        editTextOrderId.addTextChangedListener(formWatcher);
        editTextDeliveryDate.addTextChangedListener(formWatcher);
        editTextCustomerDetails.addTextChangedListener(formWatcher);
        editTextOrderAmount.addTextChangedListener(formWatcher);
        editTextOrderWeight.addTextChangedListener(formWatcher);

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

                updateOrderSummary();
                // Go back to the Supervisor dashboard
                finish();
            }
        });

        spinnerAssignDriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateOrderSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
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
                saveFormState();

                // Start the appropriate activity based on selection
                if (selectedCity.equals("Jeddah")) {
                    Intent intent = new Intent(Order.this, MapsActivityJeddah.class);
                    startActivityForResult(intent, 1);
                } else if (selectedCity.equals("Makkah")) {
                    Intent intent = new Intent(Order.this, MapsActivityMakkah.class);
                    startActivityForResult(intent, 1);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void saveFormState() {
        SharedPreferences prefs = getSharedPreferences("OrderFormData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        editor.putString(KEY_ORDER_ID, editTextOrderId.getText().toString());
        editor.putString(KEY_DELIVERY_DATE, editTextDeliveryDate.getText().toString());
        editor.putString(KEY_CUSTOMER_DETAILS, editTextCustomerDetails.getText().toString());
        editor.putString(KEY_ORDER_AMOUNT, editTextOrderAmount.getText().toString());
        editor.putString(KEY_ORDER_WEIGHT, editTextOrderWeight.getText().toString());
        editor.putString(KEY_ORDER_SUMMARY, editTextOrderSummary.getText().toString());
        editor.putString(KEY_ORDER_SUMMARY, editTextOrderSummary.getText().toString());
        editor.putString("SELECTED_STORE", selectedStore);

        if (spinnerAssignDriver.getSelectedItem() != null) {
            editor.putString(KEY_SELECTED_DRIVER, spinnerAssignDriver.getSelectedItem().toString());
        }
        if (spinnerCity.getSelectedItem() != null) {
            editor.putString(KEY_SELECTED_CITY, spinnerCity.getSelectedItem().toString());
        }


        editor.apply();
    }

    private void restoreFormState() {
        SharedPreferences prefs = getSharedPreferences("OrderFormData", MODE_PRIVATE);

        editTextOrderId.setText(prefs.getString(KEY_ORDER_ID, ""));
        editTextDeliveryDate.setText(prefs.getString(KEY_DELIVERY_DATE, ""));
        editTextCustomerDetails.setText(prefs.getString(KEY_CUSTOMER_DETAILS, ""));
        editTextOrderAmount.setText(prefs.getString(KEY_ORDER_AMOUNT, ""));
        editTextOrderWeight.setText(prefs.getString(KEY_ORDER_WEIGHT, ""));
        editTextOrderSummary.setText(prefs.getString(KEY_ORDER_SUMMARY, ""));

        // Restore spinner selections after adapter is populated
        final String savedDriver = prefs.getString(KEY_SELECTED_DRIVER, "");
        final String savedCity = prefs.getString(KEY_SELECTED_CITY, "");

        if (!savedDriver.isEmpty() && driverAdapter != null) {
            int driverPosition = driverAdapter.getPosition(savedDriver);
            if (driverPosition >= 0) {
                spinnerAssignDriver.setSelection(driverPosition);
            }
        }

        if (!savedCity.isEmpty()) {
            ArrayAdapter<CharSequence> cityAdapter = (ArrayAdapter<CharSequence>) spinnerCity.getAdapter();
            int cityPosition = cityAdapter.getPosition(savedCity);
            if (cityPosition >= 0) {
                spinnerCity.setSelection(cityPosition);
            }
        }

        selectedStore = prefs.getString("SELECTED_STORE", "");
        updateOrderSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreFormState();  // Restore data when activity resumes
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveFormState();  // Save data when activity might be destroyed
    }

    private void saveOrder() {
        // Your existing order saving logic here
        Toast.makeText(Order.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();

        // Clear the saved form data after successful submission
        SharedPreferences prefs = getSharedPreferences("OrderFormData", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedStore = data.getStringExtra("SELECTED_STORE");
            updateOrderSummary();
        }
    }

    private void updateOrderSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Order Summary:\n\n");

        // Get all the values
        String orderId = editTextOrderId.getText().toString().trim();
        String deliveryDate = editTextDeliveryDate.getText().toString().trim();
        String customerDetails = editTextCustomerDetails.getText().toString().trim();
        String orderAmount = editTextOrderAmount.getText().toString().trim();
        String orderWeight = editTextOrderWeight.getText().toString().trim();
        String assignedDriver = spinnerAssignDriver.getSelectedItem() != null ?
                spinnerAssignDriver.getSelectedItem().toString() : "";
        String selectedCity = spinnerCity.getSelectedItem() != null ?
                spinnerCity.getSelectedItem().toString() : "";
        String storeName = selectedStore;

        // Append each field with proper formatting
        if (!orderId.isEmpty()) {
            summary.append("Order ID: ").append(orderId).append("\n\n");
        }

        if (!deliveryDate.isEmpty()) {
            summary.append("Delivery Date: ").append(deliveryDate).append("\n\n");
        }

        if (!customerDetails.isEmpty()) {
            summary.append("Customer Details: ").append(customerDetails).append("\n\n");
        }

        if (!orderAmount.isEmpty()) {
            summary.append("Order Amount: ").append(orderAmount).append(" SAR\n\n");
        }

        if (!orderWeight.isEmpty()) {
            summary.append("Order Weight: ").append(orderWeight).append(" KG\n\n");
        }

        if (!assignedDriver.isEmpty()) {
            summary.append("Assigned Driver: ").append(assignedDriver).append("\n\n");
        }

        if (!selectedCity.isEmpty()) {
            summary.append("Delivery City: ").append(selectedCity).append("\n\n");
        }

        editTextOrderSummary.setFocusable(false);
        editTextOrderSummary.setClickable(false);

        // Set text color to red
        editTextOrderSummary.setTextColor(Color.RED);

        // Set text size to small (12sp)
        editTextOrderSummary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Set the summary text
        editTextOrderSummary.setText(summary.toString());
    }
}