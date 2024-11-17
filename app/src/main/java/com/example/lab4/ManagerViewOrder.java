package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;



public class ManagerViewOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_view_order);


                // Retrieve order details from intent
                Intent intent = getIntent();
                String orderId = intent.getStringExtra("orderId");
                String deliveryDate = intent.getStringExtra("deliveryDate");
                String customerDetails = intent.getStringExtra("customerDetails");
                String orderAmount = intent.getStringExtra("orderAmount");
                String orderWeight = intent.getStringExtra("orderWeight");
                String assignedDriver = intent.getStringExtra("assignedDriver");
                String city = intent.getStringExtra("city");

                // Populate views
                ((TextView) findViewById(R.id.textViewOrderId)).setText("Order ID: " + orderId);
                ((TextView) findViewById(R.id.textViewDeliveryDate)).setText("Delivery Date: " + deliveryDate);
                ((TextView) findViewById(R.id.textViewCustomerDetails)).setText("Customer Details: " + customerDetails);
                ((TextView) findViewById(R.id.textViewOrderAmount)).setText("Order Amount: $" + orderAmount);
                ((TextView) findViewById(R.id.textViewOrderWeight)).setText("Order Weight: " + orderWeight + " kg");
                ((TextView) findViewById(R.id.textViewDriver)).setText("Assigned Driver: " + assignedDriver);
                ((TextView) findViewById(R.id.textViewCity)).setText("City: " + city);


    }


        }





