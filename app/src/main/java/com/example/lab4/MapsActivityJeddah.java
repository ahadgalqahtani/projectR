package com.example.lab4;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivityJeddah extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Store markers to control adding and removing
    private Map<String, Marker> storeMarkers = new HashMap<>();

    // Define store locations
    private final LatLng danubeLocation = new LatLng(21.622786775243604, 39.15280598143593); // Example coordinates for Danube
    private final LatLng pandaLocation = new LatLng(21.58425198291532, 39.23568753044635);  // Example coordinates for Panda
    private final LatLng othaimLocation = new LatLng(21.88442978294658, 39.199336784144926); // Example coordinates for Othaim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Make sure this matches your XML filename

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Request location permissions if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set up CheckBox listeners
        setupCheckBoxListeners();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set map settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Center the map on Makkah
        LatLng makkah = new LatLng(21.3891, 39.8579);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(makkah, 12));
    }

    // Handle the results of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, initialize the map again
                if (mMap != null) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Set up listeners for each CheckBox
    private void setupCheckBoxListeners() {
        CheckBox store1 = findViewById(R.id.store1);
        CheckBox store2 = findViewById(R.id.store2);
        CheckBox store3 = findViewById(R.id.store3);

        store1.setOnCheckedChangeListener((buttonView, isChecked) -> handleMarker(isChecked, "Danube", danubeLocation));
        store2.setOnCheckedChangeListener((buttonView, isChecked) -> handleMarker(isChecked, "Panda", pandaLocation));
        store3.setOnCheckedChangeListener((buttonView, isChecked) -> handleMarker(isChecked, "Othaim", othaimLocation));
    }

    // Method to add or remove marker based on CheckBox state
    private void handleMarker(boolean shouldShow, String storeName, LatLng location) {
        if (shouldShow) {
            // Add marker if checked
            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(storeName));
            storeMarkers.put(storeName, marker);
        } else {
            // Remove marker if unchecked
            Marker marker = storeMarkers.get(storeName);
            if (marker != null) {
                marker.remove();
                storeMarkers.remove(storeName);
            }
        }
    }

    // Method triggered when the "Done" button is clicked
    public void onDoneButtonClick(View view) {
        StringBuilder selectedStores = new StringBuilder("Selected Stores:\n");

        if (((CheckBox) findViewById(R.id.store1)).isChecked()) selectedStores.append("Danube\n");
        if (((CheckBox) findViewById(R.id.store2)).isChecked()) selectedStores.append("Panda\n");
        if (((CheckBox) findViewById(R.id.store3)).isChecked()) selectedStores.append("Othaim\n");

        if (selectedStores.toString().equals("Selected Stores:\n")) {
            Toast.makeText(this, "No stores selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, selectedStores.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
