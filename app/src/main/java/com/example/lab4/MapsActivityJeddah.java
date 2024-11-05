package com.example.lab4;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityJeddah extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private Marker danubeMarker, pandaMarker, othaimMarker;
    private CheckBox checkBoxDanube, checkBoxPanda, checkBoxOthaim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_jeddah); // Use the layout directly

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Store locations
        LatLng danube = new LatLng(21.9225, 39.2061);
        LatLng panda = new LatLng(21.9151, 39.1901);
        LatLng othaim = new LatLng(21.9106, 39.2050);

        // Add markers for each store, initially hidden
        danubeMarker = mMap.addMarker(new MarkerOptions().position(danube).title("Danube"));
        pandaMarker = mMap.addMarker(new MarkerOptions().position(panda).title("Panda"));
        othaimMarker = mMap.addMarker(new MarkerOptions().position(othaim).title("Othaim"));


        // Move the camera to the area with stores
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danube, 13));
    }

    private void toggleMarker(Marker marker, boolean isVisible) {
        if (marker != null) {
            marker.setVisible(isVisible);
        }
    }


    }
