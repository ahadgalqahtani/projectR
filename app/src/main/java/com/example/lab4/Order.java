package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Order extends AppCompatActivity {

    private EditText editTextOrderId, editTextDeliveryDate, editTextCustomerDetails, editTextOrderAmount, editTextOrderWeight;
    private Spinner spinnerOrderType;
    private Button buttonSubmitOrder;

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
        spinnerOrderType = findViewById(R.id.spinnerOrderType);
        buttonSubmitOrder = findViewById(R.id.buttonSubmitOrder);

        buttonSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch user input
                String orderId = editTextOrderId.getText().toString();
                String deliveryDate = editTextDeliveryDate.getText().toString();
                String customerDetails = editTextCustomerDetails.getText().toString();
                String orderAmount = editTextOrderAmount.getText().toString();
                String orderWeight = editTextOrderWeight.getText().toString();
                String orderType = spinnerOrderType.getSelectedItem().toString();

                // Perform validation (basic example)
                if (orderId.isEmpty() || deliveryDate.isEmpty() || customerDetails.isEmpty() ||
                        orderAmount.isEmpty() || orderWeight.isEmpty()) {
                    Toast.makeText(Order.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Process the order creation (you may want to send it to a database or another screen)
                Toast.makeText(Order.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();

                // Go back to the Supervisor dashboard
                finish();
            }
        });
    }
}
