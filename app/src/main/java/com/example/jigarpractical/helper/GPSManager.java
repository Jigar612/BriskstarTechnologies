package com.example.jigarpractical.helper;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;

import com.example.jigarpractical.listener.GPSLocationListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import static com.example.jigarpractical.utility.AppConstant.REQUEST_CODE_GPS_ENABLE;


public class GPSManager {

    private final Activity activity;
    private final SettingsClient mSettingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private final LocationManager locationManager;
    private LocationRequest locationRequest;
    //private LocationListener locationListener;

    public GPSManager(Activity activity) {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(activity);
    }

    public void start(GPSLocationListener gpsLocationListener) {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10)
                .setFastestInterval(10 / 2);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(task -> {

            try {

                LocationSettingsResponse response = task.getResult(ApiException.class);

                response.getLocationSettingsStates();

                if (gpsLocationListener != null)
                    gpsLocationListener.onGPSAlreadyEnabled();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(activity, REQUEST_CODE_GPS_ENABLE);
                        } catch (IntentSender.SendIntentException ex) {
                            Log.e("ERROR", ex.toString());
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
                Log.e("ERROR", e.toString());
            }
        });
    }

    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }
}

