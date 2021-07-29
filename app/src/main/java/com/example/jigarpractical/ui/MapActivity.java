package com.example.jigarpractical.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.jigarpractical.R;
import com.example.jigarpractical.helper.GPSManager;
import com.example.jigarpractical.helper.PermissionManager;
import com.example.jigarpractical.listener.GPSLocationListener;
import com.example.jigarpractical.listener.PermissionManagerListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        PermissionManagerListener,
        GPSLocationListener,
        ResultCallback<LocationSettingsResult> {
    private final String TAG = getClass().getSimpleName();
    private final Context mContext = this;
    // private ActivityMapBinding binding;

    // private MapView mapView;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GPSManager gpsManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LocationCallback locationCallback;
    //private LocationAddressResultReceiver addressResultReceiver;
    private LatLng currentLatlang;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        init();
    }

    private void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        if (mapFragment != null)
            setMap();


        gpsManager = new GPSManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.setSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION);


    }

    /*@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }*/

    /*@Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }*/

    @Override
    public void onStop() {
        super.onStop();
        mapFragment.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // mapView.onResume();
        if (mMap != null) {
            mMap.clear();
        }
        mMap = googleMap;

        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (currentLatlang != null) {
            mMap.addMarker(new
                    MarkerOptions().position(currentLatlang).title("map"));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    currentLatlang, 15);
            mMap.getMaxZoomLevel();
            mMap.animateCamera(location);
        }

    }

    private void setMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onGPSAlreadyEnabled() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);

                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();


                currentLatlang = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                if (mapFragment != null)
                    setMap();
            }
        };
        startLocationUpdates();

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onSinglePermissionGranted(String permissionName, String... endPoint) {
        switch (permissionName) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                gpsManager.start(this);
                break;
        }
    }

    @Override
    public void onMultiplePermissionGranted(ArrayList<String> permissionName, String... endPoint) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d(TAG, "LOCATION SUCESS");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d(TAG, "LOCATION RESOLUTION");
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(TAG, "LOCATION SETTINGS CHANGE");
                break;
        }
    }
}