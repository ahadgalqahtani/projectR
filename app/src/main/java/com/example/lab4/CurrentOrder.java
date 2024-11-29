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

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
import java.util.Objects;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class CurrentOrder extends AppCompatActivity implements OnMapReadyCallback {
    private TextView orderDetailsTextView;
    private ToggleButton toggleStatus;
    private DatabaseReference orderReference;
    private GoogleMap googleMap;
    private String orderCity; // To store the city from the order details
    private boolean isMapReady = false; // Flag to check if map is ready
    private boolean isCityLoaded = false; // Flag to check if city data is loaded

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

        // Firebase reference
        orderReference = FirebaseDatabase.getInstance().getReference("order").child(currentOrderId);

        // Load order details
        loadOrderDetails();

        // Setup Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle toggle button changes
        toggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
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
                                "\nStores: " + order.getStores() +
                                "\nStatus: " + order.getStatus();
                        orderDetailsTextView.setText(details);

                        // Save the city and stores
                        orderCity = order.getCity();
                        List<String> stores = order.getStores();
                        isCityLoaded = true;

                        // Load store locations
                        loadStoreLocations(orderCity, stores);

                        // Center map on city if needed
                        centerMapOnCity();
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

    private void loadStoreLocations(String city, List<String> stores) {
        if (city == null || stores == null || stores.isEmpty()) {
            Toast.makeText(this, "City or stores information is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference locationsReference = FirebaseDatabase.getInstance().getReference("locations").child(city);

        locationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (String store : stores) {
                        String storeKey = store.toLowerCase(); // Convert store name to lowercase
                        DataSnapshot storeSnapshot = snapshot.child(storeKey);
                        if (storeSnapshot.exists()) {
                            Double latitude = storeSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = storeSnapshot.child("longitude").getValue(Double.class);

                            if (latitude != null && longitude != null) {
                                LatLng storeLocation = new LatLng(latitude, longitude);
                                // Add marker for the store on the map
                                if (googleMap != null) {
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(storeLocation)
                                            .title(store));
                                }
                            } else {
                                Toast.makeText(CurrentOrder.this, "Coordinates missing for store: " + store, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CurrentOrder.this, "Store not found in database: " + store, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(CurrentOrder.this, "City not found in locations database: " + city, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CurrentOrder.this, "Failed to load store locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(String status) {
        orderReference.child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if ("Completed".equals(status)) {
                            Toast.makeText(this, "Order Completed", Toast.LENGTH_SHORT).show();
                            Driver.currentOrderId = null;

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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        isMapReady = true;
        centerMapOnCity();
    }

    private void centerMapOnCity() {
        if (isMapReady && isCityLoaded) {
            LatLng targetLocation;
            switch (orderCity.toLowerCase()) {
                case "jeddah":
                    targetLocation = new LatLng(21.4858, 39.1925);
                    break;
                case "makkah":
                    targetLocation = new LatLng(21.3891, 39.8579);
                    break;
                default:
                    Toast.makeText(this, "City not recognized: " + orderCity, Toast.LENGTH_SHORT).show();
                    return;
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 8));
        }
    }
}