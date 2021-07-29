package com.example.jigarpractical.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.example.jigarpractical.R;
import com.example.jigarpractical.databinding.ActivityMapBinding;
import com.example.jigarpractical.helper.GPSManager;
import com.example.jigarpractical.helper.GetNearbyPlacesData;
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
     private ActivityMapBinding binding;

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
        binding = DataBindingUtil.setContentView((Activity) mContext,R.layout.activity_map);

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

        binding.btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                String url = getUrl(latitude, longitude, "restaurant");
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
            }
        });

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyC_cPLCnLl7qBascLaESUiQDKhE06tKGT8");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


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
        locationRequest.setInterval(100000);
        locationRequest.setFastestInterval(100000);
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